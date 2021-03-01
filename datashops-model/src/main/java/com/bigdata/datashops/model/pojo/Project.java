package com.bigdata.datashops.model.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_project")
public class Project extends BaseModel {
    private String name;
}
