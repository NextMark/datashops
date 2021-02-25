package com.bigdata.datashops.server.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    String lockParamExpression();

    /**
     * 锁自动释放时间(秒)
     */
    int leaseTime() default 10;

}
