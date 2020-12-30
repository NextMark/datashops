package com.bigdata.datashops.server.master.dispatch.selector;

import java.util.List;

import com.bigdata.datashops.model.pojo.rpc.Host;

public abstract class HostManager {
    protected List<Host> hosts;

    public abstract Host select();
}
