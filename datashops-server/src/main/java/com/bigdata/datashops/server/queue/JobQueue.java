package com.bigdata.datashops.server.queue;

import java.util.concurrent.PriorityBlockingQueue;

import com.bigdata.datashops.model.pojo.JobInstance;

public class JobQueue {
    private static JobQueue instance;

    private PriorityBlockingQueue<JobInstance> runJobQ = new PriorityBlockingQueue<>(20);

    public static JobQueue getInstance() {
        if (instance == null) {
            instance = new JobQueue();
        }
        return instance;
    }

    public PriorityBlockingQueue<JobInstance> getQueue() {
        return runJobQ;
    }
}
