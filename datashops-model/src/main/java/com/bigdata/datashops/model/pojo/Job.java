package com.bigdata.datashops.model.pojo;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_job")
public class Job extends BaseModel {
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
    @NotNull(message = "job type must not be null")
    private Integer type;

    /**
     * 作业配置
     */
    private String configJson;

    /**
     * 0 delete 1 normal
      */
    private Integer status;

    /**
     * 调度周期
     */
    private Integer schedulingPeriod;

    private Integer graphId;

    private String owner;

    /**
     * 后续把历史版本写入其他表
     */
    private String version;

    private String jobContext;
}
