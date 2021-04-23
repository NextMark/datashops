package com.bigdata.datashops.server.master.selector;

import com.bigdata.datashops.model.pojo.rpc.Host;

import lombok.Data;

@Data
public class HostWeight {

    private final int MEMORY_FACTOR = 20;

    private final int LOAD_AVERAGE_FACTOR = 70;

    private final Host host;

    private final double weight;

    private double currentWeight;

    public HostWeight(Host host, double memory, double loadAverage) {
        this.host = host;
        this.weight = getWeight(memory, loadAverage);
        this.currentWeight = weight;
    }

    @Override
    public String toString() {
        return "HostWeight{" + "host=" + host + ", weight=" + weight + ", currentWeight=" + currentWeight + '}';
    }

    private double getWeight(double memory, double loadAverage) {
        return memory * MEMORY_FACTOR + loadAverage * LOAD_AVERAGE_FACTOR;
    }

}
