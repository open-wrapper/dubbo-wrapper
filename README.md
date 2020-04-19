# Wrapper Dubbo Project

本项目提供了第三方升级dubbo的参考，典型的一个将一个历史版本升级到新版本的手段。

比如从2.5.x升级到2.7.x, 为了新定义版本的概念，我们的Dubbo从2.7.4开始运行，对应社区版本2.7.4.1

这是一种简便的手法用于二开开源项目，从而避免开发者对dubbo本身的修改。并且能随时随地的将迭代的future吸收，而不选择立即的版本迭代

## 架构

以下是dubbo的架构

![Architecture](http://dubbo.apache.org/img/architecture.png)

## 功能特性

* 基于透明接口的RPC
* 智能负载均衡
* 自动服务注册和发现
* 高扩展性
* 运行时流量路由
* 可视化服务治理

## 开始


以下代码段来自[Dubbo Samples](https://github.com/apache/dubbo-samples/tree/master/dubbo-samples-api)。 您可以克隆示例项目，并在继续阅读之前进入`dubbo-samples-api`子目录。

```bash
# git clone https://github.com/apache/dubbo-samples.git
# cd dubbo-samples/dubbo-samples-api
```

在`dubbo-samples-api`目录下有一个[README](https://github.com/apache/dubbo-samples/tree/master/dubbo-samples-api/README.md) 文件。 阅读它，并按照说明尝试该示例。

### Maven dependency

```xml
<properties>
    <dubbo.version>2.7.4</dubbo.version>
</properties>
    
<dependencies>
    <dependency>
        <groupId>com.wrapper.dubbo</groupId>
        <artifactId>dubbo</artifactId>
        <version>${dubbo.version}</version>
    </dependency>
</dependencies>
```

### 定义服务接口

```java
package org.apache.dubbo.samples.api;

public interface GreetingService {
    String sayHello(String name);
}
```

*见 [api/GreetingService.java](https://github.com/apache/dubbo-samples/blob/master/dubbo-samples-api/src/main/java/org/apache/dubbo/samples/api/GreetingsService.java) on GitHub.*

### 为提供者实现接口

```java
package org.apache.dubbo.samples.provider;

import org.apache.dubbo.samples.api.GreetingsService;

public class GreetingsServiceImpl implements GreetingsService {
    @Override
    public String sayHi(String name) {
        return "hi, " + name;
    }
}
```

*见 [provider/GreetingServiceImpl.java](https://github.com/apache/dubbo-samples/blob/master/dubbo-samples-api/src/main/java/org/apache/dubbo/samples/provider/GreetingsServiceImpl.java) on GitHub.*

### 开始编写服务提供者

```java
package org.apache.dubbo.samples.provider;


import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.samples.api.GreetingsService;

import java.util.concurrent.CountDownLatch;

public class Application {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    public static void main(String[] args) throws Exception {
        ServiceConfig<GreetingsService> service = new ServiceConfig<>();
        service.setApplication(new ApplicationConfig("first-dubbo-provider"));
        service.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));
        service.setInterface(GreetingsService.class);
        service.setRef(new GreetingsServiceImpl());
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
```

*见 [provider/Application.java](https://github.com/apache/dubbo-samples/blob/master/dubbo-samples-api/src/main/java/org/apache/dubbo/samples/provider/Application.java) on GitHub.*

### 构建服务提供者

```bash
# mvn clean package
# mvn -Djava.net.preferIPv4Stack=true -Dexec.mainClass=org.apache.dubbo.samples.provider.Application exec:java
```

### 消费者的调用编写

```java
package org.apache.dubbo.samples.client;


import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.samples.api.GreetingsService;

public class Application {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    public static void main(String[] args) {
        ReferenceConfig<GreetingsService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("first-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));
        reference.setInterface(GreetingsService.class);
        GreetingsService service = reference.get();
        String message = service.sayHi("dubbo");
        System.out.println(message);
    }
}
```
*See [consumer/Application.java](https://github.com/apache/dubbo-samples/blob/master/dubbo-samples-api/src/main/java/org/apache/dubbo/samples/client/Application.java) on GitHub.*

### 构建并运行消费者

```bash
# mvn clean package
# mvn -Djava.net.preferIPv4Stack=true -Dexec.mainClass=org.apache.dubbo.samples.client.Application exec:java
```

消费者将在屏幕上打印出`hi, dubbo`。

### 其他dubbo特性

请参考社区