package com.bigdata.datashops.server.master.selector;

import java.util.Collection;

import com.bigdata.datashops.model.pojo.rpc.Host;

public class AssignSelector extends AbstractSelector<Host> {

    @Override
    protected Host doSelect(Collection<Host> hosts) {
        return (Host) hosts.toArray()[0];
    }
}
