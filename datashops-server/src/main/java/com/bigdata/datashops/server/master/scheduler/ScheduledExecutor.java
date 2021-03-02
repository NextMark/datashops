package com.bigdata.datashops.server.master.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class ScheduledExecutor {
    public void run(Runnable runnable, int period) {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(runnable, 1, period, TimeUnit.SECONDS);
    }
}
