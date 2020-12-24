package com.bigdata.datashops.server.master.dispatch;

import com.bigdata.datashops.model.pojo.rpc.Host;

import lombok.Data;

@Data
public class SelectorContext {
    private Host host;
}
