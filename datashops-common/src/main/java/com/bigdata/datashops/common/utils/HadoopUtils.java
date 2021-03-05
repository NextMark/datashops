package com.bigdata.datashops.common.utils;

import static com.bigdata.datashops.common.Constants.RESOURCE_UPLOAD_PATH;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.client.cli.RMAdminCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.enums.ExecutionStatus;
import com.bigdata.datashops.common.enums.ResUploadType;
import com.bigdata.datashops.common.enums.ResourceType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class HadoopUtils implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopUtils.class);

    private static String hdfsUser = PropertyUtils.getString(Constants.HDFS_ROOT_USER);
    public static final String resourceUploadPath = PropertyUtils.getString(RESOURCE_UPLOAD_PATH, "/tmp/datashops");
    public static final String rmHaIds = PropertyUtils.getString(Constants.YARN_RESOURCEMANAGER_HA_RM_IDS);
    public static final String appAddress = PropertyUtils.getString(Constants.YARN_APPLICATION_STATUS_ADDRESS);
    public static final String jobHistoryAddress = PropertyUtils.getString(Constants.YARN_JOB_HISTORY_STATUS_ADDRESS);

    private static final String HADOOP_UTILS_KEY = "HADOOP_UTILS_KEY";

    private static final LoadingCache<String, HadoopUtils> cache = CacheBuilder.newBuilder().expireAfterWrite(
            PropertyUtils.getInt(Constants.KERBEROS_EXPIRE_TIME, 2), TimeUnit.HOURS)
                                                                           .build(new CacheLoader<String,
                                                                                                         HadoopUtils>() {
                                                                               @Override
                                                                               public HadoopUtils load(String key) {
                                                                                   return new HadoopUtils();
                                                                               }
                                                                           });

    private static volatile boolean yarnEnabled = false;

    private Configuration configuration;
    private FileSystem fs;

    private HadoopUtils() {
        init();
        initHdfsPath();
    }

    public static HadoopUtils getInstance() {

        return cache.getUnchecked(HADOOP_UTILS_KEY);
    }

    /**
     * init dolphinscheduler root path in hdfs
     */

    private void initHdfsPath() {
        Path path = new Path(resourceUploadPath);

        try {
            if (!fs.exists(path)) {
                fs.mkdirs(path);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * init hadoop configuration
     */
    private void init() {
        try {
            configuration = new Configuration();

            String resourceStorageType = PropertyUtils.getUpperCaseString(Constants.RESOURCE_STORAGE_TYPE);
            ResUploadType resUploadType = ResUploadType.valueOf(resourceStorageType);

            if (resUploadType == ResUploadType.HDFS) {
                if (PropertyUtils.getBoolean(Constants.HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false)) {
                    System.setProperty(Constants.JAVA_SECURITY_KRB5_CONF,
                            PropertyUtils.getString(Constants.JAVA_SECURITY_KRB5_CONF_PATH));
                    configuration.set(Constants.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
                    hdfsUser = "";
                    UserGroupInformation.setConfiguration(configuration);
                    UserGroupInformation
                            .loginUserFromKeytab(PropertyUtils.getString(Constants.LOGIN_USER_KEY_TAB_USERNAME),
                                    PropertyUtils.getString(Constants.LOGIN_USER_KEY_TAB_PATH));
                }

                String defaultFS = configuration.get(Constants.FS_DEFAULTFS);
                //first get key from core-site.xml hdfs-site.xml ,if null ,then try to get from properties file
                // the default is the local file system
                if (defaultFS.startsWith("file")) {
                    String defaultFSProp = PropertyUtils.getString(Constants.FS_DEFAULTFS);
                    if (StringUtils.isNotBlank(defaultFSProp)) {
                        Map<String, String> fsRelatedProps = PropertyUtils.getPrefixedProperties("fs.");
                        configuration.set(Constants.FS_DEFAULTFS, defaultFSProp);
                        fsRelatedProps.forEach((key, value) -> configuration.set(key, value));
                    } else {
                        LOG.error("property:{} can not to be empty, please set!", Constants.FS_DEFAULTFS);
                        throw new RuntimeException(
                                String.format("property: %s can not to be empty, please set!", Constants.FS_DEFAULTFS));
                    }
                } else {
                    LOG.info("get property:{} -> {}, from core-site.xml hdfs-site.xml ", Constants.FS_DEFAULTFS,
                            defaultFS);
                }

                if (fs == null) {
                    if (StringUtils.isNotEmpty(hdfsUser)) {
                        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(hdfsUser);
                        ugi.doAs((PrivilegedExceptionAction<Boolean>) () -> {
                            fs = FileSystem.get(configuration);
                            return true;
                        });
                    } else {
                        LOG.warn("hdfs.root.user is not set value!");
                        fs = FileSystem.get(configuration);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * @return Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * get application url
     *
     * @param applicationId application id
     * @return url of application
     */
    public String getApplicationUrl(String applicationId) throws Exception {
        /**
         * if rmHaIds contains xx, it signs not use resourcemanager
         * otherwise:
         *  if rmHaIds is empty, single resourcemanager enabled
         *  if rmHaIds not empty: resourcemanager HA enabled
         */
        String appUrl = "";

        if (StringUtils.isEmpty(rmHaIds)) {
            //single resourcemanager enabled
            appUrl = appAddress;
            yarnEnabled = true;
        } else {
            //resourcemanager HA enabled
            appUrl = getAppAddress(appAddress, rmHaIds);
            yarnEnabled = true;
            LOG.info("application url : {}", appUrl);
        }

        if (StringUtils.isBlank(appUrl)) {
            throw new Exception("application url is blank");
        }
        return String.format(appUrl, applicationId);
    }

    public String getJobHistoryUrl(String applicationId) {
        //eg:application_1587475402360_712719 -> job_1587475402360_712719
        String jobId = applicationId.replace("application", "job");
        return String.format(jobHistoryAddress, jobId);
    }

    /**
     * cat file on hdfs
     *
     * @param hdfsFilePath hdfs file path
     * @return byte[] byte array
     * @throws IOException errors
     */
    public byte[] catFile(String hdfsFilePath) throws IOException {

        if (StringUtils.isBlank(hdfsFilePath)) {
            LOG.error("hdfs file path:{} is blank", hdfsFilePath);
            return new byte[0];
        }

        try (FSDataInputStream fsDataInputStream = fs.open(new Path(hdfsFilePath))) {
            return IOUtils.toByteArray(fsDataInputStream);
        }
    }

    /**
     * cat file on hdfs
     *
     * @param hdfsFilePath hdfs file path
     * @param skipLineNums skip line numbers
     * @param limit        read how many lines
     * @return content of file
     * @throws IOException errors
     */
    public List<String> catFile(String hdfsFilePath, int skipLineNums, int limit) throws IOException {

        if (StringUtils.isBlank(hdfsFilePath)) {
            LOG.error("hdfs file path:{} is blank", hdfsFilePath);
            return Collections.emptyList();
        }

        try (FSDataInputStream in = fs.open(new Path(hdfsFilePath))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Stream<String> stream = br.lines().skip(skipLineNums).limit(limit);
            return stream.collect(Collectors.toList());
        }

    }

    /**
     * make the given file and all non-existent parents into
     * directories. Has the semantics of Unix 'mkdir -p'.
     * Existence of the directory hierarchy is not an error.
     *
     * @param hdfsPath path to create
     * @return mkdir result
     * @throws IOException errors
     */
    public boolean mkdir(String hdfsPath) throws IOException {
        return fs.mkdirs(new Path(hdfsPath));
    }

    /**
     * copy files between FileSystems
     *
     * @param srcPath      source hdfs path
     * @param dstPath      destination hdfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     * @return if success or not
     * @throws IOException errors
     */
    public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        return FileUtil.copy(fs, new Path(srcPath), fs, new Path(dstPath), deleteSource, overwrite, fs.getConf());
    }

    /**
     * the src file is on the local disk.  Add it to FS at
     * the given dst name.
     *
     * @param srcFile      local file
     * @param dstHdfsPath  destination hdfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     * @return if success or not
     * @throws IOException errors
     */
    public boolean copyLocalToHdfs(String srcFile, String dstHdfsPath, boolean deleteSource, boolean overwrite)
            throws IOException {
        Path srcPath = new Path(srcFile);
        Path dstPath = new Path(dstHdfsPath);

        fs.copyFromLocalFile(deleteSource, overwrite, srcPath, dstPath);

        return true;
    }

    /**
     * copy hdfs file to local
     *
     * @param srcHdfsFilePath source hdfs file path
     * @param dstFile         destination file
     * @param deleteSource    delete source
     * @param overwrite       overwrite
     * @return result of copy hdfs file to local
     * @throws IOException errors
     */
    public boolean copyHdfsToLocal(String srcHdfsFilePath, String dstFile, boolean deleteSource, boolean overwrite)
            throws IOException {
        Path srcPath = new Path(srcHdfsFilePath);
        File dstPath = new File(dstFile);

        if (dstPath.exists()) {
            if (dstPath.isFile()) {
                if (overwrite) {
                    Files.delete(dstPath.toPath());
                }
            } else {
                LOG.error("destination file must be a file");
            }
        }

        if (!dstPath.getParentFile().exists()) {
            dstPath.getParentFile().mkdirs();
        }

        return FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
    }

    /**
     * delete a file
     *
     * @param hdfsFilePath the path to delete.
     * @param recursive    if path is a directory and set to
     *                     true, the directory is deleted else throws an exception. In
     *                     case of a file the recursive can be set to either true or false.
     * @return true if delete is successful else false.
     * @throws IOException errors
     */
    public boolean delete(String hdfsFilePath, boolean recursive) throws IOException {
        return fs.delete(new Path(hdfsFilePath), recursive);
    }

    /**
     * check if exists
     *
     * @param hdfsFilePath source file path
     * @return result of exists or not
     * @throws IOException errors
     */
    public boolean exists(String hdfsFilePath) throws IOException {
        return fs.exists(new Path(hdfsFilePath));
    }

    /**
     * Gets a list of files in the directory
     *
     * @param filePath file path
     * @return {@link FileStatus} file status
     * @throws Exception errors
     */
    public FileStatus[] listFileStatus(String filePath) throws Exception {
        try {
            return fs.listStatus(new Path(filePath));
        } catch (IOException e) {
            LOG.error("Get file list exception", e);
            throw new Exception("Get file list exception", e);
        }
    }

    /**
     * Renames Path src to Path dst.  Can take place on local fs
     * or remote DFS.
     *
     * @param src path to be renamed
     * @param dst new path after rename
     * @return true if rename is successful
     * @throws IOException on failure
     */
    public boolean rename(String src, String dst) throws IOException {
        return fs.rename(new Path(src), new Path(dst));
    }

    /**
     * hadoop resourcemanager enabled or not
     *
     * @return result
     */
    public boolean isYarnEnabled() {
        return yarnEnabled;
    }

    /**
     * get the state of an application
     *
     * @param applicationId application id
     * @return the return may be null or there may be other parse exceptions
     */
    public ExecutionStatus getApplicationStatus(String applicationId) throws Exception {
        if (StringUtils.isEmpty(applicationId)) {
            return null;
        }

        String result = null;
        String applicationUrl = getApplicationUrl(applicationId);
        LOG.info("applicationUrl={}", applicationUrl);

        String responseContent;
        if (PropertyUtils.getBoolean(Constants.HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false)) {
            responseContent = KerberosHttpClient.get(applicationUrl);
        } else {
            responseContent = HttpUtils.get(applicationUrl);
        }
        if (responseContent != null) {
            ObjectNode jsonObject = JSONUtils.parseObject(responseContent);
            if (!jsonObject.has("app")) {
                return ExecutionStatus.FAILURE;
            }
            result = jsonObject.path("app").path("finalStatus").asText();

        } else {
            //may be in job history
            String jobHistoryUrl = getJobHistoryUrl(applicationId);
            LOG.info("jobHistoryUrl={}", jobHistoryUrl);
            responseContent = HttpUtils.get(jobHistoryUrl);
            if (null != responseContent) {
                ObjectNode jsonObject = JSONUtils.parseObject(responseContent);
                if (!jsonObject.has("job")) {
                    return ExecutionStatus.FAILURE;
                }
                result = jsonObject.path("job").path("state").asText();
            } else {
                return ExecutionStatus.FAILURE;
            }
        }

        switch (result) {
            case Constants.ACCEPTED:
                return ExecutionStatus.SUBMITTED_SUCCESS;
            case Constants.SUCCEEDED:
                return ExecutionStatus.SUCCESS;
            case Constants.NEW:
            case Constants.NEW_SAVING:
            case Constants.SUBMITTED:
            case Constants.FAILED:
                return ExecutionStatus.FAILURE;
            case Constants.KILLED:
                return ExecutionStatus.KILL;

            case Constants.RUNNING:
            default:
                return ExecutionStatus.RUNNING_EXECUTION;
        }
    }

    /**
     * get data hdfs path
     *
     * @return data hdfs path
     */
    public static String getHdfsDataBasePath() {
        if ("/".equals(resourceUploadPath)) {
            // if basepath is configured to /,  the generated url may be  //default/resources (with extra leading /)
            return "";
        } else {
            return resourceUploadPath;
        }
    }

    /**
     * hdfs resource dir
     *
     * @param tenantCode   tenant code
     * @param resourceType resource type
     * @return hdfs resource dir
     */
    public static String getHdfsDir(ResourceType resourceType, String tenantCode) {
        String hdfsDir = "";
        if (resourceType.equals(ResourceType.FILE)) {
            hdfsDir = getHdfsResDir(tenantCode);
        } else if (resourceType.equals(ResourceType.UDF)) {
            hdfsDir = getHdfsUdfDir(tenantCode);
        }
        return hdfsDir;
    }

    /**
     * hdfs resource dir
     *
     * @param tenantCode tenant code
     * @return hdfs resource dir
     */
    public static String getHdfsResDir(String tenantCode) {
        return String.format("%s/resources", getHdfsTenantDir(tenantCode));
    }

    /**
     * hdfs user dir
     *
     * @param tenantCode tenant code
     * @param userId     user id
     * @return hdfs resource dir
     */
    public static String getHdfsUserDir(String tenantCode, int userId) {
        return String.format("%s/home/%d", getHdfsTenantDir(tenantCode), userId);
    }

    /**
     * hdfs udf dir
     *
     * @param tenantCode tenant code
     * @return get udf dir on hdfs
     */
    public static String getHdfsUdfDir(String tenantCode) {
        return String.format("%s/udfs", getHdfsTenantDir(tenantCode));
    }

    /**
     * get hdfs file name
     *
     * @param resourceType resource type
     * @param tenantCode   tenant code
     * @param fileName     file name
     * @return hdfs file name
     */
    public static String getHdfsFileName(ResourceType resourceType, String tenantCode, String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getHdfsDir(resourceType, tenantCode), fileName);
    }

    /**
     * get absolute path and name for resource file on hdfs
     *
     * @param tenantCode tenant code
     * @param fileName   file name
     * @return get absolute path and name for file on hdfs
     */
    public static String getHdfsResourceFileName(String tenantCode, String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getHdfsResDir(tenantCode), fileName);
    }

    /**
     * get absolute path and name for udf file on hdfs
     *
     * @param tenantCode tenant code
     * @param fileName   file name
     * @return get absolute path and name for udf file on hdfs
     */
    public static String getHdfsUdfFileName(String tenantCode, String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.replaceFirst("/", "");
        }
        return String.format("%s/%s", getHdfsUdfDir(tenantCode), fileName);
    }

    /**
     * @param tenantCode tenant code
     * @return file directory of tenants on hdfs
     */
    public static String getHdfsTenantDir(String tenantCode) {
        return String.format("%s/%s", getHdfsDataBasePath(), tenantCode);
    }

    /**
     * getAppAddress
     *
     * @param appAddress app address
     * @param rmHa       resource manager ha
     * @return app address
     */
    public static String getAppAddress(String appAddress, String rmHa) {

        //get active ResourceManager
        String activeRM = YarnHAAdminUtils.getAcitveRMName(rmHa);

        String[] split1 = appAddress.split(Constants.DOUBLE_SLASH);

        if (split1.length != 2) {
            return null;
        }

        String start = split1[0] + Constants.DOUBLE_SLASH;
        String[] split2 = split1[1].split(Constants.SEPARATOR_SEMICOLON);

        if (split2.length != 2) {
            return null;
        }

        String end = Constants.SEPARATOR_SEMICOLON + split2[1];

        return start + activeRM + end;
    }

    @Override
    public void close() throws IOException {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                LOG.error("Close HadoopUtils instance failed", e);
                throw new IOException("Close HadoopUtils instance failed", e);
            }
        }
    }

    /**
     * yarn ha admin utils
     */
    private static final class YarnHAAdminUtils extends RMAdminCLI {

        /**
         * get active resourcemanager
         *
         * @param rmIds
         * @return
         */
        public static String getAcitveRMName(String rmIds) {

            String[] rmIdArr = rmIds.split(Constants.SEPARATOR_COMMA);

            int activeResourceManagerPort =
                    PropertyUtils.getInt(Constants.HADOOP_RESOURCE_MANAGER_HTTPADDRESS_PORT, 8088);

            String yarnUrl = "http://%s:" + activeResourceManagerPort + "/ws/v1/cluster/info";

            String state = null;
            try {
                /**
                 * send http get request to rm1
                 */
                state = getRMState(String.format(yarnUrl, rmIdArr[0]));

                if (Constants.HADOOP_RM_STATE_ACTIVE.equals(state)) {
                    return rmIdArr[0];
                } else if (Constants.HADOOP_RM_STATE_STANDBY.equals(state)) {
                    state = getRMState(String.format(yarnUrl, rmIdArr[1]));
                    if (Constants.HADOOP_RM_STATE_ACTIVE.equals(state)) {
                        return rmIdArr[1];
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                state = getRMState(String.format(yarnUrl, rmIdArr[1]));
                if (Constants.HADOOP_RM_STATE_ACTIVE.equals(state)) {
                    return rmIdArr[0];
                }
            }
            return null;
        }

        /**
         * get ResourceManager state
         *
         * @param url
         * @return
         */
        public static String getRMState(String url) {

            String retStr = HttpUtils.get(url);

            if (StringUtils.isEmpty(retStr)) {
                return null;
            }
            //to json
            ObjectNode jsonObject = JSONUtils.parseObject(retStr);

            //get ResourceManager state
            if (!jsonObject.has("clusterInfo")) {
                return null;
            }
            return jsonObject.get("clusterInfo").path("haState").asText();
        }

    }

}
