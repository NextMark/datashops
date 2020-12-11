package com.bigdata.datashops.server.master.dispatch;

import java.util.Collection;
import java.util.Random;

public class RandomHostSelector implements HostSelector {

    @Override
    public Object select(Collection hosts) {
        Random rnd = new Random();
        int i = rnd.nextInt(hosts.size());
        return hosts.toArray()[i];
    }
}
