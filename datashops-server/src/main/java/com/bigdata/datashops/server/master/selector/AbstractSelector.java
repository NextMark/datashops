package com.bigdata.datashops.server.master.selector;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import com.bigdata.datashops.model.pojo.job.JobInstance;

public abstract class AbstractSelector<T> implements Selector<T> {
    protected JobInstance jobInstance;

    @Override
    public T select(Collection<T> hosts) {

        if (CollectionUtils.isEmpty(hosts)) {
            throw new IllegalArgumentException("Worker empty.");
        }

        if (hosts.size() == 1) {
            return (T) hosts.toArray()[0];
        }
        return doSelect(hosts);
    }

    protected abstract T doSelect(Collection<T> hosts);

}
