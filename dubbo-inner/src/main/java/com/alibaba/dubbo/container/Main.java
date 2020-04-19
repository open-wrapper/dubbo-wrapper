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
package com.alibaba.dubbo.container;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.ConfigUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

/**
 * Main. (API, Static, ThreadSafe)
 *
 * @author william.liangf
 */
@Deprecated
public class Main {

    public static final String CONTAINER_KEY = "dubbo.container";

    public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final ExtensionLoader<org.apache.dubbo.container.Container> loader = ExtensionLoader.getExtensionLoader(org.apache.dubbo.container.Container.class);

    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.setProperty("dubbo.spring.config","classpath*:META-INF/spring/applicationContext.xml");
        try {
            if (args == null || args.length == 0) {
                String config = ConfigUtils.getProperty(CONTAINER_KEY, loader.getDefaultExtensionName());
                args = Constants.COMMA_SPLIT_PATTERN.split(config);
            }

            final List<org.apache.dubbo.container.Container> containers = new ArrayList<org.apache.dubbo.container.Container>();
            for (int i = 0; i < args.length; i++) {
                containers.add(loader.getExtension(args[i]));
            }
            logger.info("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY, "true"))) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        for (org.apache.dubbo.container.Container container : containers) {
                            try {
                                container.stop();
                                logger.info("Dubbo " + container.getClass().getSimpleName() + " stopped!");
                            } catch (Throwable t) {
                                logger.error(t.getMessage(), t);
                            }
                            synchronized (Main.class) {
                                running = false;
                                Main.class.notify();
                            }
                        }
                    }
                });
            }

            for (org.apache.dubbo.container.Container container : containers) {
                container.start();
                logger.info("Dubbo " + container.getClass().getSimpleName() + " started!");
            }
            System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " Dubbo service started!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            writeExitStatus(1);
            System.exit(1);
        }
        writeExitStatus(0);
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }

    /**
     * 将程序的退出状态写到文件里
     *
     * @param status
     */
    private static void writeExitStatus(int status) {
        //获取当前进程PID
        int pid = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        if (pid < 0) {
            pid = 0;
        }
        if (pid > 65535) {
            String strPid = Integer.toString(pid);
            strPid = strPid.substring(strPid.length() - 4, strPid.length());
            pid = Integer.parseInt(strPid);
        }
        //将程序退出状态写入文件
        String dir = System.getProperty("user.dir");
        File statusFile = new File(dir, "exitStatus." + pid);
        boolean success = true;
        Throwable ex = null;
        if (!statusFile.exists()) {
            try {
                success = statusFile.createNewFile();
            } catch (IOException e) {
                ex = e;
            }
        }
        if (success) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(statusFile));
                writer.write(String.valueOf(status));
                writer.write("\n");
            } catch (IOException e) {
                logger.error("写入文件失败.", e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        logger.error("关闭文件流异常.", e);
                    }
                }
            }
        } else {
            if (ex != null) {
                logger.error("创建文件失败.", ex);
            } else {
                logger.error("创建文件失败.");
            }
        }
    }

}