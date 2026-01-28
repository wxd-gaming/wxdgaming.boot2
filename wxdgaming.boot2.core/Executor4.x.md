# 线程池4.x版本

## 初始化
线程池初始化代码如下：

```angular2html
// 直接 new 初始化
AbstractExecutorService executorServicePlatform = new ExecutorServicePlatform("platform", 4, 1000, QueuePolicyConst.AbortPolicy);

// springboot 通过配置文件初始化
wxdgaming.boot2.core.executor.ExecutorProperties.init
ExecutorFactory.init("executor.properties");

```

## 线程上下文参数传递
线程池支持传递线程上下文参数，具体实现如下：
```code
// 通过获取当前线程上下文方式，然后put参数
ExecutorContext.context().getData().put("local-data", i);

// 如果不担心污染数据，在获取当前线程池的数据的时候可以选择调用一次clear()

// 像线程池提交任务的时候会自动存储，data的内容会传递给线程，然后通过getData()方法获取
executorServicePlatform.execute(() -> {
    log.debug("ddd: {}", ExecutorContext.context());
});

```

## 如果你的当前任务执行耗时很大，需要排除怎么办呢》？
### 1. 线程池配置，通过springboot开启aop会自动实现监控
```code

// 参考代码实现 wxdgaming.boot2.core.proxy.MainThreadStopWatchAspect.beforeAdvice
// 会自动记录代理方法耗时情况

```

### 2. 如果你不想通过aop实现，可以通过如下方式实现
```code

executorServicePlatform.scheduleAtFixedRate(new AbstractEventRunnable() {
    @Override public void onEvent() throws Exception {
        log.debug("scheduleAtFixedRate");
        ExecutorContext.context().startWatch("scheduleAtFixedRate");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        ExecutorContext.context().stopWatch();
    }
}, 0, 1, TimeUnit.SECONDS);

// 自定义监听开始代码
ExecutorContext.context().startWatch("scheduleAtFixedRate");
// xxxxx 自己的业务逻辑
// 逻辑完成关闭监听
ExecutorContext.context().stopWatch();
```

### 输出示例:
```code
[01-28 10:20:03.021] [ERROR] [virtual-220] [ExecutorContext#cleanup:82] - 线程执行耗时过大：
    线程: virtual-220
  线程池: ExecutorServiceVirtual{name='virtual', threadSize=32, queueSize=5000, queuePolicy=AbortPolicy, stoping=false}
    队列: null
    任务: wxdgaming.boot2.starter.net.httpclient5.AbstractHttpRequest$$Lambda/0x0000021a81a8f918@3d3cb88
提交时间: 2026-01-28 10:20:00, 开始时间: 2026-01-28 10:20:00
提交耗时: 00:00:03.016, 执行耗时: 00:00:03.016
线程数据：{}
耗时追踪: ↓↓↓↓
name: wxdgaming.boot2.starter.net.httpclient5.AbstractHttpRequest$$Lambda/0x0000021a81a8f918@3d3cb88, cost: 3016861 MICROSECONDS
-----------------------------------------------------------------------
name: HttpClientProperties.getKeepAliveTimeout, cost: 12

```

## 线程池的“协程”处理
这里所说的协程并不是真正的协程，

只是模拟丢出去的异步的业务会回到当前线程继续执行回调

比如说我们执行一个任务需要向跨服服务器执行 rpc 请求获取数据，并且我们希望数据回来后处理线程依然是当前线程
```code
AbstractExecutorService executorServiceVirtual = new ExecutorServiceVirtual("virtual", 100, 1000, QueuePolicyConst.AbortPolicy);
AbstractExecutorService executorServicePlatform = new ExecutorServicePlatform("platform", 4, 1000, QueuePolicyConst.AbortPolicy);

executorServicePlatform.execute(new Run1() {

    @Override public void run() {

        ExecutorContext.Content context = ExecutorContext.context();
        context.getData().put("async", "d");
        int taskId = taskIdFactory.incrementAndGet();
        log.debug("taskId:{} 发起协程队列 queue:{}", taskId, ExecutorContext.context().queueName());
        CompletableFuture<Void> coroutine = executorServiceVirtual.coroutine(() -> {
            log.debug("taskId:{} 协程队列执行中", taskId);
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
            log.debug("taskId:{} 协程队列执行结束", taskId);
        });
        coroutine.whenComplete((v, e) -> {
            log.debug("taskId:{} 协程队列回调 queue:{}", taskId, ExecutorContext.context().queueName());
        });
    }

});

我们在平台线程 executorServicePlatform 执行任务的时候，
向 executorServiceVirtual线程池发起 coroutine 任务
回调 coroutine.whenComplete 处理线程池依然会回到 executorServicePlatform 线程继续执行

```

输出结果：
```code
[01-28 11:07:30.550] [DEBUG] [platform-1] [ExecutorServiceFutureCallBackTest$1#run:73] - taskId:1 发起协程队列 queue:abc
[01-28 11:07:30.560] [DEBUG] [virtual-0] [ExecutorServiceFutureCallBackTest$1#lambda$run$0:75] - taskId:1 协程队列执行中
[01-28 11:07:30.561] [DEBUG] [platform-2] [ExecutorServiceFutureCallBackTest$2#run:90] - taskId:2 队列任务发起协程仍然是队列任务 queue:abc
[01-28 11:07:30.562] [DEBUG] [virtual-1] [ExecutorServiceFutureCallBackTest$2$1#run:93] - taskId:2 队列任务发起协程仍然是队列任务 协程队列执行中 queue:abc
[01-28 11:07:32.567] [DEBUG] [virtual-0] [ExecutorServiceFutureCallBackTest$1#lambda$run$0:77] - taskId:1 协程队列执行结束
[01-28 11:07:32.567] [DEBUG] [virtual-1] [ExecutorServiceFutureCallBackTest$2$1#run:95] - taskId:2 队列任务发起协程仍然是队列任务 协程队列执行结束 queue:abc
[01-28 11:07:32.568] [DEBUG] [platform-1] [ExecutorServiceFutureCallBackTest$1#lambda$run$1:80] - taskId:1 协程队列回调 queue:abc
[01-28 11:07:32.568] [DEBUG] [platform-1] [ExecutorServiceFutureCallBackTest$2#lambda$run$0:99] - taskId:2 队列任务发起协程仍然是队列任务 回调 queue:abc
```
