package com.bigdata.datashops.server.master.dispatch.selector;

import java.util.List;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.server.master.dispatch.SelectorContext;

public abstract class HostManager {
    protected List<Host> hosts;

    public abstract Host select(SelectorContext context);
}
