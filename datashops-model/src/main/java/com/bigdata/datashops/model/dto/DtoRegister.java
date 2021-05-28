package com.bigdata.datashops.model.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.bigdata.datashops.common.Constants;

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

    private String avatar;

    private String roleIds;

    public void setRoleIds(List ids) {
        this.roleIds = StringUtils.join(ids, Constants.SEPARATOR_COMMA);
    }

    public String[] getRoleIds() {
        return this.roleIds.split(Constants.SEPARATOR_COMMA);
    }
}
