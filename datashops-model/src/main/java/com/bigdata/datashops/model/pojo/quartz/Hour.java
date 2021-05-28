package com.bigdata.datashops.model.pojo.quartz;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
public class Hour {
    @NotNull(message = "must not be null")
    private int type;

    //type=1,指定时间，多选
    private String hours;

    //type=2
    @Pattern(regexp = "^[0-9]{2}$", message = "时间格式不正确")
    private String hourBegin;
    @Pattern(regexp = "^[0-9]{2}$", message = "时间格式不正确")
    private String hourMinute;
    private String hourPeriod;
    @Pattern(regexp = "^[0-9]{2}$", message = "时间格式不正确")
    private String hourEnd;
}
