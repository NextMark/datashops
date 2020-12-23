package com.bigdata.datashops.model.pojo.quartz;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Hour {
    @NotNull(message = "must not be null")
    private int type;

    //type=1,指定时间，多选
    private String hour;

    //type=2
    private String beginHour;
    private String beginMinute;
    private String period;
    private String endHour;
    private String endMinute;
}
