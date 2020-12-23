package com.bigdata.datashops.model.pojo.quartz;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Week {
    // 可多个
    @NotNull(message = "must not be null")
    private String week;

    @NotNull(message = "must not be null")
    private String hour;

    @NotNull(message = "must not be null")
    private String minute;
}
