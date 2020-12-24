package com.bigdata.datashops.server.master.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class MasterConfig {
    @Value("${master.exec.threads:100}")
    private int masterJobThreads;

}
