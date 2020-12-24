package com.bigdata.datashops.model.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtoCronExpression {
    @NotNull
    private int schedulingPeriod;

    private String config;

}
