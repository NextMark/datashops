package com.bigdata.datashops.model.pojo.rpc;

import java.util.Objects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Host {
    private String address;

    private String ip;

    private int port;

    private String getAddress() {
        return this.ip + ":" + this.port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Host host = (Host) o;
        return Objects.equals(getAddress(), host.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }

    @Override
    public String toString() {
        return "Host{address='" + address + "'}";
    }
}
