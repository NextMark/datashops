package com.bigdata.datashops.server.master.dispatch.selector;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.server.master.dispatch.SelectorContext;

public class AppointSelector extends HostManager {
    @Override
    public Host select(SelectorContext context) {
        return context.getHost();
    }
}
