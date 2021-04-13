package com.bigdata.datashops.model.pojo.job;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_temporary_query")
public class TemporaryQuery extends BaseModel {
    private String name;

    private Integer type;

    private Integer uid;

    private String owner;

    private String value;
}
