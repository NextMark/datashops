package com.bigdata.datashops.model.pojo.job;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "t_job_instance")
public class JobInstance extends BaseModel implements Comparable<JobInstance> {
    private Integer projectId;

    private String name;

    private Integer jobId;

    private String maskId;

    private String instanceId;

    /**
     * job instance type, graph、job
     * {@link com.bigdata.datashops.model.enums.JobInstanceType}
     */
    private int type;

    /**
     * job instance state
     * {@link com.bigdata.datashops.model.enums.RunState}
     */
    private Integer state;

    /**
     * 0 delete 1 normal
     */
    private Integer status;

    private String host;

    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Integer retryTimes;

    private Integer retryInterval;

    private Integer priority;

    private Integer failureStrategy;

    /**
     * 基准时间，依赖及指定数据时间用
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date bizTime;

    /**
     * 数据时间，执行SQL对应输出的数据时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dataTime;

    /**
     * 动态依赖数组，只保留还未成功的作业，全部成功后该字段为空
     * 为防止频繁查库后续可依赖外部缓存
     */
    private String dynamicDependency;

    /**
     * 依赖的图或节点，数组，逗号分隔
     * example: id|offset，12|0，12|-3，13|1
     */
    private String preDependency;

    /**
     * 下游依赖节点，数组，逗号分隔
     */
    private String postDependency;

    private String data;

    private int hostSelector;

    @Transient
    private Job job;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(getId());
        sb.append(", maskId=").append(maskId);
        sb.append(", status=").append(status);
        sb.append(", startTime=").append(startTime);
        sb.append(", stopTime=").append(endTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int compareTo(JobInstance o) {
        if (o != null) {
            if (this.priority.equals(o.priority)) {
                return this.createTime.after(o.createTime) ? 1 : -1;
            }
            return this.priority.compareTo(o.getPriority());
        }
        return 0;
    }
}
