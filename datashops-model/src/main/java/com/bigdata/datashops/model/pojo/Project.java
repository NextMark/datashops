package com.bigdata.datashops.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_project")
public class Project extends BaseModel {
    private String name;
}
