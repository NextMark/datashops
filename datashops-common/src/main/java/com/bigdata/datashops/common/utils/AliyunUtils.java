package com.bigdata.datashops.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.bigdata.datashops.common.Constants;

public class AliyunUtils {
    private static Logger LOG = LoggerFactory.getLogger(AliyunUtils.class);

    private static OSS oss = new OSSClientBuilder().build(PropertyUtils.getString(Constants.ALIYUN_OSS_ENDPOINT),
            PropertyUtils.getString(Constants.ALIYUN_OSS_KEY_ID),
            PropertyUtils.getString(Constants.ALIYUN_OSS_KEY_SECRET));

    private static ObjectMetadata defaultMetaData = new ObjectMetadata();

    static {
        defaultMetaData.setCacheControl("max-age=5184000");
    }

    public static String getBucket() {
        return PropertyUtils.getString(Constants.ALIYUN_OSS_BUCKET_NAME);
    }

    public static String getEndPoint() {
        return PropertyUtils.getString(Constants.ALIYUN_OSS_ENDPOINT);
    }

    public static String putByteFile(InputStream is, String fileKey) {
        try {
            byte[] fileData = IOUtils.toByteArray(is);
            oss.putObject(
                    new PutObjectRequest(getBucket(), fileKey, new ByteArrayInputStream(fileData), defaultMetaData));
            return getLocationPrefix() + fileKey;
        } catch (Exception e) {
            LOG.error("Store image error, url={}", fileKey, e);
            return null;
        }
    }

    public static String getLocationPrefix() {
        return String.format("https://%s.%s/", getBucket(), getEndPoint());
    }

    public static void download(String objectName, String localPath) {
        try {
            oss.getObject(new GetObjectRequest(getBucket(), objectName), new File(localPath));
        } catch (Exception e) {
            LOG.error("Download file error, url={}", objectName, e);
        }
    }
}
