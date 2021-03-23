package com.bigdata.datashops.server.job;

import com.bigdata.datashops.common.Constants;

public class SqoopJob extends AbstractJob {
    @Override
    protected void process() throws Exception {
        buildGrpcRequest(Constants.RPC_JOB_SUCCESS);

    }
}
