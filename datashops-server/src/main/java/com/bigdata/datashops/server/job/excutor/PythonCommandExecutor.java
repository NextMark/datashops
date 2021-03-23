package com.bigdata.datashops.server.job.excutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.PythonData;
import com.bigdata.datashops.server.job.JobContext;

public class PythonCommandExecutor extends CommandExecutor {

    public PythonCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s/python-job.%s", jobContext.getExecutePath(),
                jobContext.getJobInstance().getInstanceId(), "py");
    }

    @Override
    public String commandInterpreter() {
        return "python";
    }

    @Override
    public List<String> commandArgs() {
        return null;
    }

    @Override
    public void buildCommandFile() throws IOException {
        PythonData data = JSONUtils.parseObject(jobContext.getJobInstance().getData(), PythonData.class);
        FileUtils.writeStringToFile(new File(buildCommandFilePath()), data.getValue(), StandardCharsets.UTF_8);
    }
}
