package com.bigdata.datashops.model.pojo.job;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_temporary_query")
public class TemporaryQuery extends BaseModel {
    private String name;

    private Integer type;

    private Integer uid;

    private String owner;

    private String value;
}
