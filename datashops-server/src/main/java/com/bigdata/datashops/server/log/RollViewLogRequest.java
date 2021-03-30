package com.bigdata.datashops.server.log;

import lombok.Data;

@Data
public class RollViewLogRequest {
    private String path;

    /**
     * skip line number
     */
    private int skipLineNum;

    /**
     * query line number
     */
    private int limit;
}
