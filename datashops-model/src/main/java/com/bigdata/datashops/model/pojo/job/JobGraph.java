package com.bigdata.datashops.model.pojo.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.bigdata.datashops.model.vo.VoJobNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_job_graph")
public class JobGraph extends BaseModel {
    private String name;
    /**
     * 作业描述
     */
    private String description;

    /**
     * 作业组
     * {
     * graphNodes: [
     * {
     * id: 1,
     * top: 10px,
     * left: 20px,
     * ico: el
     * }
     * ]
     * }
     */
    private String graphNodes;

    private String configJson;

    private String jobIds;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextTriggerTime;

    /**
     * 0 关闭调度，1 开启调度
     */
    private int schedulerStatus;

    private Integer groupId;

    private String owner;

    private Integer notifyType;

    private Integer priority;

    private String emails;

    private String phones;

    private Integer baseTimeType;

    private Integer offset;

    private Integer timeout;

    private Integer retryTimes;

    private Integer retryInterval;

    private Integer workerSelector;

    @Transient
    private List<VoJobNode> nodeList;

    @Transient
    private List<Map<String, Object>> lineList;

    public Map<String, List<Integer>> parseJobIds() {
        Map<String, List<Integer>> result = Maps.newHashMap();
        if (StringUtils.isBlank(jobIds)) {
            return result;
        }
        String[] arrs = jobIds.split(Constants.SEPARATOR_COMMA);
        for (String arr : arrs) {
            String[] id = arr.split(Constants.SEPARATOR_HYPHEN);
            List<Integer> data = result.get(id[0]);
            if (data == null) {
                data = Lists.newArrayList();
                data.add(Integer.valueOf(id[1]));
            } else {
                data.add(Integer.valueOf(id[1]));
            }
            result.put(id[0], data);
        }
        return result;
    }
}
