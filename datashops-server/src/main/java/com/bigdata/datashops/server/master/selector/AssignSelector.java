package com.bigdata.datashops.server.master.selector;

import java.util.Collection;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;

public class AssignSelector extends AbstractSelector<Host> {

    public AssignSelector(JobInstance instance) {
        this.jobInstance = instance;
    }

    @Override
    protected Host doSelect(Collection<Host> hosts) {
        Host host = new Host();
        host.setIp(jobInstance.getJob().getHost());
        host.setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT));
        return host;
    }
}
