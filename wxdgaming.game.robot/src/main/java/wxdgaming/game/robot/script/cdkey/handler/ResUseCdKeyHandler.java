package wxdgaming.game.robot.script.cdkey.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.cdkey.ResUseCdKey;

/**
 * 响应使用cdkey
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResUseCdKeyHandler {

    /** 响应使用cdkey */
    @ProtoRequest
    public void resUseCdKey(SocketSession socketSession, ResUseCdKey req) {

    }

}