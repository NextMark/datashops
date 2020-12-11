package com.bigdata.datashops.model.pojo;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_job_dependency")
public class JobDependency extends BaseModel {
    // default 1, 虚拟根节点
    private int sourceId;

    private int targetId;

    // job instance type
    private int dependType;
}
