package com.bigdata.datashops.server.job;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobContext implements Serializable {
    private String jobName;

    private String jobType;

    private String runPath;

    private String jobParams;

    private List<Map<String, String>> resources;

}
