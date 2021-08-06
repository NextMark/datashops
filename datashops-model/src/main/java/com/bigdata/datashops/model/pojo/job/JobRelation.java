package com.bigdata.datashops.model.pojo.job;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_job_relation")
public class JobRelation extends BaseModel {
    private String graphMaskId;

    private String jobMaskId;

    private String topPos;

    private String leftPos;

    private Integer nodeType;

}
