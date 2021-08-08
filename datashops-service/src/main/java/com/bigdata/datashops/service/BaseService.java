package com.bigdata.datashops.service;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {

    @Autowired
    public JobInstanceService jobInstanceService;

    @Autowired
    public JobService jobService;

    @Autowired
    public JobDependencyService jobDependencyService;
}
