package com.bigdata.datashops.server.job.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public abstract List<String> commandArgs();

    public abstract void buildCommandFile() throws IOException;

    public CommandResult run() {
        CommandResult result = new CommandResult();
        String commandFilePath = buildCommandFilePath();
        String log = "Process job end, name=%s, instanceId=%s, status=%s, exitValue=%s";

        try {
            buildCommandFile();
            buildProcess(commandFilePath);
            Integer processId = getProcessId(process);
            LOG.info("Process start, processId={}", processId);
            result.setProcessId(processId);
            readInputStream();
            readErrorStream();
            boolean status = process.waitFor(jobContext.getJobInstance().getJob().getTimeout(), TimeUnit.SECONDS);
            if (status) {
                LOG.info(String.format(log, jobContext.getJobInstance().getName(),
                        jobContext.getJobInstance().getInstanceId(), true, process.exitValue()));
                result.setExistCode(Constants.RPC_JOB_SUCCESS);
            } else {
                LOG.info(String.format(log, jobContext.getJobInstance().getName(),
                        jobContext.getJobInstance().getInstanceId(), false, process.exitValue()));
                result.setExistCode(Constants.RPC_JOB_FAIL);
            }
            FileUtils.deleteFile(commandFilePath);
        } catch (IllegalStateException e) {
            LOG.error(String.format(log, jobContext.getJobInstance().getName(),
                    jobContext.getJobInstance().getInstanceId(), false, process.exitValue()), e);
            result.setExistCode(Constants.RPC_JOB_TIMEOUT_FAIL);
        } catch (InterruptedException | IOException e) {
            LOG.error(String.format(log, jobContext.getJobInstance().getName(),
                    jobContext.getJobInstance().getInstanceId(), false, process.exitValue()), e);
            result.setExistCode(Constants.RPC_JOB_FAIL);
        }
        return result;
    }

    private void buildProcess(String commandFile) throws IOException {
        List<String> command = new LinkedList<>();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(jobContext.getExecutePath()));
        //processBuilder.redirectErrorStream(true);

        if (jobContext.getExecuteUser() != null) {
            command.add("sudo");
            command.add("-u");
            command.add(jobContext.getExecuteUser());
        }
        command.add(commandInterpreter());
        command.addAll(commandArgs());
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

    private void readInputStream() {
        new Thread(() -> {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    LOG.info("std output: {}", line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void readErrorStream() {
        new Thread(() -> {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    LOG.error(String.format("script std err: %s", line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
