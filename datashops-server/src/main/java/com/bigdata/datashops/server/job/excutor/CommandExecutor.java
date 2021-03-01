package com.bigdata.datashops.server.job.excutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.server.job.CommandResult;
import com.bigdata.datashops.server.job.JobContext;

public abstract class CommandExecutor {
    protected Logger LOG;

    protected JobContext jobContext;

    private Process process;

    public CommandExecutor(JobContext jobContext) {
        this.jobContext = jobContext;
        this.LOG = jobContext.getLogger();
    }

    public abstract String buildCommandFilePath();

    public abstract String commandInterpreter();

    public CommandResult run(String command) throws IOException, InterruptedException {
        CommandResult result = new CommandResult();
        String commandFilePath = buildCommandFilePath();
        commandToFile(command, commandFilePath);
        buildProcess(commandFilePath);
        Integer processId = getProcessId(process);

        LOG.info("process start, process id is: {}", processId);

        boolean status = process.waitFor(jobContext.getJobInstance().getJob().getTimeout(), TimeUnit.MILLISECONDS);

        result.setProcessId(processId);
        LOG.info("process has exited, execute path:{}, processId:{} ,status:{}", jobContext.getExecutePath(), processId,
                status);
        if (status) {
            LOG.info("Process job success, projectId:{}, instanceId:{}", jobContext.getJobInstance().getProjectId(),
                    jobContext.getJobInstance().getInstanceId());
            result.setExistCode(Constants.EXIT_SUCCESS_CODE);
        } else {
            LOG.error("Process job failed {}, projectId:{}, instanceId:{}", process.exitValue(),
                    jobContext.getJobInstance().getProjectId(), jobContext.getJobInstance().getInstanceId());
            result.setExistCode(Constants.EXIT_FAIL_CODE);
        }
        FileUtils.deleteFile(commandFilePath);
        return result;
    }

    private void buildProcess(String commandFile) throws IOException {
        List<String> command = new LinkedList<>();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(jobContext.getExecutePath()));
        processBuilder.redirectErrorStream(true);

        //        command.add("sudo");
        //        command.add("-u");
        //        command.add(jobContext.getExecuteUser());
        command.add(commandInterpreter());
        command.add(commandFile);

        processBuilder.command(command);
        process = processBuilder.start();

    }

    private void commandToFile(String command, String file) throws IOException {
        FileUtils.writeStringToFile(new File(file), command, StandardCharsets.UTF_8);

    }

    private int getProcessId(Process process) {
        int processId = 0;

        try {
            Field f = process.getClass().getDeclaredField(Constants.PID);
            f.setAccessible(true);

            processId = f.getInt(process);
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }

        return processId;
    }

}
