package com.bigdata.datashops.dao.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.model.pojo.job.JobInstance;

public interface JobInstanceMapper extends BaseMapper<JobInstance> {
    IPage<JobInstance> findJobInstanceListPaging(IPage<JobInstance> page, @Param("name") String name,
                                                 @Param("operator") String operator);

}
