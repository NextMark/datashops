package com.bigdata.datashops.server.redis;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RedissonLockAop {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonLockAop.class);
    private static final Integer LOCK_WAIT_TIME = 5;
    private static final String KEY_PREFIX = "datashops:lock:%s";

    @Autowired
    private RedissonClient redissonClient;

    public RedissonLockAop() {
    }

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        validRedissonLock(redissonLock);

        String key = String.format(KEY_PREFIX, parseExpression(joinPoint, redissonLock));

        Object obj;
        RLock lock = redissonClient.getLock(key);
        boolean lockResult = lock.tryLock(LOCK_WAIT_TIME, redissonLock.leaseTime(), TimeUnit.SECONDS);
        if (lockResult) {
            LOG.info("acquire the lock: {}", key);
            try {
                obj = joinPoint.proceed();
            } finally {
                unlock(lock);
            }
            LOG.info("releases the lock: {}", key);
        } else {
            throw new RuntimeException(String.format("try lock fail: %s", key));
        }
        return obj;
    }

    private void validRedissonLock(RedissonLock redissonLock) {
        if (StringUtils.isBlank(redissonLock.lockParamExpression())) {
            throw new RuntimeException("no lock param expression.");
        }
    }

    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

    private String parseExpression(ProceedingJoinPoint joinPoint, RedissonLock redissonLock)
            throws NoSuchMethodException {
        String lockParam = redissonLock.lockParamExpression();
        Method targetMethod = getTargetMethod(joinPoint);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), targetMethod, joinPoint.getArgs(),
                new DefaultParameterNameDiscoverer());
        Expression expression = parser.parseExpression(lockParam);
        if (!lockParam.contains("#")) {
            return expression.getExpressionString();
        } else {
            return expression.getValue(context, String.class);
        }
    }

    private void unlock(RLock lock) {
        try {
            lock.unlock();
        } catch (Exception e) {
            LOG.error("unlock exception.", e);
            throw new RuntimeException("unlock exception.");
        }
    }
}
