### SpringBoot Admin
#### 简介
- SpringBootAdmin是一个开源社区项目，其主要用途就是管理和监控SpringBoot应用程序。常用的监控项目如下：
    - 显示健康状况
    - 显示详细信息，例如
        - JVM和内存指标
        - micrometer.io指标
        - 数据源指标
        - 缓存指标
    - 显示构建信息编号
    - 关注并下载日志文件
    - 查看jvm系统和环境属性
    - 查看Spring Boot配置属性
    - 支持Spring Cloud的postable / env-和/ refresh-endpoint
    - 轻松的日志级管理与JMX-beans交互
    - 查看线程转储
    - 查看http跟踪
    - 查看auditevents
    - 查看http-endpoints
    - 查看计划任务
    - 查看和删除活动会话（使用spring-session）
    - 查看Flyway / Liquibase数据库迁移
    - 下载heapdump
    - 状态变更通知（通过电子邮件，Slack，Hipchat，......）
    - 状态更改的事件日志（非持久性）
- 具体某些功能的使用会详细写出来，项目的官方文档：[SpringBoot Admin Docs](https://codecentric.github.io/spring-boot-admin/current/)
#### 创建Admin服务端
- 主要的流程其实类似于SpringCloud中的服务注册发现，需要被监控的Client应用通过配置的url将服务注册到Server端，Server端就可以通过Client的actuator暴露的api进行服务的监控。如下图
- ![31ddacf2cb15939fb91acf1e56bcedbc.png](en-resource://database/498:1)
- 当然SpringBootAdmin也可以借助Eureka等注册中心(Nacos、Zookeeper还没有试过)进行Client的服务监控
- 服务端很简单，引入spring-boot-admin-starter-server、spring-boot-starter-web。具体版本自己摸索
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- 使用Tomcat容器有的版本会报错，换成jetty -->
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>${spring-boot-admin.version}</version>
</dependency>
```
- 添加propertied配置
```properties
server.port=9998
spring.application.name=monitor
```
- SpringBoot启动类添加EnableAdminServer注解，启动项目，进入localhost:9998即可进入Admin监控主页面，如图
![141dc283ebf241681fd94be8789b41b7.png](en-resource://database/500:1)
- SpringBootAdmin在2.2.0版本之后新增了多种语言的支持，如中文、俄语等等
- 此外，服务端也可以使用SpringSecurity组件进行安全登录认证
#### 客户端服务注册
- 首先引入spring-boot-admin-starter-client依赖
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>${spring-boot-admin.version}</version>
</dependency>
```
- 由于client包已经包括了actuator包，所以也就不需要重复引入
- 添加配置
```yml
spring
  boot:
    admin:
      client:
        url: http://localhost:9998
        #如果服务端开启了登录认证，则需要添加用户名密码
        username: admin
        password: 123456

management:
  endpoints:
    web:
      exposure:
        include: '*'
    health:
      sensitive: false
  endpoint:
    health:
      show-details: ALWAYS
```
- 启动项目就可以进行服务的监控了
- 另外 如果Client服务中添加了安全认证组件，如Shiro、Security等，则需要对Actuator组件中所涉及的api进行放行，否则就会出现监控数据异常的情况。具体的api规则为/actuator/\**、/instances/**
#### 日志监控
- 下面就日志监控进行配置，以下为官方文档原文
> By default the logfile is not accessible via actuator endpoints and therefore not visible in Spring Boot Admin. In order to enable the logfile actuator endpoint you need to configure Spring Boot to write a logfile, either by setting logging.file.path or logging.file.name.
> Spring Boot Admin will detect everything that looks like an URL and render it as hyperlink.
ANSI color-escapes are also supported. You need to set a custom file log pattern as Spring Boot’s default one doesn’t use colors.
>- application.properties
> ```properties
> logging.file.name=/var/log/sample-boot-application.log 
> logging.pattern.file=%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID}){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx 
> ```
>- Destination the logfile is written to. Enables the logfile actuator endpoint.
>- File log pattern using ANSI colors.
- 也就是可以通过logging.file.path和logging.file.name两个配置进行日志监控的配置，其中path属性表示监控的日志文件所在的路径，如果不配置name的话，默认就是目录下的spring.log文件
- ![f2007f78e7c001332eec126dab8d57bb.png](en-resource://database/504:1)
- 上图中的LogFileWebEndpoint类为日志监控所需要使用到的类，类中的logFile属性就是监控的文件信息
- 如果配置的要监控的log文件不存在，页面就会提示404错误，所以就需要使用logback或者其他的日志组件生成日志
- 因为一般情况下的框架都会将日志按照不同的日志等级进行分文件存储，所以如果想要监控全部等级的日志，就需要另外生成一份包含全等级的日志文件
#### 服务监控通知
- 该功能主要