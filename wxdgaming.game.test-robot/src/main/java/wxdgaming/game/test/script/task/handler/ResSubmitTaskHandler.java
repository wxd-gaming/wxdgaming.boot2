package wxdgaming.game.test.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.task.message.ResSubmitTask;

/**
 * 提交任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResSubmitTaskHandler {

    /** 提交任务 */
    @ProtoRequest
    public void resSubmitTask(SocketSession socketSession, ResSubmitTask req) {

    }

}