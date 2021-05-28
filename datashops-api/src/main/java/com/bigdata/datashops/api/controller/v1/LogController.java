package com.bigdata.datashops.api.controller.v1;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;

@RestController
@RequestMapping("/v1/log")
public class LogController extends BasicController {
    @RequestMapping(value = "/rollReadLog")
    public Result rollReadLog(@NotNull String instanceId, @NotNull Integer skipLines, @NotNull Integer limit) {
        JobInstance instance = jobInstanceService.findJobInstance("instanceId=" + instanceId);
        Host host = new Host();
        host.setIp(instance.getHost());
        host.setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT));
        String filePath = String.format("%s%s%s", FileUtils.getJobExecLogDir(), instanceId, ".log");
        String content = logRequestProcessor.rollReadLog(filePath, skipLines, limit, host);
        return ok(content);
    }

    @RequestMapping(value = "/readYarnLog")
    public Result readYarnLog(@NotNull String instanceId, @NotNull Integer skipLines, @NotNull Integer limit)
            throws IOException {
        hadoopService.readLog("application_1614320942413_698804");
        return ok();
    }
}
