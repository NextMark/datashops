package com.bigdata.datashops.server.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadUtil {
    public static ExecutorService newDaemonFixedThreadExecutor(String threadName, int threadsNum){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                                              .setDaemon(true)
                                              .setNameFormat(threadName)
                                              .build();
        return Executors.newFixedThreadPool(threadsNum, threadFactory);
    }
}
