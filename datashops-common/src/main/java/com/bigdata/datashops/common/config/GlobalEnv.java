package com.bigdata.datashops.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalEnv {
    @Value("${mail.port}")
    public int mailPort;
}
