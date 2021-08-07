package com.bigdata.datashops.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.model.pojo.job.Job;

public interface JobMapper extends BaseMapper<Job> {
    IPage<Job> findJobListPaging(IPage<Job> page, @Param("name") String name, @Param("owner") String owner);

    Job findJobByMaskIdAndVersion(@Param("maskId") String maskId, @Param("version") int version);

    Job findMaxVersionJob(@Param("maskId") String maskId);

    Job findOnlineJob(@Param("maskId") String maskId);

    List<List<String>> findJobVersionList(@Param("maskId") String maskId);
}
