package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum DbType {
    HIVE(0, "hive"),
    MYSQL(1, "mysql"),
    CLICK_HOUSE(2, "clickhouse");

    DbType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private final int code;
    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static HashMap<Integer, DbType> MAP = new HashMap<>();

    static {
        for (DbType dbType : DbType.values()) {
            MAP.put(dbType.getCode(), dbType);
        }
    }

    public static DbType of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
