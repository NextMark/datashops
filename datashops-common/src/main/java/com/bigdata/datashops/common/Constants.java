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
}
