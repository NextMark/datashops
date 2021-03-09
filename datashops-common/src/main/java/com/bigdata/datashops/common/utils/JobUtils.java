package com.bigdata.datashops.common.utils;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Maps;

public class JobUtils {
    public static String genJobInstanceId() {
        return DateUtils.getCurrentTime("yyMMddHHmmss") + RandomStringUtils.randomNumeric(6);
    }

    public static String genMaskId(String prefix) {
        return prefix + DateUtils.getCurrentTime("yyMMddHHmmss") + RandomStringUtils.randomNumeric(3);
    }

    public static String buildJobData(int type, String value) {
        Map data = JSONUtils.toMap(value);
        Map<String, Object> result = Maps.newHashMap();
        result.putAll(data);
        switch (type) {
            case 0:
                result.put("dbType", 0);
                result.put("user", "develop");
                result.put("password", "3NOPvVw9HLt0akfh");
                result.put("address", "jdbc:hive2://192.168.1.124:10000/default");
                result.put("database", "default");
                break;
            case 4:
                result.put("dbType", 4);
                result.put("user", "develop");
                result.put("password", "3NOPvVw9HLt0akfh");
                result.put("address", "jdbc:hive2://192.168.1.124:10000/default");
                result.put("database", "default");
                break;
            case 6:
                result.put("dbType", 6);
                result.put("user", "develop");
                result.put("password", "3NOPvVw9HLt0akfh");
                result.put("address", "jdbc:hive2://192.168.1.124:10000/default");
                result.put("database", "default");
                break;
        }

        return JSONUtils.toJsonString(result);
    }
}
