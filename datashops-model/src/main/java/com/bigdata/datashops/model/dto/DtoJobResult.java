package com.bigdata.datashops.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.bigdata.datashops.common.utils.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoJobResult extends AbstractPageDto {

    @NotNull(message="ID不能为空")
    private Integer itemId;

    @NotBlank(message="开始时间不能为空")
    @Pattern(regexp="^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message="开始时间格式不正确")
    private String startDate;

    @NotBlank(message="结束时间不能为空")
    @Pattern(regexp="^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message="结束时间格式不正确")
    private String endDate;

    private JsonNode filtering;

    @Override
    public String validate() {
        if (itemId == null) {
            return "error";
        }
        return DateUtils.isValidFormat(startDate) && DateUtils.isValidFormat(endDate) ? null : "error";
    }
}
