# Wrapper Dubbo Project

本项目提供了外部如何升级dubbo的参考。这是基于一个基于大规模生产实践校验的做法。

比如从2.5.x升级到2.7.x, 为了新定义版本的概念，我们的Dubbo从2.7.4开始运行，对应社区版本2.7.4.1

## 优势

1. 便捷的追踪开源代码，避免开发者对dubbo本身的修改
2. 运行复杂的定制，而不是仅仅受限于框架的内部的扩展机制
3. 随时随地的进行迭代future吸收，而不选择立即的版本迭代
4. 新版本实验，运行使用最为简单方式进行更新内核版本

## 模块介绍

1. dubbo
2. dubbo-cluster
3. dubbo-common
4. dubbo-compitable
    1. dubbo-compitable-spring
    2. dubbo-compitable-zk
5. dubbo-exchanger
    1. dubbo-exchanger-version
6. dubbo-filter
    1. dubbo-filter-statistics
7. dubbo-inner


### dubbo-inner

inner作为最为核心的模块，本质上通过覆盖的策略，去过滤掉来自dubbo的类。

1. 通过魔改来自dubbo原生的代码，植入自己的业务代码(不推荐这种方式，请先考虑SPI扩展实现)。
2. 修复dubbo中不满足的逻辑。
3. 增强dubbo的兼容逻辑，典型用于公司内部从低版本升级到高版本。
4. other待补充

## 关于dubbo特性

请参考社区

## 关于我

java人士一枚，擅长开源定制，技术负债弱化。欢迎交流

