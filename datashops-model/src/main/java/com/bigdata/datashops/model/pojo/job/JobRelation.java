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
    private String graphMaskId;

    private String jobMaskId;

    private String topPos;

    private String leftPos;

    private Integer nodeType;

}
