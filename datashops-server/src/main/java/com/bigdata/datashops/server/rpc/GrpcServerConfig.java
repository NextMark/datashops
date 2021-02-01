package com.bigdata.datashops.server.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource(value = "master.properties")
public class GrpcServerConfig {
    @Value("${grpc.server.host:localhost}")
    private String host;

    // default port
    @Value("${grpc.server.port:60000}")
    private int port;
}
