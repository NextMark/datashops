package com.bigdata.datashops.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class GrpcServerConfig {
    @Value("${grpc.server.host}")
    private String host;

    // default port
    @Value("${grpc.server.port}")
    private int port = 60000;
}
