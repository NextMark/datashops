package com.bigdata.datashops.server.job.excutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.PythonData;
import com.bigdata.datashops.server.job.JobContext;

public class PythonCommandExecutor extends CommandExecutor {

    public PythonCommandExecutor(JobContext jobContext) {
        super(jobContext);
        pythonData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), PythonData.class);
    }

    private PythonData pythonData;

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s/python-job.%s", jobContext.getExecutePath(),
                jobContext.getJobInstance().getInstanceId(), "py");
    }

    @Override
    public String commandInterpreter() {
        return pythonData.getVersion();
    }

    @Override
    public List<String> commandArgs() {
        return Lists.newArrayList();
    }

    @Override
    public void buildCommandFile() throws IOException {
        FileUtils.writeStringToFile(new File(buildCommandFilePath()), pythonData.getValue(), StandardCharsets.UTF_8);
    }
}
