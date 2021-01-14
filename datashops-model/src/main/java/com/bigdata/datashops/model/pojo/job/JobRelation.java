package com.bigdata.datashops.model.pojo.job;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_job_relation")
public class JobRelation extends BaseModel {
    private String graphStrId;

    private String jobStrId;

    private String topPos;

    private String leftPos;

    private Integer nodeType;

}
