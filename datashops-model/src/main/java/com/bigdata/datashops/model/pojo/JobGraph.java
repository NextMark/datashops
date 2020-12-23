package com.bigdata.datashops.model.pojo;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_job_graph")
public class JobGraph extends BaseModel {
    @NotBlank(message = "job name must not be empty")
    private String name;
    /**
     * 作业描述
     */
    private String description;
    /**
     * 作业类型
     */
    @NotNull(message = "job type must not be null")
    private Integer type;

    /**
     * 作业配置
     */
    private String configJson;

    /**
     * 0已删除，1存在
     */
    private Integer status;

    /**
     * 调度周期
     */
    private Integer schedulingPeriod;

    private String cronExpression;

    /**
     * 下次调度时间
     */
    private String nextTriggerTime;

    /**
     * 0 关闭调度，1 开启调度
     */
    private int schedulerStatus;

    private String dependencies;

    private Integer groupId;

    private String owner;

    private Integer scheduleType;

    private Integer notifyType;

    private Integer priority;

    private String emails;

    private String phones;

    private Integer baseTimeType;

    private Integer offset;

    private String version;

    private Integer timeout;

    private Integer retryTimes;

    private Integer retryInterval;
}
