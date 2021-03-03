package com.bigdata.datashops.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoJobDependency {
    private String name;

    private Integer sourceId;

    private int offset;

    private String owner;

    private int type;
}
