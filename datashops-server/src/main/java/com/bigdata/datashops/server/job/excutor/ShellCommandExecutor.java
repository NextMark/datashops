package com.bigdata.datashops.server.job.excutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.ShellData;
import com.bigdata.datashops.server.job.JobContext;

public class ShellCommandExecutor extends CommandExecutor {
    public ShellCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s/shell-job.%s", jobContext.getExecutePath(),
                jobContext.getJobInstance().getInstanceId(), "sh");
    }

    @Override
    public String commandInterpreter() {
        return "bash";
    }

    @Override
    public List<String> commandArgs() {
        return null;
    }

    @Override
    public void buildCommandFile() throws IOException {
        ShellData shellData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), ShellData.class);
        FileUtils.writeStringToFile(new File(buildCommandFilePath()), shellData.getValue(), StandardCharsets.UTF_8);
    }

}
