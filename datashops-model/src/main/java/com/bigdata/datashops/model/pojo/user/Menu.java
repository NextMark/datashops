package com.bigdata.datashops.model.pojo.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user_menu")
public class Menu extends BaseModel {

    private String path;

    private String name;

    private String meta;

    private String component;

    private String redirect;

    @Transient
    private List<Menu> children;

    private ObjectNode getMeta() {
        return JSONUtils.parseObject(meta);
    }
}
