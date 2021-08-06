package com.bigdata.datashops.model.pojo.user;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@TableName(value = "t_user")
@JsonIgnoreProperties(value = {"password"})
public class User extends BaseModel {
    private String name;

    private String password;

    private String email;

    private String phone;

    private Date lastLoginTime;

    private String avatar;

    @TableField(exist = false)
    private List<Role> roleList;

    public String getAvatar() {
        if (this.avatar == null) {
            return "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2146034403,1504718527&fm=26&gp=0.jpg";
        }
        return this.avatar;
    }
}
