package com.bigdata.datashops.rpc;

import lombok.Data;

@Data
public class GrpcServerConfig {
    private String host;

    // default port
    private int port = 60000;
}
