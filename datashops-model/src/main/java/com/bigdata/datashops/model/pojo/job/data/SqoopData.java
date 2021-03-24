package com.bigdata.datashops.model.pojo.job.data;

import lombok.Data;

@Data
public class SqoopData {
    // 1 hive2mysql, 2 mysql2hive
    private Integer type;

    private String mysqlJdbc;

    private String mysqlAddress;

    private String mysqlUser;

    private String mysqlPass;

    private String mysqlDb;

    private String mysqlTable;

    private String fieldsTerminated;

    private String linesTerminated;

    private String mapNum;

    private String exportDir;

    // mysql to hive
    private String hiveDb;

    private String hiveTable;

    private String partitionKey;

    private String partitionValue;
}
