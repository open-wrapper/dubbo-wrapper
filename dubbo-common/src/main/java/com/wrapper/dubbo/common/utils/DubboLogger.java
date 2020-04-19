package com.wrapper.dubbo.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboLogger {

    private static final String NAME = "com.wrapper.dubbo.common.utils.DubboLogger";
    private static Logger LOGGER;

    static {
        initLogger();
    }

    private static void initLogger() {
        String logRoot = System.getProperty("dubbo.log.root");
        if (logRoot == null) {
            String userHome = System.getProperty("user.home");
            System.setProperty("dubbo.log.root", userHome + "/logs/dubbo");
        }
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        ILoggerFactory realLoggerFactory;
        if (loggerFactory.getClass().getName().equals("com.wrapper.common.slf4j.VdianILoggerFactory")) {
            try {
                Method getRealLoggerFactory=loggerFactory.getClass().getMethod("getRealLoggerFactory");
                realLoggerFactory = (ILoggerFactory)getRealLoggerFactory.invoke(loggerFactory);
            } catch (Exception e) {
                realLoggerFactory = loggerFactory;
            }
        } else {
            realLoggerFactory = loggerFactory;
        }
        try {
            Class<? extends ILoggerFactory> classType = realLoggerFactory.getClass();
            if (classType.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {
                Class<?> domConfigurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
                Object comConfiguratorObj = domConfigurator.newInstance();
                Method configure = comConfiguratorObj.getClass().getMethod("configure", URL.class);
                URL url = DubboLogger.class.getClassLoader().getResource("dubbo-common/log4j.xml");
                configure.invoke(comConfiguratorObj, url);

                LOGGER = loggerFactory.getLogger(NAME);
            } else if (classType.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {//log4j2
                Class<?> loggerContextClass = Class.forName("org.apache.logging.log4j.core.LoggerContext");
                URI uri = DubboLogger.class.getClassLoader().getResource("dubbo-common/log4j2.xml").toURI();
                Constructor<?> loggerContextConstructor = loggerContextClass.getConstructor(String.class, Object.class, URI.class);
                Object loggerContext = loggerContextConstructor.newInstance("dubboLogger", null, uri);

                Method setConfigLocationMethod = loggerContextClass.getMethod("setConfigLocation", URI.class);
                setConfigLocationMethod.invoke(loggerContext, uri);

                Method getLoggerMethod = loggerContextClass.getMethod("getLogger", String.class);
                Object dubboLogger = getLoggerMethod.invoke(loggerContext, NAME);

                Class<?> log4jLoggerClass = Class.forName("org.apache.logging.slf4j.Log4jLogger");
                Class<?> extendedLoggerClass = Class.forName("org.apache.logging.log4j.spi.ExtendedLogger");
                Constructor<?> log4jLoggerConstructor = log4jLoggerClass.getConstructor(extendedLoggerClass, String.class);

                LOGGER = (Logger) log4jLoggerConstructor.newInstance(dubboLogger, NAME);
            } else if (classType.getName().equals("ch.qos.logback.classic.LoggerContext")) {
                Class<?> context = Class.forName("ch.qos.logback.core.Context");
                Class<?> joranConfigurator = Class.forName("ch.qos.logback.classic.joran.JoranConfigurator");
                Object joranConfiguratoroObj = joranConfigurator.newInstance();
                Method setContext = joranConfiguratoroObj.getClass().getMethod("setContext", context);
                setContext.invoke(joranConfiguratoroObj, realLoggerFactory);
                URL url = DubboLogger.class.getClassLoader().getResource("dubbo-common/logback.xml");
                Method doConfigure = joranConfiguratoroObj.getClass().getMethod("doConfigure", URL.class);
                doConfigure.invoke(joranConfiguratoroObj, url);

                LOGGER = loggerFactory.getLogger(NAME);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        /**
         * 依赖系统
         */
        if (LOGGER == null) {
            LOGGER = LoggerFactory.getLogger(NAME);
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
