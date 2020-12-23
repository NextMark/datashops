package com.bigdata.datashops.model.pojo.quartz;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Minute {
    @NotNull(message = "must not be null")
    private String beginHour;

    @NotNull(message = "must not be null")
    private String beginMinute;

    @NotNull(message = "must not be null")
    private String period;

    @NotNull(message = "must not be null")
    private String endHour;

    @NotNull(message = "must not be null")
    private String endMinute;
}
