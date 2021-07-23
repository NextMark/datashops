package com.bigdata.datashops.plugin.selector;

import java.util.Collection;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.google.common.collect.Iterables;

public class AssignSelector implements Selector<Host> {

    @Override
    public Host select(Collection<Host> hosts) {
        return Iterables.get(hosts, 0);
    }
}
