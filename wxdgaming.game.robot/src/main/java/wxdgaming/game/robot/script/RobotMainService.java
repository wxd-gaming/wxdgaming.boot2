package wxdgaming.game.robot.script;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.collection.ListOf;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.message.role.ReqHeartbeat;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.message.task.ReqAcceptTask;
import wxdgaming.game.message.task.ReqSubmitTask;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * socket
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 13:20
 **/
@Slf4j
@Getter
@Service
public class RobotMainService {

    final ConnectLoginProperties connectLoginProperties;
    final SocketClient socketClient;
    final ConcurrentHashMap<String, Robot> robotMap = new ConcurrentHashMap<>();

    public RobotMainService(ConnectLoginProperties connectLoginProperties, SocketClient socketClient) {
        this.connectLoginProperties = connectLoginProperties;
        this.socketClient = socketClient;
    }

    @EventListener
    public void start(StartEvent event) {
        for (int i = 0; i < 100; i++) {
            String account = "r19" + (i + 1);
            robotMap.put(account, new Robot().setAccount(account).setName(account));
        }
    }

    public RunResult httpLogin(Robot robot) {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("appId", 1);
        jsonObject.put("platform", 1);
        jsonObject.put("account", robot.getAccount());
        jsonObject.put("token", robot.getAccount());

        String uriPath = getConnectLoginProperties().getUrl() + "/login/check";
        HttpResponse httpResponse = HttpRequestPost.of(uriPath, jsonObject).execute();
        RunResult runResult = httpResponse.bodyRunResult();
        if (runResult.isFail()) {
            log.error("登录失败：{}", runResult.msg());
        }
        return runResult;
    }

    /** 1秒一次主循环 */
    @Scheduled("*/5")
    public void timer() {
        for (Robot robot : robotMap.values()) {
            SocketSession socketSession = robot.getSocketSession();
            if (socketSession == null || !socketSession.isOpen()) {
                RunResult runResult = httpLogin(robot);
                if (runResult.isOk()) {
                    List<JSONObject> serverList = runResult.getObject("serverList", new TypeReference<List<JSONObject>>() {});
                    if (ListOf.isEmpty(serverList)) {
                        log.info("游戏服。。。。。");
                        return;
                    }
                    List<JSONObject> list = serverList.stream().filter(v -> StringUtils.isNotBlank(v.getString("host"))).toList();
                    JSONObject jsonObject = RandomUtils.randomItem(list);
                    log.info("登录成功：{}, 选择游戏服={}", robot, jsonObject);
                    String host = jsonObject.getString("host");
                    int port = jsonObject.getInteger("port");
                    socketClient.connect(host, port, connect -> {
                        robot.setSocketSession(connect);
                        connect.getChannel().closeFuture().addListener(new ChannelFutureListener() {
                            @Override public void operationComplete(ChannelFuture future) throws Exception {
                                robot.setSendLogin(false);
                                robot.setLoginEnd(false);
                                robot.setSocketSession(null);
                            }
                        });
                        connect.bindData("robot", robot);

                        robot.setSendLogin(true);
                        connect.write(
                                new ReqLogin()
                                        .setAccount(robot.getAccount())
                                        .setSid(1)
                                        .setToken(runResult.getString("token"))
                        );

                    });
                }
            } else {
                ReqHeartbeat reqHeartbeat = new ReqHeartbeat();
                socketSession.write(reqHeartbeat);
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled("*/30")
    public void timer30() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd() && robot.getLevel() > 10) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                ReqChatMessage reqChatMessage = new ReqChatMessage();
                reqChatMessage.setType(ChatType.Chat_TYPE_World);
                reqChatMessage.setContent("你好");
                robot.getSocketSession().write(reqChatMessage);
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled("*/5")
    public void timerTask() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd()) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                for (TaskBean taskBean : robot.getTasks().values()) {
                    if (!taskBean.isAccept()) {
                        ReqAcceptTask reqAcceptTask = new ReqAcceptTask();
                        reqAcceptTask.setTaskType(TaskType.Main);
                        reqAcceptTask.setTaskId(taskBean.getTaskId());
                        robot.getSocketSession().write(reqAcceptTask);
                    } else if (!taskBean.isCompleted()) {
                        ReqChatMessage reqChatMessage = new ReqChatMessage();
                        reqChatMessage.setType(ChatType.Chat_TYPE_World);
                        reqChatMessage.setContent("@gm completeTask " + taskBean.getTaskId());
                        robot.getSocketSession().write(reqChatMessage);
                    } else if (!taskBean.isReward()) {
                        ReqSubmitTask reqSubmitTask = new ReqSubmitTask();
                        reqSubmitTask.setTaskType(TaskType.Main);
                        reqSubmitTask.setTaskId(taskBean.getTaskId());
                        robot.getSocketSession().write(reqSubmitTask);
                    }
                }
            }
        }
    }

    /** 1秒一次主循环 */
    @Scheduled(value = "*/5")
    public void timer60() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd()) {
                if (!RandomUtils.randomBoolean()) {
                    continue;
                }
                ReqChatMessage reqChatMessage = new ReqChatMessage();
                reqChatMessage.setType(ChatType.Chat_TYPE_World);
                reqChatMessage.setContent("@gm addexp " + RandomUtils.random(20, 100));
                robot.getSocketSession().write(reqChatMessage);
            }
        }
    }

    AtomicBoolean resetTime = new AtomicBoolean();

    /** 设置时间 */
    @Scheduled(value = "0")
    public void timer61() {
        for (Robot robot : robotMap.values()) {
            if (robot.isLoginEnd()) {
                if (resetTime.compareAndSet(false, true)) {
                    ReqChatMessage reqChatMessage = new ReqChatMessage();
                    reqChatMessage.setType(ChatType.Chat_TYPE_World);
                    reqChatMessage.setContent("@gm time " + MyClock.formatDate("yyyy-MM-dd HH:mm:ss", MyClock.millis() + TimeUnit.MINUTES.toMillis(60)));
                    robot.getSocketSession().write(reqChatMessage);
                }
            }
        }
    }

}
