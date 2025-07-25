# wxdgaming.boot2

## 更新仓库地址

github https://github.com/orgs/wxd-gaming/repositories<br>
gitee &nbsp;&nbsp;&nbsp;https://gitee.com/wxd-gaming<br>
博客首页 https://www.cnblogs.com/wxd-gameing<br>
B站 https://www.bilibili.com/list/316421251

#### 软件架构

| 模块                     | 说明                               |
|------------------------|----------------------------------|
| core                   | 核心模块，线程基础模块                      |
| starter                | 启动容器模块                           |
| starter.batis          | 数据库基础配置                          |
| starter.batis.sql      | 关系型数据库基础模块                       |
| starter.batis.mysql    | mysql关系型数据库                      |
| starter.batis.pgsql    | pgsql关系型数据库                      |
| starter.net            | 网络通信相关的基础模块                      |
| starter.net.httpclient | 基于 apache http client 网络通信模块     |
| starter.net.server     | 网络监听，端口同时支持 http, websocket, tcp |
| starter.schedule       | 定时任务模块                           |
| game.test              | 容器启动测试模块                         |
| game.test-scrtip       | 容器启动测试模块脚本模块实现热更新                |
| boot.yml               | 容器采用yml作为启动配置                    |

### 配置

#### 开启端口服务

```
socket:
  server:
    debug: false
    port: 8080
    enabledHttp: true                                        #是否启用http
    enabledTcp: true                                         #是否启用tcp
    enabledWebSocket: true                                   #是否启用websocket
    webSocketPrefix: "/ws"                                   #websocket前缀
    maxAggregatorLength: 64                                  #聚合器最大长度
    maxFrameBytes: -1                                        #单个消息最大长度
    maxFrameLength: -1                                       #一秒钟接收消息最大数量
    sslProtocolType: TLS                                     #ssll类型
    sslKeyStorePath: "jks/wxdtest-1.8.jks"                   #jks证书路径
    sslPasswordPath: "jks/wxdtest-1.8.jks.pwd"               #jks证书密码路径
    idleTimeout: 0
    writeTimeout: 0
    readTimeout: 0
    recvByteBufM: 12                                          #接收缓冲区大小 单位MB
    writeByteBufM: 12                                         #发送缓冲区大小 单位MB
```

#### 开启数据库服务

```
db:
  pgsql:
    debug: true                                                 #调试模式将会显示sql语句
    driverClassName: "org.postgresql.Driver"
    url: "jdbc:postgresql://127.0.0.1:5432/test1"
    username: "postgres"
    password: "test"
    scanPackage: ""                                           #扫描路径
    connectionTimeoutMs: 2000                                 #链接超时单位毫秒
    idleTimeoutM: 10                                          #空闲超时单位分钟
    minPoolSize: 5                                            #最小连接数
    maxPoolSize: 20                                           #最大连接数
    batchSubmitSize: 500                                      #批量提交数量
    batchThreadSize: 1                                        #批量提交线程数
```

#### 启动代码

<pre><code class="language-lava">
RunApplication run = WxdApplication.run(
            CoreScan.class,           //基础模块
            ScheduledScan.class,      //定时任务模块
            NetScan.class,            //网络模块
            PgsqlScan.class,          //开启pgsql数据库模块
            MysqlScan.class,          //开启mysql数据库模块
            Main.class                //启动类
        );
</code>
</pre>

#### 数据库映射

<pre><code class="language-lava">
package run.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@DbTable
public class EntityTest extends EntityLongUID {

    @DbColumn(key = true)
    @Partition()
    private int day;
    @DbColumn(index = true)
    private boolean online;
    private short sex;
    private int age;
    @DbColumn(length = 128)
    private String name;
    @DbColumn(length = 12800)
    private String remark;
    @DbColumn(length = 32800)
    private String remark2;
    private final List<String> list = new ArrayList<>();

    private byte[] datas;
    @DbColumn(index = true)
    private int overhead = 2;
}
</code>
</pre>
数据库支持分区表，
数据类型支持 json 和 blob 类型

#### 数据库操作

<pre><code class="language-lava">
    @Test
    public void t1() {
        EntityTest entityTest = new EntityTest();
        entityTest.setUid(System.currentTimeMillis());
        entityTest.setDay(Integer.parseInt(MyClock.formatDate("yyyyMMdd")));
        entityTest.setName("测试");
        dataHelper.insert(entityTest);
        entityTest.getList().add("测试1");
        dataHelper.update(entityTest);
        dataHelper.save(entityTest);
    }
</code>
</pre>

#### 开启 http 监听接口

<pre><code class="language-lava">
    @HttpRequest()
    public String error(RunApplication runApplication,
                        @Value(path = "executor") ExecutorConfig executorConfig,
                        @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(path = "b1", defaultValue = "2") String b1) {
        throw new RuntimeException("d");
    }
</code>
</pre>
支持多种方式注入参数，

1. @Value 是通过配置文件获取参数注入
2. @Param 是通过请求的数据获取注入
3. @Body 是完整的参数映射
4. @Qualifier 是容器类实例参数

#### 开启 rpc 监听接口

<pre><code class="language-lava">
    @RpcRequest()
    public String rpcTest1(RunApplication runApplication,
                        @Value(path = "executor") ExecutorConfig executorConfig,
                        @Value(path = "executor1", required = false) ExecutorConfig executorConfig1,
                        @Body(defaultValue = "1") String body,
                        @Param(path = "b1", defaultValue = "2") String b1) {
        throw new RuntimeException("d");
    }
</code>
</pre>
支持多种方式注入参数，

1. @Value 是通过配置文件获取参数注入
2. @Param 是通过请求的数据获取注入
3. @Body 是完整的参数映射
4. @Qualifier 是容器类实例参数

###### rpc的调用方式

<pre><code class="language-lava">
    rpcService.request(socketSession, "rpcIndex", RunResult.ok().fluentPut("a", "b"))
            .whenComplete((jsonObject, throwable) -> {
                if (throwable != null) {
                    log.error("rpcIndex", throwable);
                } else {
                    log.info("rpcIndex response {}", jsonObject);
                }
            });
</code>
</pre>

##### 定时任务

<pre><code class="language-lava">
    @Scheduled("*/30")
    @ExecutorWith(useVirtualThread = true)
    public void timerAsync() {
        log.info("{}", "timerAsync()");
    }
</code></pre>

#### 预览

![image](/png/start.png)
