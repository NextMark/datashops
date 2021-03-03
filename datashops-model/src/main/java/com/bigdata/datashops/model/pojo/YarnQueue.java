package com.bigdata.datashops.model.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_queue")
public class YarnQueue extends BaseModel {
    private Integer projectId;

    private String name;

    private String value;
}
