package com.bigdata.datashops.server.master.selector;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.google.common.collect.Lists;

public class WeightSelector extends AbstractSelector<Host> {

    @Override
    protected Host doSelect(Collection<Host> hosts) {
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
