package com.bigdata.datashops.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.PropertyUtils;

@Configuration
public class AliyunConfig {
    public String getBucket() {
        return PropertyUtils.getString(Constants.ALIYUN_OSS_BUCKET_NAME);
    }

    public String getEndPoint() {
        return PropertyUtils.getString(Constants.ALIYUN_OSS_ENDPOINT);
    }

    @Bean
    public OSS getOSSClient() {
        return new OSSClientBuilder().build(PropertyUtils.getString(Constants.ALIYUN_OSS_ENDPOINT),
                PropertyUtils.getString(Constants.ALIYUN_OSS_KEY_ID),
                PropertyUtils.getString(Constants.ALIYUN_OSS_KEY_SECRET));
    }

}
