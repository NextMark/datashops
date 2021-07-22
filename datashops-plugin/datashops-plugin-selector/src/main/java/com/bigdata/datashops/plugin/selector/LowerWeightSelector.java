package com.bigdata.datashops.plugin.selector;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.model.pojo.rpc.HostWeight;
import com.google.common.collect.Lists;

public class LowerWeightSelector implements WorkerSelector<Host> {

    @Override
    public Host select(Collection<Host> hosts) {
        List<HostWeight> hostWeightList = Lists.newArrayList();
        for (Host host : hosts) {
            HostWeight hostWeight =
                    new HostWeight(host, host.getOsInfo().getMemoryUsage(), host.getOsInfo().getLoadAverage());
            hostWeightList.add(hostWeight);
        }
        HostWeight hostWeight = hostWeightList.stream().min(Comparator.comparingDouble(HostWeight::getWeight)).get();
        return hostWeight.getHost();
    }
}
