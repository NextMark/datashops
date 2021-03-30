package com.bigdata.datashops.api.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.model.pojo.rpc.Host;

@RestController
@RequestMapping("/v1/log")
public class LogController extends BasicController {
    @RequestMapping(value = "/rollReadLog")
    public Result rollReadLog(String instanceId, int skip, int limit) {
        String filePath = String.format("%s%s%s", FileUtils.getJobExecLogDir(), instanceId, ".log");
        logRequestProcessor.rollReadLog(filePath, skip, limit, new Host());
        return ok();
    }
}
