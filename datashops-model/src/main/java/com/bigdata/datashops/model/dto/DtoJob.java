package com.bigdata.datashops.model.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DtoJob {
    @NotBlank(message = "job name must not be empty")
    private String name;
    /**
     * 作业描述
     */
    private String description;
    /**
     * 作业类型
     * {@link com.bigdata.datashops.model.enums.JobType}
     */
    @NotBlank(message = "job type must not be null")
    private Integer type;

    /**
     * 作业配置
     */
    private String configJson;

    /**
     * 0 delete 1 normal
     */
    private Integer status;

    @NotBlank(message = "must not be null")
    private String owner;

    /**
     * 后续把历史版本写入其他表
     */
    private String version;

    private String jobContext;
}
