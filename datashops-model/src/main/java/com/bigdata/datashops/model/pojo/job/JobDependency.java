package com.bigdata.datashops.model.pojo.job;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_job_dependency")
public class JobDependency extends BaseModel {
    /**
     * default 1, 虚拟根节点
     */
    private String sourceId;

    private String targetId;

    /**
     * 依赖类型，1-集合，2-区间
     */
    private Integer type;

    /**
     * 依赖作业的偏移，逗号分隔，0，-1，-5
     */
    private String offset;

}
