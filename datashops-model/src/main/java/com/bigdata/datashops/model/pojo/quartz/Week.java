package com.bigdata.datashops.model.pojo.quartz;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Week {
    // 可多个
    @NotNull(message = "must not be null")
    private String week;

    @NotNull(message = "must not be null")
    @Pattern(regexp="^[0-9]{2}$", message="时间格式不正确")
    private String hour;

    @NotNull(message = "must not be null")
    @Pattern(regexp="^[0-9]{2}$", message="时间格式不正确")
    private String minute;
}
