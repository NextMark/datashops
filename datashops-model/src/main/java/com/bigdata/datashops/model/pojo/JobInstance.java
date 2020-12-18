package com.bigdata.datashops.model.pojo;

import java.util.Date;

import javax.persistence.Table;

import com.bigdata.datashops.model.enums.RunState;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Table(name = "t_job_instance")
public class JobInstance extends BaseModel implements Comparable<JobInstance> {
    private Long graphId;

    private Long jobId;

    private String instanceId;

    /**
     * graph、job
      */
    private int type;

    private RunState state;

    /**
     * 0 delete 1 normal
      */
    private Integer status;

    private String host;

    private String operator;

    private Date submitTime;

    private Date startTime;

    private Date endTime;

    private Integer retryTimes;

    private Integer retryInterval;

    private Integer priority;

    private Integer failureStrategy;

    /**
     * 基准时间，依赖及指定数据时间用
     */
    private Date baseTime;

    /**
     * 数据时间，执行SQL对应输出的数据时间
     */
    private Date dataTime;

    private Integer offset;

    /**
     * 依赖的节点，数组，逗号分隔
     */
    private String preDependency;

    /**
     * 下游依赖节点，数组，逗号分隔
     */
    private String postDependency;

    private String data;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(getId());
        sb.append(", jobId=").append(jobId);
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
