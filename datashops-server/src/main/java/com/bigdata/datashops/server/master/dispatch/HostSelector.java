package com.bigdata.datashops.server.master.dispatch;

import java.util.Collection;

public interface HostSelector<T> {
    T select(Collection<T> hosts);
}
