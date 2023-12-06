# Redis简介以及使用Redis存储Session数据

### 1、Redis简介
Redis 是一种开源（BSD 许可）内存中数据结构存储，用作数据库、缓存、消息代理和流引擎。 Redis 提供数据结构，例如字符串、哈希、列表、集合、带有范围查询的排序集、位图、超级日志、地理空间索引和流。 Redis 具有内置复制、Lua 脚本、LRU 驱逐、事务和不同级别的磁盘持久性，并通过 Redis Sentinel 和 Redis Cluster 自动分区提供高可用性。
您可以对这些类型进行原子操作，例如附加到字符串、增加哈希值、将一个元素插入列表，计算集合的交、并、差，或获取排序集中排名最高的成员。
为了实现最佳性能，Redis 使用内存数据集。根据您的使用案例，Redis可以通过定期将数据集转储到磁盘或将每个命令附加到基于磁盘的日志来持久保存数据。如果您只需要功能丰富的网络内存缓存，您还可以禁用持久性。
Redis 支持异步复制，具有快速非阻塞同步和自动重新连接以及网络分割时部分重新同步的功能。

Redis同样包括:
- 事务
- 发布/订阅
- Lua脚本
- 可限定TTL（生存时间）的键
- LRU（Least Recently Used，即最近最久未使用）回收键
- 自动故障恢复

您可以通过大多数编程语言使用 Redis。
Redis 采用 ANSI C 编写，适用于大多数POSIX系统，如Linux、*BSD 和 Mac OS X，无需外部依赖。Linux和OS X是Redis开发和测试最多的两个操作系统，我们建议使用Linux进行部署。Redis官方没有对Windows版本的支持，不建议在windows下使用Redis，所以官网没有 windows 版本可以下载。但是微软团队维护了开源的windows版本，只有 3.2 版本，可用于普通测试。本篇使用Windows上的Ubuntu虚拟机安装Redis。


### 2、开发环境

当前项目使用以下开发环境：
- 操作系统：Windows 11
- JDK 17
- IDE：VS Code（版本1.83.1），并安装以下插件：
  1. Extension Pack for Java
  1. Spring Boot Extension Pack