package com.bigdata.datashops.model.pojo.user;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user")
@JsonIgnoreProperties(value = {"password"})
public class User extends BaseModel {
    private String name;

    private String password;

    private String email;

    private String phone;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

    private String avatar;

    @Transient
    private List<Role> roleList;

    public String getAvatar() {
        if (this.avatar == null) {
            return "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2146034403,1504718527&fm=26&gp=0.jpg";
        }
        return this.avatar;
    }
}
