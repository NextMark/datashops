package com.bigdata.datashops.model.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DtoJob {
    private Integer id;

    @NotBlank(message = "job name must not be empty")
    private String name;

    private String description;

    @NotBlank(message = "must not be null")
    private String owner;

    /**
     * 调度周期
     */
    private Integer schedulingPeriod;

    private String cronExpression;

    /**
     * 下次调度时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextTriggerTime;

    /**
     * 0 关闭调度，1 开启调度
     */
    private Integer schedulerStatus;

    private Integer notifyType;

    private Integer priority;

    private String emails;

    private String phones;

    private Integer baseTimeType;

    private Integer offset;

    private Integer timeout;

    private Integer retry;

    private Integer retryTimes;

    private Integer retryInterval;

    private Integer workerSelector;

    private Integer queueId;

    private String timeConfig;

    private Date validStartDate;

    private Date validEndDate;

}
