package com.bigdata.datashops.common;

public class Constants {
    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * date format of yyyy-MM-dd
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * date format of yyyyMMddHHmmss
     */
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * common properties path
     */
    public static final String COMMON_PROPERTIES_PATH = "/datashops.properties";
    public static final String DATA_BASEDIR_PATH = "data.basedir.path";
    public static final String PID = "pid";
    public static final int EXIT_SUCCESS_CODE = 0;
    public static final int EXIT_FAIL_CODE = -1;

    /**
     * driver
     */
    public static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String ORG_APACHE_HIVE_JDBC_HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    public static final String CLICK_HOUSE_JDBC = "ru.yandex.clickhouse.ClickHouseDriver";

    /**
     * jdbc url
     */
    public static final String JDBC_MYSQL = "jdbc:mysql://";
    public static final String JDBC_HIVE_2 = "jdbc:hive2://";

    /**
     * network
     */
    public final static String ANY_HOST_VALUE = "0.0.0.0";
    public final static String LOCALHOST_VALUE = "127.0.0.1";

    /**
     * zk
     */
    public final static String ZK_ROOT = "/datashops";
    public final static String ZK_MASTER_NODE = "/master";
    public final static String ZK_WORKER_NODE = "/worker";
    public final static String ZK_WORKER_META = "/meta";
    public final static String ZK_REGISTRY = "/registry";
    public final static String ZK_JOB_QUEUE = "/queue";
    public final static String ZK_FINDER_LOCK = "/lock/finder";
    public final static String ZK_JOB_QUEUE_PREFIX = "qn-";

    /**
     * job
     */
    public final static String JOB_DEFAULT_OPERATOR = "machine";

    /**
     * separator
     */
    public final static String SEPARATOR_COMMA = ",";
    public final static String SEPARATOR_HYPHEN = "-";
    public final static String SEPARATOR_UNDERLINE = "_";
    public final static String SEPARATOR_LINE = "\\|";
    public final static String SEPARATOR_SEMICOLON = ";";
    public final static String SEPARATOR_USER_TOKEN_SALT = "__datashops__";
    public static final String DOUBLE_SLASH = "//";

    /**
     * rpc
     */
    public final static Integer RPC_JOB_SUCCESS = 200;
    public final static Integer RPC_JOB_FAIL = 201;

    /**
     * jwt
     */
    public final static String JWT_HEADER = "Authorization";
    public final static int JWT_EXPIRATION_TIME = 86400;
    public final static String JWT_TOKEN_PREFIX = "Bearer";
    public final static String JWT_AUTHORITIES_KEY = "auth";
    public final static String JWT_PRIVATE_KEY = "rsa/private-key.pem";
    public final static String JWT_PUBLIC_LEY = "rsa/public-key.pem";

    /**
     * fs.defaultFS
     */
    public static final String FS_DEFAULTFS = "fs.defaultFS";

    /**
     * yarn.resourcemanager.ha.rm.ids
     */
    public static final String YARN_RESOURCEMANAGER_HA_RM_IDS = "yarn.resourcemanager.ha.rm.ids";
    public static final String YARN_RESOURCEMANAGER_HA_XX = "xx";

    public static final String RESOURCE_UPLOAD_PATH = "resource.upload.path";
    public static final String KERBEROS_EXPIRE_TIME = "kerberos.expire.time";
    public static final String RESOURCE_STORAGE_TYPE = "resource.storage.type";

    /**
     * yarn.application.status.address
     */
    public static final String YARN_APPLICATION_STATUS_ADDRESS = "yarn.application.status.address";

    /**
     * yarn.job.history.status.address
     */
    public static final String YARN_JOB_HISTORY_STATUS_ADDRESS = "yarn.job.history.status.address";

    public static final String HDFS_ROOT_USER = "hdfs.root.user";
    public static final String KERBEROS = "kerberos";

    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";

    public static final String JAVA_SECURITY_KRB5_CONF_PATH = "java.security.krb5.conf.path";

    public static final String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";

    public static final String HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE =
            "hadoop.security.authentication.startup.state";

    public static final String AWS_S3_V4 = "com.amazonaws.services.s3.enableV4";

    public static final String LOGIN_USER_KEY_TAB_USERNAME = "login.user.keytab.username";

    public static final int DEFAULT_WORKER_ID = -1;

    public static final String LOGIN_USER_KEY_TAB_PATH = "login.user.keytab.path";

    public static final String TASK_LOG_INFO_FORMAT = "TaskLogInfo-%s";

    public static final String HIVE_CONF = "hiveconf:";
    public static final String HADOOP_RM_STATE_ACTIVE = "ACTIVE";

    public static final String HADOOP_RM_STATE_STANDBY = "STANDBY";

    public static final String HADOOP_RESOURCE_MANAGER_HTTPADDRESS_PORT = "resource.manager.httpaddress.port";

    public static final int HTTP_CONNECT_TIMEOUT = 60 * 1000;

    /**
     * http connect request time out
     */
    public static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;

    /**
     * httpclient soceket time out
     */
    public static final int SOCKET_TIMEOUT = 60 * 1000;

    /**
     * http header
     */
    public static final String HTTP_HEADER_UNKNOWN = "unKnown";

    /**
     * http X-Forwarded-For
     */
    public static final String HTTP_X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * http X-Real-IP
     */
    public static final String HTTP_X_REAL_IP = "X-Real-IP";
    public static final String UTF_8 = "UTF-8";

    public static final String ACCEPTED = "ACCEPTED";

    /**
     * SUCCEEDED
     */
    public static final String SUCCEEDED = "SUCCEEDED";
    /**
     * NEW
     */
    public static final String NEW = "NEW";
    /**
     * NEW_SAVING
     */
    public static final String NEW_SAVING = "NEW_SAVING";
    /**
     * SUBMITTED
     */
    public static final String SUBMITTED = "SUBMITTED";
    /**
     * FAILED
     */
    public static final String FAILED = "FAILED";
    /**
     * KILLED
     */
    public static final String KILLED = "KILLED";
    /**
     * RUNNING
     */
    public static final String RUNNING = "RUNNING";

    /**
     * common.properties
     */
    public static final String FLINK_BASE_PATH = "flink.base.path";

    public static final String FLINK_USER_JARS_PATH = "flink.user.jars.path";

    public static final String MASTER_GRPC_SERVER_PORT = "master.grpc.server.port";

    public static final String WORKER_GRPC_SERVER_PORT = "worker.grpc.server.port";

    public static final String WORKER_JOB_EXEC_THREADS = "worker.job.exec.threads";

    public static final String MASTER_HEARTBEAT_INTERVAL = "master.heartbeat.interval";

    public static final String WORKER_HEARTBEAT_INTERVAL = "worker.heartbeat.interval";

    public static final String MASTER_RPC_PROCESS_THREADS = "master.rpc.process.threads";

    public static final String MASTER_FINDER_INTERVAL = "master.finder.interval";

    /**
     * aliyun
     */
    public static final String ALIYUN_OSS_REGION = "aliyun.oss.region";

    public static final String ALIYUN_OSS_ENDPOINT = "aliyun.oss.endpoint";

    public static final String ALIYUN_OSS_KEY_ID = "aliyun.oss.key.id";

    public static final String ALIYUN_OSS_KEY_SECRET = "aliyun.oss.key.secret";

    public static final String ALIYUN_OSS_BUCKET_NAME = "aliyun.oss.bucket.name";

}
