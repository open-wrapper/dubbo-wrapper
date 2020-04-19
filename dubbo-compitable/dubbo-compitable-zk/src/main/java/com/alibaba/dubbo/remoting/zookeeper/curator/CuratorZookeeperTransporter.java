package com.alibaba.dubbo.remoting.zookeeper.curator;

import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.common.URL;

public class CuratorZookeeperTransporter {

    public ZookeeperClient connect(URL url) {
        return new CuratorZookeeperClient(new com.alibaba.dubbo.common.URL(url));
    }

    public ZookeeperClient connect(com.alibaba.dubbo.common.URL url) {
        return new CuratorZookeeperClient(url);
    }

}
