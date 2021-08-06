package com.bigdata.datashops.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_queue")
public class YarnQueue extends BaseModel {
    private Integer projectId;

    private String name;

    private String value;
}
