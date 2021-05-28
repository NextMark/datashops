package com.bigdata.datashops.server.job;

import lombok.Data;

@Data
public class CommandResult {
    private Integer processId;

    private Integer existCode;

    private String data;
}
