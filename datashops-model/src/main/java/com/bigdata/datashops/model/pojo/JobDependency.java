package com.bigdata.datashops.model.pojo;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_job_dependency")
public class JobDependency extends BaseModel {
    /**
     * default 1, 虚拟根节点
      */
    private int sourceId;

    private int targetId;

    /**
     * job instance type，graph or job
     * {@link com.bigdata.datashops.model.enums.JobInstanceType}
      */
    private int dependType;

    /**
     * 依赖作业的偏移，逗号分隔，0，-1，-5
     */
    private String offset;
}
