package com.bigdata.datashops.model.pojo.job.data;

import java.util.List;

import com.google.common.collect.Lists;

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

    public String[] buildHive2Mysql() {
        List<String> args = Lists.newArrayList();
        args.add("--connect");
        args.add(mysqlJdbc);
        args.add("--username");
        args.add(mysqlUser);
        args.add("--password");
        args.add(mysqlPass);
        args.add("--table");
        args.add(mysqlTable);
        args.add("--input-null-string");
        args.add("\\N");
        args.add("--input-null-non-string");
        args.add("\\N");
        args.add("--export-dir");
        args.add(exportDir);
        args.add("--input-lines-terminated-by");
        args.add(linesTerminated);
        args.add("--input-fields-terminated-by");
        args.add(fieldsTerminated);
        args.add("--m");
        args.add(mapNum);
        return (String[]) args.toArray();
    }

    public String[] buildMysql2Hive() {
        List<String> args = Lists.newArrayList();
        args.add("--connect");
        args.add(mysqlJdbc);
        args.add("--username");
        args.add(mysqlUser);
        args.add("--password");
        args.add(mysqlPass);
        args.add("--table");
        args.add(mysqlTable);
        args.add("--hive-overwrite");
        args.add("--hive-import");
        args.add("--input-null-string");
        args.add("\\N");
        args.add("--input-null-non-string");
        args.add("\\N");
        args.add("--hive-database");
        args.add(hiveDb);
        args.add("--hive-table");
        args.add(hiveTable);
        args.add("--fields-terminated-by");
        args.add(fieldsTerminated);
        args.add("--m");
        args.add(mapNum);
        args.add("--hive-partition-key");
        args.add(partitionKey);
        args.add("--hive-partition-value");
        args.add(partitionValue);
        return (String[]) args.toArray();
    }
}
