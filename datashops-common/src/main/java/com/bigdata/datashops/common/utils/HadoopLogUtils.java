package com.bigdata.datashops.common.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.logaggregation.AggregatedLogFormat;
import org.apache.hadoop.yarn.logaggregation.LogAggregationUtils;
import org.apache.hadoop.yarn.logaggregation.LogCLIHelpers;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Times;

public class HadoopLogUtils {
    private static Configuration yarnConfiguration;
    private static LogCLIHelpers logCLIHelpers;

    /**
     * 初始化配置
     */
    static {
        yarnConfiguration = new YarnConfiguration();

        yarnConfiguration.addResource("core-site.xml");
        yarnConfiguration.addResource("hdfs-site.xml");
        yarnConfiguration.addResource("yarn-site.xml");
        logCLIHelpers = new LogCLIHelpers();
        logCLIHelpers.setConf(yarnConfiguration);
    }

    public static Configuration getYarnConfiguration() {
        return yarnConfiguration;
    }

//    public static int dumpAContainersLogs(String appId, String containerId, String nodeId, String jobOwner,
//                                          PrintStream out, List<String> logType) throws IOException {
//        Path remoteRootLogDir =
//                new Path(getYarnConfiguration().get("yarn.nodemanager.remote-app-log-dir", "/tmp/logs"));
//
//        String suffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(getYarnConfiguration());
//        Path remoteAppLogDir = LogAggregationUtils
//                                       .getRemoteAppLogDir(remoteRootLogDir, ConverterUtils.toApplicationId(appId),
//                                               jobOwner, suffix);
//
//        RemoteIterator nodeFiles;
//        try {
//            Path qualifiedLogDir = FileContext.getFileContext(getYarnConfiguration()).makeQualified(remoteAppLogDir);
//            nodeFiles = FileContext.getFileContext(qualifiedLogDir.toUri(), getYarnConfiguration())
//                                .listStatus(remoteAppLogDir);
//        } catch (FileNotFoundException var16) {
//            return -1;
//        }
//
//        boolean foundContainerLogs = false;
//
//        while (nodeFiles.hasNext()) {
//            FileStatus thisNodeFile = (FileStatus) nodeFiles.next();
//            String fileName = thisNodeFile.getPath().getName();
//            if (fileName.contains(LogAggregationUtils.getNodeString(nodeId)) && !fileName.endsWith(".tmp")) {
//                AggregatedLogFormat.LogReader reader = null;
//
//                try {
//                    reader = new AggregatedLogFormat.LogReader(getYarnConfiguration(), thisNodeFile.getPath());
//                    if (dumpAContainerLogs(containerId, reader, out, thisNodeFile.getModificationTime(), logType)
//                                > -1) {
//                        foundContainerLogs = true;
//                    }
//                } finally {
//                    if (reader != null) {
//                        reader.close();
//                    }
//
//                }
//            }
//        }
//        if (!foundContainerLogs) {
//            return -1;
//        } else {
//            return 0;
//        }
//    }

    public static int dumpAContainerLogs(String containerIdStr, AggregatedLogFormat.LogReader reader, PrintStream out,
                                         long logUploadedTime, List<String> logType) throws IOException {
        AggregatedLogFormat.LogKey key = new AggregatedLogFormat.LogKey();
        DataInputStream valueStream;
        for (valueStream = reader.next(key); valueStream != null && !key.toString().equals(containerIdStr);
             valueStream = reader.next(key)) {
            key = new AggregatedLogFormat.LogKey();
        }
        if (valueStream == null) {
            return -1;
        } else {
            boolean foundContainerLogs = false;
            while (true) {
                try {
                    readContainerLogs(valueStream, out, logUploadedTime, logType);
                    foundContainerLogs = true;
                } catch (EOFException var10) {
                    if (foundContainerLogs) {
                        return 0;
                    }
                    return -1;
                }
            }
        }
    }

//    public static Map<String, String> getContaines(String appId, String appOwner) throws IOException {
//        Path remoteRootLogDir = new Path(yarnConfiguration.get(YarnConfiguration.NM_REMOTE_APP_LOG_DIR,
//                YarnConfiguration.DEFAULT_NM_REMOTE_APP_LOG_DIR));
//        String user = appOwner;
//        String logDirSuffix = LogAggregationUtils.getRemoteNodeLogDirSuffix(yarnConfiguration);
//        // TODO Change this to get a list of files from the LAS.
//        Path remoteAppLogDir = LogAggregationUtils
//                                       .getRemoteAppLogDir(remoteRootLogDir, ConverterUtils.toApplicationId(appId),
//                                               user, logDirSuffix);
//        RemoteIterator<FileStatus> nodeFiles;
//        Map<String, String> containerAndNodeId = new LinkedHashMap<>();
//        try {
//            Path qualifiedLogDir = FileContext.getFileContext(yarnConfiguration).makeQualified(remoteAppLogDir);
//            nodeFiles =
//                    FileContext.getFileContext(qualifiedLogDir.toUri(), yarnConfiguration).listStatus(remoteAppLogDir);
//        } catch (FileNotFoundException fnf) {
//            return Collections.emptyMap();
//        }
//        boolean foundAnyLogs = false;
//        while (nodeFiles.hasNext()) {
//            FileStatus thisNodeFile = nodeFiles.next();
//            if (!thisNodeFile.getPath().getName().endsWith(LogAggregationUtils.TMP_FILE_SUFFIX)) {
//                AggregatedLogFormat.LogReader reader =
//                        new AggregatedLogFormat.LogReader(yarnConfiguration, thisNodeFile.getPath());
//                try {
//                    DataInputStream valueStream;
//                    AggregatedLogFormat.LogKey key = new AggregatedLogFormat.LogKey();
//                    valueStream = reader.next(key);
//
//                    while (valueStream != null) {
//                        containerAndNodeId.put(key.toString(), thisNodeFile.getPath().getName().replace("_", ":"));
//                        foundAnyLogs = true;
//                        key = new AggregatedLogFormat.LogKey();
//                        valueStream = reader.next(key);
//                    }
//                } finally {
//                    reader.close();
//                }
//            }
//        }
//        if (!foundAnyLogs) {
//            return Collections.emptyMap();
//        }
//        return containerAndNodeId;
//    }

    private static void readContainerLogs(DataInputStream valueStream, PrintStream out, long logUploadedTime,
                                          List<String> logType) throws IOException {
        byte[] buf = new byte[65535];

        String fileType = valueStream.readUTF();
        //if (logType.contains(fileType)) {
        String fileLengthStr = valueStream.readUTF();
        long fileLength = Long.parseLong(fileLengthStr);
        out.print("LogType:");
        out.println(fileType);
        if (logUploadedTime != -1) {
            out.print("Log Upload Time:");
            out.println(Times.format(logUploadedTime));
        }
        out.print("LogLength:");
        out.println(fileLengthStr);
        out.println("Log Contents:");

        long curRead = 0;
        long pendingRead = fileLength - curRead;
        int toRead = pendingRead > buf.length ? buf.length : (int) pendingRead;
        int len = valueStream.read(buf, 0, toRead);
        while (len != -1 && curRead < fileLength) {
            out.write(buf, 0, len);
            curRead += len;

            pendingRead = fileLength - curRead;
            toRead = pendingRead > buf.length ? buf.length : (int) pendingRead;
            len = valueStream.read(buf, 0, toRead);
        }
        out.println("End of LogType:" + fileType);
        out.println("");
        // }
    }
}
