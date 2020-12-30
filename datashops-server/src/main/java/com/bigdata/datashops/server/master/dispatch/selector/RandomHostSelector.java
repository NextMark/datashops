package com.bigdata.datashops.server.master.dispatch.selector;

import java.util.List;
import java.util.Random;

import com.bigdata.datashops.model.pojo.rpc.Host;

public class RandomHostSelector extends HostManager {

    public RandomHostSelector(List<Host> hosts) {
        this.hosts = hosts;
    }

    @Override
    public Host select() {
        Random rnd = new Random();
        int i = rnd.nextInt(hosts.size());
        return hosts.get(i);
    }
}
