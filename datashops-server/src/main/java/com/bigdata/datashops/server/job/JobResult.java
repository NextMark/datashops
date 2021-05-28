package com.bigdata.datashops.server.job;

import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
public class JobResult {

    private String instanceId;

    private String data;

}
