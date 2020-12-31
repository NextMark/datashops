package com.bigdata.datashops.model.pojo.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
}
