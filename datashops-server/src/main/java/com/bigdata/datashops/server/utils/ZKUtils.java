package com.bigdata.datashops.server.utils;

import com.bigdata.datashops.common.Constants;

public class ZKUtils {
    public static String getRootPath() {
        return Constants.ZK_ROOT;
    }

    public static String getMasterPath() {
        return getRootPath() + Constants.ZK_MASTER_NODE;
    }

    public static String getMasterRegistryPath() {
        return getMasterPath() + Constants.ZK_REGISTRY;
    }

    public static String getWorkerPath() {
        return getRootPath() + Constants.ZK_WORKER_NODE;
    }

    public static String getWorkerRegistryPath() {
        return getWorkerPath() + Constants.ZK_REGISTRY;
    }

    public static String getQueuePath() {
        return getRootPath() + Constants.ZK_JOB_QUEUE;
    }

    public static String getWorkerMetaPath() {
        return getWorkerPath() + Constants.ZK_WORKER_META;
    }
}