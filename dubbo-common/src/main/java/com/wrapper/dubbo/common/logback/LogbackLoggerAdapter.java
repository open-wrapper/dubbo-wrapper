package com.wrapper.dubbo.common.logback;

import java.io.File;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerAdapter;

public class LogbackLoggerAdapter implements LoggerAdapter {

    @Override
    public Logger getLogger(Class<?> key) {
        return new LogbackLogger();
    }

    @Override
    public Logger getLogger(String key) {
        return new LogbackLogger();
    }

    @Override
    public void setLevel(Level level) {
        throw new RuntimeException("Method setLevel not supported.");
    }

    @Override
    public Level getLevel() {
        throw new RuntimeException("Method setLevel not supported.");
    }

    @Override
    public File getFile() {
        throw new RuntimeException("Method setLevel not supported.");
    }

    @Override
    public void setFile(File file) {
        throw new RuntimeException("Method setLevel not supported.");
    }

}
