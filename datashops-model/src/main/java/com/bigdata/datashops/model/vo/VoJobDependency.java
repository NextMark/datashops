package com.bigdata.datashops.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoJobDependency {
    private Integer id;

    private String name;

    private String sourceId;

    private String offset;

    private String owner;

    private int type;

    private int jobType;

    private int schedulingPeriod;
}
