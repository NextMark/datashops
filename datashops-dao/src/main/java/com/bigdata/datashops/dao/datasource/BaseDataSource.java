package com.bigdata.datashops.dao.datasource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.model.enums.DbType;

import lombok.Data;

@Data
public abstract class BaseDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDataSource.class);

    protected String user;

    protected String password;

    private String address;

    private String database;

    private String params;

    private String value;

    protected String fillParams(String params) {
        return params;
    }

    public abstract DbType dbType();

    public String getJdbcUrl() {
        StringBuilder jdbcUrl = new StringBuilder(getAddress());
        appendDatabase(jdbcUrl);
        appendOther(jdbcUrl);
        return jdbcUrl.toString();
    }

    protected void appendDatabase(StringBuilder jdbcUrl) {
        if (getAddress().lastIndexOf('/') != (jdbcUrl.length() - 1)) {
            jdbcUrl.append("/");
        }
        jdbcUrl.append(getDatabase());
    }

    private void appendOther(StringBuilder jdbcUrl) {
        if (StringUtils.isNotEmpty(params)) {
            String separator = "";
            switch (dbType()) {
                case CLICK_HOUSE:
                case MYSQL:
                    separator = "?";
                    break;
                case HIVE:
                default:
                    LOG.error("Db type {} mismatch!", dbType());
            }
            jdbcUrl.append(separator).append(params);
        }
    }

}
