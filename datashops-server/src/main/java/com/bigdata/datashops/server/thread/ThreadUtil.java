package com.bigdata.datashops.server.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadUtil {
    public static ExecutorService newDaemonFixedThreadExecutor(String threadName, int threadsNum) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadName).build();
        return Executors.newFixedThreadPool(threadsNum, threadFactory);
    }

    public static void scheduleAtFixedRate(Runnable runnable, int period) {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(runnable, 1, period, TimeUnit.SECONDS);
    }
}
