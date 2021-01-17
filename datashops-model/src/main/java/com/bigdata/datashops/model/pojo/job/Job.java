package com.bigdata.datashops.model.pojo.job;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_job")
public class Job extends BaseModel {
    private String strId;

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
     * 作业配置
     */
    private String configJson;

    /**
     * 0 delete 1 normal
     */
    private Integer status;

    private String owner;

    /**
     * 后续把历史版本写入其他表
     */
    //private String version;

    private String jobContext;

    private String ico;

    private Integer state;
}
