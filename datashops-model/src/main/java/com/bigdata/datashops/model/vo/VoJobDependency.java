package com.bigdata.datashops.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoJobDependency {
    private String name;

    private String sourceMaskId;

    private int offset;

    private String owner;

    private int type;
}
