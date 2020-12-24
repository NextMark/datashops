package com.bigdata.datashops.server.master.dispatch.selector;

import java.util.Random;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.server.master.dispatch.SelectorContext;

public class RandomHostSelector extends HostManager {

    @Override
    public Host select(SelectorContext context) {
        Random rnd = new Random();
        int i = rnd.nextInt(hosts.size());
        return hosts.get(i);
    }
}
