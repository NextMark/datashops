package com.bigdata.datashops.server.utils;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
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
    public void testGetDependencyBizTime() {
        Date now = new Date();
        String cron = "00 29 19 * * ?";
        int period = SchedulingPeriod.DAY.getCode();
        int offset = -1;
        for (int i = offset; i <= 0; i++) {
            List<Date> date = CronHelper.getDependencyBizTime(now, period, i, cron);
            date.forEach(x -> System.out.println(DateUtils.format(x, Constants.YYYY_MM_DD_HH_MM_SS)));
        }
    }
}
