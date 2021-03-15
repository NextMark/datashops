package com.bigdata.datashops.service.utils;

import org.quartz.CronExpression;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.exception.ValidationException;
import com.bigdata.datashops.model.pojo.quartz.Day;
import com.bigdata.datashops.model.pojo.quartz.Hour;
import com.bigdata.datashops.model.pojo.quartz.Minute;
import com.bigdata.datashops.model.pojo.quartz.Month;
import com.bigdata.datashops.model.pojo.quartz.Week;

public class CronHelper {

    public static String buildCronExpression(DtoCronExpression dtoCronExpression) {
        String expression = "%s %s %s %s %s %s";
        switch (SchedulingPeriod.of(dtoCronExpression.getSchedulingPeriod())) {
            case MINUTE:
                Minute minute = JSONUtils.parseObject(dtoCronExpression.getConfig(), Minute.class);
                assert minute != null;
                expression = String.format(expression, "00", "*/" + minute.getPeriod(),
                        minute.getMinuteBegin() + "-" + minute.getMinuteEnd(), "*", "*", "?");
                break;
            case HOUR:
                Hour hour = JSONUtils.parseObject(dtoCronExpression.getConfig(), Hour.class);
                assert hour != null;
                int type = hour.getType();
                if (type == 2) {
                    expression = String.format(expression, "00", "00", hour.getHours(), "*", "*", "?");
                }
                if (type == 1) {
                    expression = String.format(expression, "00", hour.getHourMinute(),
                            String.format("%s-%s/%s", hour.getHourBegin(), hour.getHourEnd(), hour.getHourPeriod()),
                            "*", "*", "?");
                }
                break;
            case DAY:
                Day day = JSONUtils.parseObject(dtoCronExpression.getConfig(), Day.class);
                assert day != null;
                expression = String.format(expression, "00", day.getMinute(), day.getHour(), "*", "*", "?");
                break;
            case WEEK:
                Week week = JSONUtils.parseObject(dtoCronExpression.getConfig(), Week.class);
                assert week != null;
                expression =
                        String.format(expression, "00", week.getMinute(), week.getHour(), "?", "*", week.getWeeks());
                break;
            case MONTH:
                Month month = JSONUtils.parseObject(dtoCronExpression.getConfig(), Month.class);
                assert month != null;
                expression =
                        String.format(expression, "00", month.getMinute(), month.getHour(), month.getDays(), "*", "?");
                break;
            default:
                break;
        }
        boolean valid = CronExpression.isValidExpression(expression);
        if (!valid) {
            throw new ValidationException("Can not build cron expression, " + dtoCronExpression.toString());
        }
        return expression;
    }
}
