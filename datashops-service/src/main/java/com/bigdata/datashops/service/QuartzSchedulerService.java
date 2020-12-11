package com.bigdata.datashops.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class QuartzSchedulerService {
    public Map<String, Object> buildJobDataMap() {
        return Maps.newLinkedHashMap();
    }
}
