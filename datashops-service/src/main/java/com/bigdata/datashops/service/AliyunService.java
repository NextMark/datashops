package com.bigdata.datashops.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.bigdata.datashops.service.config.AliyunConfig;

@Service
public class AliyunService {
    private static Logger logger = LoggerFactory.getLogger(AliyunService.class);

    @Autowired
    OSS ossClient;

    @Autowired
    AliyunConfig aliyunConfig;

    private static ObjectMetadata defaultMetaData = new ObjectMetadata();

    static {
        defaultMetaData.setCacheControl("max-age=5184000");
    }

    public String putByteFile(InputStream is, String fileKey) {
        try {
            byte[] fileData = IOUtils.toByteArray(is);
            ossClient.putObject(
                    new PutObjectRequest(aliyunConfig.getBucket(), fileKey, new ByteArrayInputStream(fileData),
                            defaultMetaData));
            return getLocationPrefix() + fileKey;
        } catch (Exception e) {
            logger.error("Store image error, url={}", fileKey, e);
            return null;
        }
    }

    private String getLocationPrefix() {
        return String.format("https://%s.%s/", aliyunConfig.getBucket(), aliyunConfig.getEndPoint());
    }

    public void download(String objectName, String localPath) {
        try {
            ossClient.getObject(new GetObjectRequest(aliyunConfig.getBucket(), objectName), new File(localPath));
        } catch (Exception e) {
            logger.error("Download file error, url={}", objectName, e);
        }
    }

}
