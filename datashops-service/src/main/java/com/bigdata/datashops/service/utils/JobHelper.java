package com.bigdata.datashops.service.utils;

import java.util.Date;

import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.model.enums.SchedulingPeriod;

public class JobHelper {
    public static Date getBizDate(int schedulingPeriod) {
        Date now = new Date();
        Date bizTime = now;
        switch (SchedulingPeriod.of(schedulingPeriod)) {
            case MINUTE:
                bizTime = DateUtils.getStartOfMinute(now);
                break;
            case HOUR:
                bizTime = DateUtils.getStartOfHour(now);
                break;
            case DAY:
                bizTime = DateUtils.getStartOfDay(now);
                break;
            case WEEK:
                bizTime = DateUtils.getMonday(now);
                break;
            case MONTH:
                bizTime = DateUtils.getFirstDayOfMonth(now);
                break;
            default:
                break;
        }
        return bizTime;
    }

    public static void main(String[] args) {
        System.out.println(getBizDate(4));
    }
}
