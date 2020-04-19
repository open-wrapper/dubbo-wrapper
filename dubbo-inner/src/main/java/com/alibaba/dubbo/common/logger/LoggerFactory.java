/*
 * Copyright 1999-2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.logger;

import com.wrapper.dubbo.common.logback.LogbackLoggerAdapter;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.jcl.JclLoggerAdapter;
import org.apache.dubbo.common.logger.jdk.JdkLoggerAdapter;
import org.apache.dubbo.common.logger.log4j.Log4jLoggerAdapter;
import org.apache.dubbo.common.logger.slf4j.Slf4jLoggerAdapter;

/**
 * 日志输出器工厂
 *
 * @author william.liangf
 */
@Deprecated
public class LoggerFactory {

    private LoggerFactory() {
    }

    private static volatile org.apache.dubbo.common.logger.LoggerAdapter LOGGER_ADAPTER;

    private static final ConcurrentMap<String, org.apache.dubbo.common.logger.support.FailsafeLogger> LOGGERS = new ConcurrentHashMap<String, org.apache.dubbo.common.logger.support.FailsafeLogger>();

    private static final ConcurrentMap<String, Logger> LOGGER_LOGGERS = new ConcurrentHashMap<String, Logger>();

    // 查找常用的日志框架
    static {
        String logger = System.getProperty("dubbo.application.logger");
        if ("slf4j".equals(logger)) {
            setLoggerAdapter(new Slf4jLoggerAdapter());
        } else if ("jcl".equals(logger)) {
            setLoggerAdapter(new JclLoggerAdapter());
        } else if ("log4j".equals(logger)) {
            setLoggerAdapter(new Log4jLoggerAdapter());
        } else if ("jdk".equals(logger)) {
            setLoggerAdapter(new JdkLoggerAdapter());
        } else if ("logback".equals(logger)) {
            setLoggerAdapter(new LogbackLoggerAdapter());
        } else {
            try {
                setLoggerAdapter(new LogbackLoggerAdapter());
            } catch (Throwable e0) {
                try {
                    setLoggerAdapter(new Log4jLoggerAdapter());
                } catch (Throwable e1) {
                    try {
                        setLoggerAdapter(new Slf4jLoggerAdapter());
                    } catch (Throwable e2) {
                        try {
                            setLoggerAdapter(new JclLoggerAdapter());
                        } catch (Throwable e3) {
                            setLoggerAdapter(new JdkLoggerAdapter());
                        }
                    }
                }
            }
        }
    }

    public static void setLoggerAdapter(String loggerAdapter) {
        if (loggerAdapter != null && loggerAdapter.length() > 0) {
            setLoggerAdapter(ExtensionLoader.getExtensionLoader(org.apache.dubbo.common.logger.LoggerAdapter.class).getExtension(loggerAdapter));
        }
    }

    /**
     * 设置日志输出器供给器
     *
     * @param loggerAdapter 日志输出器供给器
     */
    public static void setLoggerAdapter(org.apache.dubbo.common.logger.LoggerAdapter loggerAdapter) {
        if (loggerAdapter != null) {
            org.apache.dubbo.common.logger.Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
            logger.info("using logger: " + loggerAdapter.getClass().getName());
            LoggerFactory.LOGGER_ADAPTER = loggerAdapter;
            for (Map.Entry<String, org.apache.dubbo.common.logger.support.FailsafeLogger> entry : LOGGERS.entrySet()) {
                entry.getValue().setLogger(LOGGER_ADAPTER.getLogger(entry.getKey()));
            }
        }
    }

    /**
     * 获取日志输出器
     *
     * @param key 分类键
     * @return 日志输出器, 后验条件: 不返回null.
     */
    public static Logger getLogger(Class<?> key) {
        org.apache.dubbo.common.logger.support.FailsafeLogger logger = LOGGERS.get(key.getName());
        if (logger == null) {
            LOGGERS.putIfAbsent(key.getName(), new org.apache.dubbo.common.logger.support.FailsafeLogger(LOGGER_ADAPTER.getLogger(key)));
            logger = LOGGERS.get(key.getName());
            LOGGER_LOGGERS.putIfAbsent(key.getName(), new LoggerWrapper(logger));

        }
        return LOGGER_LOGGERS.get(key.getName());

    }

    /**
     * 获取日志输出器
     *
     * @param key 分类键
     * @return 日志输出器, 后验条件: 不返回null.
     */
    public static Logger getLogger(String key) {
        org.apache.dubbo.common.logger.support.FailsafeLogger logger = LOGGERS.get(key);
        if (logger == null) {
            LOGGERS.putIfAbsent(key, new org.apache.dubbo.common.logger.support.FailsafeLogger(LOGGER_ADAPTER.getLogger(key)));
            logger = LOGGERS.get(key);
            LOGGER_LOGGERS.putIfAbsent(key, new LoggerWrapper(logger));
        }
        return LOGGER_LOGGERS.get(key);
    }

    /**
     * 动态设置输出日志级别
     *
     * @param level 日志级别
     */
    public static void setLevel(Level level) {
        LOGGER_ADAPTER.setLevel(org.apache.dubbo.common.logger.Level.valueOf(level.name()));

    }

    /**
     * 获取日志级别
     *
     * @return 日志级别
     */
    public static Level getLevel() {
        return Level.valueOf(LOGGER_ADAPTER.getLevel().name());
    }

    /**
     * 获取日志文件
     *
     * @return 日志文件
     */
    public static File getFile() {
        return LOGGER_ADAPTER.getFile();
    }

}