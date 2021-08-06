package com.bigdata.datashops.model.pojo.job;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.enums.DataType;
import com.bigdata.datashops.model.pojo.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_job_result")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResult extends BaseModel {
    private Long time;

    public DataType dataType;

    public Long longValue;

    public Double doubleValue;

    private String keyValue;

    public String storeUrl;

    private Long itemId;

    @TableField(exist = false)
    private JsonNode jsonArray;

    public Object getValue() {
        switch (dataType) {
            case LONG:
                return longValue;
            case DOUBLE:
                return doubleValue;
            case KEY_VALUE:
                return keyValue;
            case FILE:
                return storeUrl;
            default:
                return null;
        }
    }

    public Map<String, Object> parse(List<String> fields, JsonNode filtering) {
        Map<String, Object> data = Maps.newLinkedHashMap();
        String strTime = DateFormatUtils.format(time * 1000, DateFormatUtils.ISO_DATE_FORMAT.getPattern());
        if (dataType == DataType.LONG || dataType == DataType.FILE) {
            data.put("time", strTime);
            data.put("type", dataType.toString());
            data.put("value", this.getValue());
            return data;
        }
        ArrayNode array = JSONUtils.parseArray(this.getValue().toString());

        for (int i = 0; i < array.size(); i++) {
            Object node = array.get(i);
            data.put(fields.get(i), node);
        }
        data.put("time", strTime);
        data.put("type", dataType.toString());
        if (filtering == null) {
            return data;
        } else {
            if (JSONUtils.mapContains(data,
                    Objects.requireNonNull(JSONUtils.toMap(filtering.asText(), String.class, Object.class)))) {
                return data;
            }
        }
        return null;
    }
}
