package com.bigdata.datashops.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class JobUtils {
    public static String genJobInstanceId() {
        return DateUtils.getCurrentTime("yyMMddHHmmss") + RandomStringUtils.randomNumeric(6);
    }

    public static String genMaskId(String prefix) {
        return prefix + DateUtils.getCurrentTime("yyMMddHHmmss") + RandomStringUtils.randomNumeric(3);
    }
}
