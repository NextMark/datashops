package com.bigdata.datashops.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import tk.mybatis.spring.annotation.MapperScan;

@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
//@MapperScan("com.bigdata.datashops.dao.mapper")
@EntityScan("com.bigdata.datashops.model")
@EnableJpaRepositories(basePackages={"com.bigdata.datashops.dao"})
@SpringBootApplication(scanBasePackages = {"com.bigdata.datashops"}, exclude = {SecurityAutoConfiguration.class})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
