package com.bigdata.datashops.server.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.pojo.quartz.Week;
import com.bigdata.datashops.service.utils.CronHelper;

@SpringBootTest(classes = CronHelper.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CronHelperTest {

    @Test
    public void test() {
        Week week = Week.builder().weeks("1,3").hour("05").minute("02").build();
        DtoCronExpression dtoCronExpression =
                DtoCronExpression.builder().schedulingPeriod(1).config(JSONUtils.toJsonString(week)).build();
        System.out.println(CronHelper.buildCronExpression(dtoCronExpression));

    }
}
