package com.bigdata.datashops.server.worker;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartBeat {
    @Scheduled(cron = "*/10 * * * * ?")
    public void reportHostInfo() {

    }
}
