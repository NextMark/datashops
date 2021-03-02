package com.bigdata.datashops.server.utils;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class LogUtils {

    public static Logger getLogger(String filePattern, String loggerName, String logDir) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = LoggerFactory.getLogger(loggerName);
        ch.qos.logback.classic.Logger newLogger = (ch.qos.logback.classic.Logger) logger;
        newLogger.detachAndStopAllAppenders();

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        //policy
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(loggerContext);
        policy.setFileNamePattern(logDir + filePattern);
        policy.setParent(appender);
        policy.setMaxHistory(48);
        policy.start();

        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%file:%line] %msg%n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        //start appender
        appender.setRollingPolicy(policy);
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.setPrudent(false);
        appender.start();

        newLogger.addAppender(appender);
        newLogger.setLevel(Level.INFO);
        newLogger.setAdditive(false);
        return newLogger;
    }
}
