package com.bigdata.datashops.model.pojo.job;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_job_dependency")
public class JobDependency extends BaseModel {
    /**
     * default 1, 虚拟根节点
     */
    private Integer sourceId;

    private Integer targetId;

    /**
     * 依赖作业的偏移，逗号分隔，0，-1，-5
     */
    private Integer offset;

}
