package com.bigdata.datashops.model.pojo.user;

import java.util.LinkedHashMap;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_user_menu")
public class Menu extends BaseModel {

    private String path;

    private String name;

    private String meta;

    private String component;

    private Integer parentId;

    private Boolean hidden;

    private Integer sort;

    private String authority;

    private String parameters;

    @TableField(exist = false)
    private List<Menu> children = Lists.newArrayList();

    public ObjectNode getMeta() {
        return JSONUtils.parseObject(meta);
    }

//    public void setMeta(LinkedHashMap map) {
//        this.meta = JSONUtils.toJsonString(map);
//    }

}
