package com.bigdata.datashops.server.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.quartz.Week;
import com.bigdata.datashops.service.utils.CronHelper;

@EnableAutoConfiguration
@SpringBootTest(classes = CronHelper.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CronHelperTest {

    @Test
    public void test() {
        Week week = Week.builder().weeks("1,3").hour("05").minute("02").build();
        DtoCronExpression dtoCronExpression =
                DtoCronExpression.builder().schedulingPeriod(1).config(JSONUtils.toJsonString(week)).build();
        System.out.println(CronHelper.buildCronExpression(dtoCronExpression));
    }

    @Test
    public void testGetDependencyBizTime() throws ParseException {
        Date now = new Date();
        String cron = "00 29 19 * * ?";
        int period = SchedulingPeriod.DAY.getCode();
        int offset = -1;
        for (int i = offset; i <= 0; i++) {
            List<Date> date = CronHelper.getDependencyBizTime(now, period, i, cron);
            date.forEach(x -> System.out.println(DateUtils.format(x, Constants.YYYY_MM_DD_HH_MM_SS)));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date myDate = dateFormat.parse("2021-08-13 19:29:00");
        for (int i = offset; i <= 0; i++) {
            List<Date> date = CronHelper.getDependencyBizTime(myDate, period, i, cron);
            for (Date d : date) {
                System.out.println(i + " -> " + DateUtils.format(d, Constants.YYYY_MM_DD_HH_MM_SS));
            }
        }
    }

    @Test
    public void testGetNextExecutionTime() {
        String cron = "00 00 08 ? * 2,5";
        System.out.println(CronHelper.getNextExecutionTime(cron));

    }

    @Test
    public void testGetDownstreamBizTime() throws ParseException {
        String sourceCron = "00 35 23 * * ?";

        String targetCron = "00 00 08 ? * 2,5";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse("2021-08-13 23:35:00");
        System.out.println(
                CronHelper.getDownstreamBizTime(date, -5, SchedulingPeriod.DAY.getCode(), sourceCron, targetCron));

        sourceCron = "00 29 19 * * ?";
        targetCron = "00 */5 14-23 * * ?";
        date = format.parse("2021-08-15 19:29:00");
        List<Date> dates =
                CronHelper.getDownstreamBizTime(date, -1, SchedulingPeriod.DAY.getCode(), sourceCron, targetCron);
        dates.forEach(x -> System.out.println(DateUtils.format(x, Constants.YYYY_MM_DD_HH_MM_SS)));
    }

    @Test
    public void testGetOffsetTriggerTime() throws ParseException {
        String startTime = "2021040111";
        String endTime = "2021040112";
        Date start = DateUtils.parse(startTime, "yyyyMMddHH");
        Date end = DateUtils.parse(endTime, "yyyyMMddHH");
        CronExpression expression = new CronExpression("00 */10 04-23 * * ?");
        for (; start.getTime() <= end.getTime(); ) {
            start = expression.getNextValidTimeAfter(start);
            //System.out.println(start);
            if (start.getTime() >= end.getTime()) {
                break;
            }
        }
        try {
            Date date = DateUtils.stringToDate("2021-04-09 10:15:00");
            System.out.println(CronHelper.getUpstreamBizTime("00 */5 13-14 * * ?", date, 0));
            System.out.println(CronHelper.getUpstreamBizTime("00 */5 10-14 * * ?", date, 0));

            System.out.println(CronHelper.getUpstreamBizTime("00 */5 13-14 * * ?", date, -2));

            System.out.println(CronHelper.getUpstreamBizTime("00 */5 13-14 * * ?", date, -14));
            System.out.println(CronHelper.getUpstreamBizTime("00 10 23 * * ?", date, -2));
            System.out.println(CronHelper.getUpstreamBizTime("00 */10 04-23 * * ?", date, 0));
            System.out.println(CronHelper.getUpstreamBizTime("00 */10 04-23 * * ?", date, 3));

        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported cron or date", e);
        }
    }
}
