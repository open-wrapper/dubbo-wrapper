package com.alibaba.dubbo.remoting.zookeeper.zkclient;

import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.common.URL;

public class ZkclientZookeeperTransporter {

    public ZookeeperClient connect(URL url) {
        return new ZkclientZookeeperClient(new com.alibaba.dubbo.common.URL(url));
    }

    public ZookeeperClient connect(com.alibaba.dubbo.common.URL url) {
        return new ZkclientZookeeperClient(url);
    }

}
