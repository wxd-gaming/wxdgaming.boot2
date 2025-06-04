package wxdgaming.game.robot.script;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jsonwebtoken.JwtBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClientImpl;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.message.chat.ChatType;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.message.role.ReqHeartbeat;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.message.task.ReqAcceptTask;
import wxdgaming.game.message.task.ReqSubmitTask;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.robot.bean.Robot;

import java.util.concurrent.ConcurrentHashMap;

/**
 * socket
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 13:20
 **/
@Getter
@Singleton
public class RobotMainService {

    SocketClientImpl socketClient;
    ConcurrentHashMap<String, Robot> robotMap = new ConcurrentHashMap<>();

    @Inject
    public RobotMainService(SocketClientImpl socketClient) {
        this.socketClient = socketClient;
    }

    @Start
    public void start() {
        for (int i = 0; i < 1; i++) {
            String account = "a6" + (i + 1);
            robotMap.put(account, new Robot().setAccount(account).setName(account));
        }
    }

    /** 1秒一次主循环 */
    @Scheduled("*/5")
    public void timer() {
        for (Robot robot : robotMap.values()) {
            SocketSession socketSession = robot.getSocketSession();
            if (socketSession == null || !socketSession.isOpen()) {
                socketClient.connect(connect -> {
                    robot.setSendLogin(true);

                    JwtBuilder jwtBuilder = JwtUtils.createJwtBuilder();
                    jwtBuilder.claim("account", robot.getAccount());
                    String token = jwtBuilder.compact();

                    robot.setSocketSession(connect);
                    connect.getChannel().closeFuture().addListener(new ChannelFutureListener() {
                        @Override public void operationComplete(ChannelFuture future) throws Exception {
                            robot.setSendLogin(false);
                            robot.setLoginEnd(false);
                            robot.setSocketSession(null);
                        }
                    });
                    connect.bindData("robot", robot);

                    connect.write(new ReqLogin().setAccount(robot.getAccount()).setSid(1).setToken(token));

                });
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
            if (robot.isLoginEnd()) {
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
    @Scheduled(value = "*/5", async = true)
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

}
