package com.bigdata.datashops.plugin.selector;

import java.util.Collection;

import org.apache.dubbo.common.extension.SPI;

@SPI("random")
public interface WorkerSelector<T> {

    T select(Collection<T> hosts);
}
