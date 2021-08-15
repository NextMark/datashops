package com.bigdata.datashops.service.graph;

import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.job.RelationshipEdge;
import com.bigdata.datashops.service.BaseService;
import com.google.common.collect.Maps;

@Service
public class GraphService extends BaseService {

    public void buildGraph(DirectedWeightedPseudograph<String, RelationshipEdge> dag, String maskId) {
        List<JobDependency> upstream = jobDependencyService.findByTargetId(maskId);
        List<JobDependency> downstream = jobDependencyService.findBySourceId(maskId);
        upstream.addAll(downstream);

        Job job = jobService.getOnlineJobByMaskId(maskId);
        Map<String, Object> extra = Maps.newHashMap();
        extra.put("period", job.getSchedulingPeriod());
        extra.put("owner", job.getOwner());
        Vertex vertex = new Vertex(maskId, job.getName(), job.getType(), JSONUtils.toJsonString(extra));
        String vertexStr = JSONUtils.toJsonString(vertex);
        dag.addVertex(vertexStr);

        for (JobDependency dep : upstream) {
            // maskId是target
            boolean isTarget = false;
            String otherMaskId;
            String sourceId = dep.getSourceId();
            String targetId = dep.getTargetId();

            if (sourceId.equals(maskId)) {
                otherMaskId = targetId;
            } else {
                isTarget = true;
                otherMaskId = sourceId;
            }
            Job otherDepJob = jobService.getOnlineJobByMaskId(otherMaskId);
            Map<String, Object> otherExtra = Maps.newHashMap();
            otherExtra.put("period", otherDepJob.getSchedulingPeriod());
            otherExtra.put("owner", otherDepJob.getOwner());
            Vertex otherDepVertex = new Vertex(otherMaskId, otherDepJob.getName(), otherDepJob.getType(),
                    JSONUtils.toJsonString(otherExtra));
            String otherVertexStr = JSONUtils.toJsonString(otherDepVertex);
            // 构造边
            String label = null;
            if (dep.getType() == 1) {
                label = "集合: [" + dep.getOffset() + "]";
            }
            if (dep.getType() == 2) {
                label = "区间: [" + dep.getOffset() + "]";
            }
            String sourceEdge = isTarget ? otherVertexStr : vertexStr;
            String targetEdge = isTarget ? vertexStr : otherVertexStr;
            // 已包含节点就只添加边
            if (dag.containsVertex(otherVertexStr)) {
                dag.addEdge(sourceEdge, targetEdge, new RelationshipEdge(label));
                continue;
            }
            // 不包含节点则添加节点、边，并递归
            dag.addVertex(otherVertexStr);
            dag.addEdge(sourceEdge, targetEdge, new RelationshipEdge(label));
            buildGraph(dag, otherMaskId);
        }
    }

    public void buildInstanceGraph(DirectedWeightedPseudograph<String, RelationshipEdge> dag, String instanceId) {
        JobInstance instance = jobInstanceService.findByInstanceId(instanceId);
        List<Map> upstream = JSONUtils.toList(instance.getUpstreamVertex(), Map.class);
        List<Map> downstream = JSONUtils.toList(instance.getDownstreamVertex(), Map.class);
        upstream.addAll(downstream);

        Job job = jobService.getJobByMaskIdAndVersion(instance.getMaskId(), instance.getVersion());
        Map<String, Object> extra = Maps.newHashMap();
        extra.put("state", instance.getState());
        extra.put("bizTime", DateUtils.format(instance.getBizTime(), Constants.YYYY_MM_DD_HH_MM_SS));
        Vertex vertex = new Vertex(instanceId, job.getName(), job.getType(), JSONUtils.toJsonString(extra));
        String vertexStr = JSONUtils.toJsonString(vertex);
        dag.addVertex(vertexStr);
        for (int i = 0; i < upstream.size(); i++) {
            Map map = upstream.get(i);
            String maskId = (String) map.get("maskId");
            int version = Integer.parseInt(map.get("version").toString());
            String bizTime = (String) map.get("bizTime");
            boolean isUpstream = map.get("upstream") != null && Boolean.parseBoolean(map.get("upstream").toString());
            JobInstance depInstance = jobInstanceService.findByMaskIdAndVersionAndBizTime(maskId, version, bizTime);
            Job depJob = jobService.getJobByMaskIdAndVersion(maskId, version);
            // 依赖任务没有生成实例，生成虚拟节点
            if (depInstance == null) {
                Map<String, Object> depExtra = Maps.newHashMap();
                depExtra.put("virtual", true);
                Vertex depVertex = new Vertex(depJob.getMaskId() + i, depJob.getName(), depJob.getType(),
                        JSONUtils.toJsonString(depExtra));
                String depVertexStr = JSONUtils.toJsonString(depVertex);
                dag.addVertex(depVertexStr);
                if (isUpstream) {
                    dag.addEdge(depVertexStr, vertexStr, new RelationshipEdge(""));
                } else {
                    dag.addEdge(vertexStr, depVertexStr, new RelationshipEdge(""));
                }
            } else {
                Map<String, Object> depExtra = Maps.newHashMap();
                depExtra.put("state", depInstance.getState());
                depExtra.put("bizTime", DateUtils.format(depInstance.getBizTime(), Constants.YYYY_MM_DD_HH_MM_SS));
                Vertex depVertex = new Vertex(depInstance.getInstanceId(), depJob.getName(), depJob.getType(),
                        JSONUtils.toJsonString(depExtra));
                String depVertexStr = JSONUtils.toJsonString(depVertex);
                String sourceEdge = isUpstream ? depVertexStr : vertexStr;
                String targetEdge = isUpstream ? vertexStr : depVertexStr;
                String label = isUpstream ? depInstance.getState().toString() : instance.getState().toString();
                if (dag.containsVertex(depVertexStr)) {
                    dag.addEdge(sourceEdge, targetEdge, new RelationshipEdge(label));
                    continue;
                }
                dag.addVertex(depVertexStr);
                dag.addEdge(sourceEdge, targetEdge, new RelationshipEdge(label));
                buildInstanceGraph(dag, depInstance.getInstanceId());
            }
        }
    }
}
