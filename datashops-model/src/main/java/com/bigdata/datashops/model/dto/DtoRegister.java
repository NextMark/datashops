package com.bigdata.datashops.model.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DtoRegister {
    private String name;

    @NotNull(message = "不能为空")
    private String phone;

    @NotNull(message = "不能为空")
    private String password;

    @NotNull(message = "不能为空")
    private String email;
}
