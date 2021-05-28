package com.bigdata.datashops.server.master.selector;

import java.util.Collection;

public interface Selector<T> {
    T select(Collection<T> hosts);
}
