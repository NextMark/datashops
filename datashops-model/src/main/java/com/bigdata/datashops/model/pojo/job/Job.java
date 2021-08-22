package com.bigdata.datashops.model.pojo.job;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.bigdata.datashops.model.vo.VoJobNode;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@TableName(value = "t_job")
public class Job extends BaseModel {
    private String maskId;

    private String name;
    /**
     * 作业描述
     */
    private String description;
    /**
     * 作业类型
     * {@link com.bigdata.datashops.model.enums.JobType}
     */
    private Integer type;

    /**
     * 0 delete 1 normal
     */
    private Integer status = 1;

    private String owner;

    /**
     * 后续把历史版本写入其他表
     */
    private Integer version;

    private String data;

    private String ico;

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
    private Integer schedulerStatus = 0;

    private Integer projectId;

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

    private Integer hostSelector;

    private String host;

    private Integer queueId;

    private String operator;

    @TableField(exist = false)
    private List<VoJobNode> nodeList;

    @TableField(exist = false)
    private List<Edge> lineList;

    private Date validStartDate;

    private Date validEndDate;

    private String timeConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Job job = (Job) o;

        if (!name.equals(job.name)) {
            return false;
        }
        if (!data.equals(job.data)) {
            return false;
        }
        if (!schedulingPeriod.equals(job.schedulingPeriod)) {
            return false;
        }
        if (!cronExpression.equals(job.cronExpression)) {
            return false;
        }
        if (!schedulerStatus.equals(job.schedulerStatus)) {
            return false;
        }
        if (!notifyType.equals(job.notifyType)) {
            return false;
        }
        if (!priority.equals(job.priority)) {
            return false;
        }
        if (!timeout.equals(job.timeout)) {
            return false;
        }
        if (!retry.equals(job.retry)) {
            return false;
        }
        if (!retryTimes.equals(job.retryTimes)) {
            return false;
        }
        if (!retryInterval.equals(job.retryInterval)) {
            return false;
        }
        if (!queueId.equals(job.queueId)) {
            return false;
        }
        return timeConfig.equals(job.timeConfig);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
