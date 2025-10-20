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
| game.authority         | web权限模块                          |
| game.basic             | 游戏基础模块                           |
| game.login             | 游戏的登录模块，管理模块，服务器列表模块             |
| game.robot             | 游戏机器人模块，模拟机器人操作                  |
| game.server            | 服务器模块，管理模块                       |
| game.server-script     | 游戏服务器脚本，业务逻辑模块                   |
| logbus                 | 日志处理模块，用于上报日志                    |
| logserver              | 日志服务，用户处理游戏内的日志，包括统计             |

### 完成情况
#### 登录（模块：game.login）
###### 1.账号系统
1. 封号
2. 解封，
3. gm等级
4. 白名单
###### 2.区服信息
1. 新增区服
2. 修改开服时间
3. 服务器冠名
###### 3.礼包码
1. 新增礼包码
2. 修改礼包码状态
3. 查询礼包码信息
###### 4.公告
1. 新增公告
2. 修改公告状态
3. 查询公告信息

#### 游戏（模块：game.server）
1. 角色系统
2. 角色快照
3. 邮件
4. 成就
5. 任务
6. 活动
7. 属性计算
8. buff基础
#### 日志(模块：logserver)
1. 日志记录
2. 日志查询
3. 日志统计分析

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
![image](/png/backend-1.png)
![image](/png/backend-2.png)
![image](/png/backend-3.png)
![image](/png/backend-4.png)
![image](/png/backend-5.png)
