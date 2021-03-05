package com.bigdata.datashops.common.enums;

/**
 * resource type
 */
public enum ResourceType {
    /**
     * 0 file, 1 udf
     */
    FILE(0, "file"), UDF(1, "udf");

    ResourceType(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    private final int code;
    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }
}
