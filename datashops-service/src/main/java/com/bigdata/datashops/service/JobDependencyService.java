package com.bigdata.datashops.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bigdata.datashops.dao.mapper.JobDependencyMapper;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.Node;
import com.bigdata.datashops.model.vo.VoJobDependency;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class JobDependencyService extends BaseService {

    @Autowired
    private JobDependencyMapper jobDependencyMapper;

    public void save(JobDependency entity) {
        jobDependencyMapper.insert(entity);
    }

    public void update(JobDependency entity) {
        LambdaUpdateWrapper<JobDependency> wrapper = Wrappers.lambdaUpdate();
        if (StringUtils.isNotBlank(entity.getOffset())) {
            wrapper.set(JobDependency::getOffset, entity.getOffset());
        }
        if (!Objects.isNull(entity.getType())) {
            wrapper.set(JobDependency::getType, entity.getType());
        }
        wrapper.eq(JobDependency::getId, entity.getId());
        jobDependencyMapper.update(null, wrapper);
    }

    public void deleteById(int id) {
        jobDependencyMapper.deleteById(id);
    }

    public List<JobDependency> findBySourceId(String source) {
        LambdaQueryWrapper<JobDependency> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobDependency::getSourceId, source);
        return jobDependencyMapper.selectList(lqw);
    }

    public List<JobDependency> findByTargetId(String target) {
        LambdaQueryWrapper<JobDependency> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobDependency::getTargetId, target);
        return jobDependencyMapper.selectList(lqw);
    }

    public List<VoJobDependency> fillJobInfo(List<JobDependency> dependencies) {
        List<VoJobDependency> vo = Lists.newArrayList();
        for (JobDependency dependency : dependencies) {
            Job job = jobService.getMaxVersionByMaskId(dependency.getSourceId());
            VoJobDependency v =
                    VoJobDependency.builder().id(dependency.getId()).name(job.getName()).offset(dependency.getOffset())
                            .owner(job.getOwner()).schedulingPeriod(job.getSchedulingPeriod())
                            .type(dependency.getType()).jobType(job.getType()).sourceId(dependency.getSourceId())
                            .build();
            vo.add(v);
        }
        return vo;
    }

    public Map<String, Object> getJobDependencyGraph(String id) {
        Map<String, Object> result = Maps.newHashMap();
        List<Edge> edges = Lists.newArrayList();
        List<Node> nodes = Lists.newArrayList();
        Job job = jobService.getOnlineJobByMaskId(id);
        Node node = new Node();
        node.setLabel(job.getName() + "\n" + StringUtils.lowerCase(JobType.of(job.getType()).name()));
        node.setId(id.toString());
        node.setClassName("type-normal");
        nodes.add(node);

        findPre(id, edges, nodes);
        findPost(id, edges, nodes);
        result.put("nodes", nodes);
        result.put("edges", edges);
        return result;
    }

    private void findPre(String id, List<Edge> edges, List<Node> nodes) {
        List<JobDependency> pre = findByTargetId(id);
        if (pre.size() > 0) {
            for (JobDependency dependency : pre) {
                Job job = jobService.getMaxVersionByMaskId(dependency.getSourceId());

                if (dependency.getTargetId().equals(dependency.getSourceId())) {
                    Node node = new Node();
                    node.setId(dependency.getSourceId() + dependency.getOffset());
                    node.setLabel(job.getName() + "\n" + StringUtils.lowerCase(JobType.of(job.getType()).name()));
                    node.setClassName("type-normal");
                    nodes.add(node);

                    Edge edge = new Edge();
                    edge.setFrom(dependency.getSourceId() + dependency.getOffset());
                    edge.setTo(dependency.getTargetId());
                    edge.setLabel(
                            String.format("%s: [%s]", dependency.getType() == 1 ? "集合" : "区间", dependency.getOffset()));
                    edges.add(edge);
                    continue;
                }

                Node node = new Node();
                node.setId(dependency.getSourceId());
                node.setLabel(job.getName() + "\n" + StringUtils.lowerCase(JobType.of(job.getType()).name()));
                node.setClassName("type-normal");
                nodes.add(node);

                Edge edge = new Edge();
                edge.setFrom(dependency.getSourceId());
                edge.setTo(dependency.getTargetId());
                edge.setLabel(
                        String.format("%s: [%s]", dependency.getType() == 1 ? "集合" : "区间", dependency.getOffset()));
                edges.add(edge);

                findPre(dependency.getSourceId(), edges, nodes);
            }
        }
    }

    private void findPost(String id, List<Edge> edges, List<Node> nodes) {
        List<JobDependency> pre = findBySourceId(id);
        if (pre.size() > 0) {
            for (JobDependency dependency : pre) {
                if (dependency.getSourceId().equals(dependency.getTargetId())) {
                    continue;
                }
                Job job = jobService.getMaxVersionByMaskId(dependency.getTargetId());
                Node node = new Node();
                node.setId(dependency.getTargetId());
                node.setLabel(job.getName() + "\n" + StringUtils.lowerCase(JobType.of(job.getType()).name()));
                node.setClassName("type-normal");
                nodes.add(node);

                Edge edge = new Edge();
                edge.setFrom(dependency.getSourceId());
                edge.setTo(dependency.getTargetId());
                edge.setLabel(
                        String.format("%s: [%s]", dependency.getType() == 1 ? "集合" : "区间", dependency.getOffset()));
                edges.add(edge);

                findPost(dependency.getTargetId(), edges, nodes);
            }
        }
    }
}
