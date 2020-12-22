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
    public static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String ORG_APACHE_HIVE_JDBC_HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    /**
     * jdbc url
     */
    public static final String JDBC_MYSQL = "jdbc:mysql://";
    public static final String JDBC_HIVE_2 = "jdbc:hive2://";

    /**
     * network
     */
    public final static String ANYHOST_VALUE = "0.0.0.0";
    public final static String LOCALHOST_VALUE = "127.0.0.1";

    /**
     * zk
     */
    public final static String ZK_ROOT = "datashops/";
    public final static String ZK_MASTER_NODE = "master/";
    public final static String ZK_WORKER_NODE = "worker/";
    public final static String ZK_JOB_QUEUE = "queue/";
    public final static String ZK_JOB_QUEUE_PREFIX = "qn-";

    /**
     * job
     */
    public final static String JOB_DEFAULT_OPERATOR = "machine";

    /**
     * separator
     */
    public final static String SEPARATOR_COMMA = ",";
    public final static String SEPARATOR_LINE = "\\|";


}
