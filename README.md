# Wrapper Dubbo Project

本项目提供公司如何进行dubbo迭代的模板。这是基于一个基于大规模生产实践的做法。

在大规模的生产实践中，我们使用了基于该模板迭代的方式，进行了对dubbo的定制，使得500+的dubbo的应用，平滑的从2.5.3版本升到了内核为2.7.4.1版本

理论上你可以利用该模板，对dubbo做任何定制化的开发，无论是硬编码还是SPI扩展。

## 使用

1. 下载源码,编译执行 
        
        mvn clean install -Dmaven.test.skip=true -Dmaven.javaDoc.skip=true

2. 使用依赖

            <dependency>
                <groupId>com.wrapper.dubbo</groupId>
                <artifactId>dubbo</artifactId>
                <version>2.7.4-SNAPSHOT</version>
             </dependency>

## 优势

1. 便捷的追踪开源代码方式
    - 避免开发者对dubbo本身的修改
2. 复杂的定制可行性
    - 模板突破了框架的内部的扩展机制，不仅限于SPI本身
3. 迭代future吸收
    - 随时随地的吸收bugfix，补丁。不需要和dubbo官方版本进行绑定。
4. 轻易版本升级
    - 模板控制了dubbo内核版本（当前2.7.4.1），提供了一个选项即可完成内核版本的升级。
5. 极小的技术负债
    - 避免人员流动对项目本身造成安全隐患

## 模块介绍

1. dubbo
    - 最终的打包jar
2. dubbo-cluster
    1. 简单的定义一个集群也就是cluster的实现，来指导读者在dubbo框架中自定义cluster
3. dubbo-common
    1. 放置一些公共类
4. dubbo-compitable
    1. dubbo-compitable-spring
        1. 用于构建一层适配转换如果你在容器中获得是org.apache的dubbo的bean
    2. dubbo-compitable-zk
        1. 用于兼容如果业务显示的使用了dubbo框架中的api去操作zk的
5. dubbo-exchanger
    1. dubbo-exchanger-version
        1. 用于更改由Request-Response模型下的的dubbo的version的问题
6. dubbo-filter
    1. dubbo-filter-statistics
        1. 简单的定义一个Filter实现，来指导读者在dubbo框架中自定义Filter。
7. dubbo-inner
    - 核心的魔改代码，提供了一些dubbo框架兼容性不好的类，以及一些被删除的类，但是业务方可能会使用

### dubbo-inner

inner作为最为核心的模块，本质上通过覆盖的策略，去过滤掉来自dubbo的类。

1. 通过魔改来自dubbo原生的代码，植入自己的业务代码(不推荐这种方式，请先考虑SPI扩展实现)。
2. 修复dubbo中不满足的逻辑。
3. 增强dubbo的兼容逻辑，典型用于公司内部从低版本升级到高版本。
4. other待补充

### 关于使用

同开源版本，仅仅更换坐标即可

## 关于dubbo特性

请参考社区

## 关于我

java人士一枚，擅长开源定制，技术负债弱化。欢迎交流

![](https://github.com/open-wrapper/dubbo-wrapper/blob/master/codeL.jpg)


