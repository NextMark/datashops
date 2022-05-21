package com.bigdata.datashops.service.hadoop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.HadoopLogUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.enums.RunState;
import com.google.common.collect.Maps;

@Service
public class HadoopService {
    @Autowired
    protected RestTemplate restTemplate;

    public void readLog(String appId) throws IOException {
//        Map<String, String> map = HadoopLogUtils.getContaines(appId, "hive");
//        map.forEach((containeId, nodeId) -> {
//            OutputStream in = null;
//            try {
//                File file = new File("/Users/qinshiwei/1.txt");
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                in = new FileOutputStream(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            PrintStream printStream = new PrintStream(in);
//            List<String> logType = new ArrayList<>(1);
//            logType.add("stdout");
//            try {
//                HadoopLogUtils.dumpAContainersLogs(appId, containeId, nodeId, "hive", printStream, logType);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public void cancelApplication(String appId) {
        String url = String.format(PropertyUtils.getString(Constants.YARN_APPLICATION_CANCEL_ADDRESS), appId);
        Map<String, String> map = Maps.newHashMap();
        map.put("state", "KILLED");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(map, headers);
        restTemplate.put(url, entity);
    }

    public RunState getApplicationStatus(String appId) throws Exception {
        try {
            String url = String.format(PropertyUtils.getString(Constants.YARN_APPLICATION_CANCEL_ADDRESS), appId);
            String resp = restTemplate.getForObject(url, String.class);
            Map<String, String> result = JSONUtils.toMap(resp);
            String state = result.get("state");
            switch (state) {
                case Constants.SUCCEEDED:
                case Constants.FINISHED:
                    return RunState.SUCCESS;
                case Constants.NEW:
                case Constants.NEW_SAVING:
                case Constants.FAILED:
                    return RunState.FAIL;
                case Constants.KILLED:
                    return RunState.KILL;
                case Constants.RUNNING:
                default:
                    return RunState.RUNNING;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
