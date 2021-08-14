package com.bigdata.datashops.service.graph;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.springframework.stereotype.Service;

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

    public void buildInstanceGraph(DirectedWeightedPseudograph<String, DefaultWeightedEdge> dag, String instanceId) {
        JobInstance instance = jobInstanceService.findByInstanceId(instanceId);
        List<Map> upstream = JSONUtils.toList(instance.getUpstreamVertex(), Map.class);
        List<Map> downstream = JSONUtils.toList(instance.getDownstreamVertex(), Map.class);
        upstream.addAll(downstream);

        Job job = jobService.getJobByMaskIdAndVersion(instance.getMaskId(), instance.getVersion());
        Vertex vertex = new Vertex(instanceId, job.getName(), job.getType(), "");
        String vertexStr = JSONUtils.toJsonString(vertex);
        if (!dag.containsVertex(vertexStr)) {
            dag.addVertex(vertexStr);
        }
        for (int i = 0; i < upstream.size(); i++) {
            Map map = upstream.get(i);
            String maskId = (String) map.get("maskId");
            int version = (int) map.get("version");
            String bizTime = (String) map.get("bizTime");
            boolean isUpstream = (boolean) map.get("upstream");
            JobInstance depInstance = jobInstanceService.findByMaskIdAndVersionAndBizTime(maskId, version, bizTime);
            Job depJob = jobService.getJobByMaskIdAndVersion(maskId, version);
            // 依赖任务没有生成实例，生成虚拟节点
            if (depInstance == null) {
                Map<String, Object> extra = Maps.newHashMap();
                extra.put("virtual", true);
                Vertex depVertex = new Vertex(depJob.getMaskId() + i, depJob.getName(), depJob.getType(),
                        JSONUtils.toJsonString(extra));
                dag.addVertex(JSONUtils.toJsonString(depVertex));
            } else {
                Vertex depVertex = new Vertex(depInstance.getInstanceId(), depJob.getName(), depJob.getType(), "");
                String depVertexStr = JSONUtils.toJsonString(depVertex);
                if (isUpstream) {
                    Graphs.addEdge(dag, depVertexStr, vertexStr, 1);
                } else {
                    Graphs.addEdge(dag, vertexStr, depVertexStr, 1);
                }
                if (!dag.containsVertex(depVertexStr)) {
                    dag.addVertex(depVertexStr);
                    buildInstanceGraph(dag, depInstance.getInstanceId());
                }
            }
        }
    }
}
