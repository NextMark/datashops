package com.bigdata.datashops.plugin.selector;

import java.util.Collection;
import java.util.Random;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.google.common.collect.Iterables;

public class RandomSelector implements WorkerSelector<Host> {

    @Override
    public Host select(Collection<Host> hosts) {
        Random rnd = new Random();
        int i = rnd.nextInt(hosts.size());
        return Iterables.get(hosts, i);
    }
}
