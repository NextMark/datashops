package com.bigdata.datashops.server.master.selector;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.enums.HostSelector;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;

@Component
public class SelectorManager {
    public AbstractSelector create(JobInstance instance, List<Host> hosts) {
        HostSelector selector = HostSelector.of(instance.getJob().getHostSelector());
        switch (selector) {
            case ASSIGN:
                Host host = new Host();
                host.setIp(instance.getJob().getHost());
                host.setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT));
                if (!hosts.contains(host)) {
                    return new RandomSelector();
                }
                return new AssignSelector(instance);
            case WEIGHT:
                return new LowerWeightSelector();
            default:
                return new RandomSelector();
        }
    }
}
