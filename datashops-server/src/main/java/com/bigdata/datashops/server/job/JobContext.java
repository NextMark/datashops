package com.bigdata.datashops.server.job;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bigdata.datashops.model.pojo.job.JobInstance;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class JobContext implements Serializable {
    private String jobName;

    private int jobType;

    private String runPath;

    private String jobParams;

    private JobInstance jobInstance;

    private List<Map<String, String>> resources;

}
