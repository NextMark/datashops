package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum RunState {
    CREATED(0, "has submitted"), //WAIT_FOR_RESOURCE(1, "wait for resource"),
    WAIT_FOR_DEPENDENCY(2, "wait for dependency"), WAIT_FOR_RUN(3, "wait for run"), RUNNING(4, "running"),
    SUCCESS(5, "success"), CANCEL(6, "cancel"), FAILURE(7, "failure"), KILL(8, "kill");

    RunState(int code, String name) {
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

    private static HashMap<Integer, RunState> MAP = new HashMap<>();

    static {
        for (RunState jobType : RunState.values()) {
            MAP.put(jobType.getCode(), jobType);
        }
    }

    public static RunState of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

}
