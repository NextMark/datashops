package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum SchedulingPeriod {
    MONTH(0, "month"),
    WEEK(1, "week"),
    DAY(2, "day"),
    HOUR(3, "hour"),
    MINUTE(4, "minute");

    SchedulingPeriod(int code, String name){
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

    private static HashMap<Integer, SchedulingPeriod> MAP = new HashMap<>();

    static {
        for (SchedulingPeriod value : SchedulingPeriod.values()) {
            MAP.put(value.getCode(), value);
        }
    }

    public static SchedulingPeriod of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }

}
