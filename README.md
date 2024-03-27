# Redis简介以及使用Redis存储Session数据

## Redis简介
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

## 在Ubuntu虚拟机上安装Redis
在 Ubuntu 上打开命令行，并输入以下命令来安装Redis：
```
$ curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

$ echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

$ sudo apt-get update

$ sudo apt-get install redis
```

## 配置Redis
安装完成后，修改Redis配置文件。 为此，请使用您选择的文本编辑器打开文件：
```
$ sudo vi /etc/redis/redis.conf
```
在打开的文件中修改下列信息：
- 绑定IP

绑定当前机器IP‘192.168.137.40’到Redis（注意当前机器IP可能为其它，可以使用命令ip a或ifconfig查看当前机器IP）：
```
bind 192.168.137.40 127.0.0.1
```
- 禁用保护模式

在保护模式下，只能连接当前机器上的Redis。
```
protected-mode no
```
- 指定受监督指令 

默认情况下，受监督指令设置为 no。 但是，要将 Redis 作为服务进行管理，请将受监督指令设置为 systemd（Ubuntu 的 init 系统）。
```
supervised systemd
```
保存redis.conf然后退出。

接下来使用如下命令来设置Redis开机自启动：
```
$ sudo systemctl enable redis-server.service
```
执行如下命令启动Redis服务：
```
$ sudo systemctl start redis-server.service 
```
 
使用如下命令打开防火墙端口6379，6379是Redis默认使用端口。
```
$ sudo ufw allow 6379
```

## 构建与运行前提条件

在当前机器上安装下列软件:
1. Java JDK 17
2. Apache Maven 4.0.0-alpha-8或更高版本

## 开发环境

当前项目使用以下开发环境：
- 操作系统：Windows 11
- JDK 17
- IDE：VS Code（版本1.83.1），并安装以下插件：
  1. Extension Pack for Java
  1. Spring Boot Extension Pack

## 构建Spring Boot应用
在代码根目录下，运行以下命令来构建应用: 
```
mvn clean package
```
它将在应用下得/target目录产生对应的jar文件。


## 如何运行

在代码根目录下，使用以下命令来运行应用：
```
cd target
java -jar session-redis-0.0.1-SNAPSHOT.jar
```

## 如何测试
首先启动Redis服务，以及应用运行成功后，可以执行应用中的Junit Test来测试。