package wxdgaming.game.test.script;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jsonwebtoken.JwtBuilder;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClientImpl;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.test.bean.Robot;
import wxdgaming.game.test.script.role.message.ReqHeartbeat;
import wxdgaming.game.test.script.role.message.ReqLogin;

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
        String account = "test2";
        robotMap.put(account, new Robot().setAccount(account).setName(account));
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
                    connect.attribute("robot", robot);

                    connect.write(new ReqLogin().setAccount(robot.getAccount()).setSid(1).setToken(token));

                });
            } else {
                ReqHeartbeat reqHeartbeat = new ReqHeartbeat();
                socketSession.write(reqHeartbeat);
            }
        }
    }

}
