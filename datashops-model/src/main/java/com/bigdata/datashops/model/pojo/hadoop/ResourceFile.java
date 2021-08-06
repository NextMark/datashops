package com.bigdata.datashops.model.pojo.hadoop;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_resource_file")
public class ResourceFile extends BaseModel {
    private Integer type;

    private Integer jobId;

    private String name;

    private Long size;

    private String url;
}
