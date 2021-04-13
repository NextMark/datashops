package com.bigdata.datashops.model.pojo.rpc;

import lombok.Data;

@Data
public class OSInfo {
    private String type;
    private String hostName;
    private String ip;

    private String name;

    private String version;
    private String availableMem;

    private String totalMem;

    private String cpuInfo;
}
