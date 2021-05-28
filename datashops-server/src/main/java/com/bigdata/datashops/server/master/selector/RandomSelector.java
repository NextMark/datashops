package com.bigdata.datashops.server.master.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.bigdata.datashops.model.pojo.rpc.Host;

public class RandomSelector extends AbstractSelector<Host> {

    @Override
    protected Host doSelect(Collection<Host> hosts) {
        List<Host> data = new ArrayList<>(hosts);
        Random rnd = new Random();
        int i = rnd.nextInt(data.size());
        return data.get(i);
    }
}
