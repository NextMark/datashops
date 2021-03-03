package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum HostSelector {
    RANDOM(0, "random"), SCORE(1, "score"), ASSIGN(2, "assign");

    HostSelector(int code, String name) {
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

    private static HashMap<Integer, HostSelector> MAP = new HashMap<>();

    static {
        for (HostSelector type : HostSelector.values()) {
            MAP.put(type.getCode(), type);
        }
    }

    public static HostSelector of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
