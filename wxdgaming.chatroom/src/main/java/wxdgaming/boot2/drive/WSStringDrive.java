package wxdgaming.boot2.drive;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.module.data.DataService;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.IWebSocketStringListener;

/**
 * ws的驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 10:52
 **/
@Slf4j
@Singleton
public class WSStringDrive implements IWebSocketStringListener {

    final DataService dataService;

    @Inject
    public WSStringDrive(DataService dataService) {
        this.dataService = dataService;
    }

    @Override public void onMessage(SocketSession socketSession, String message) {
        /*接受发过来的消息*/
        JSONObject jsonObject = FastJsonUtil.parseJSONObject(message);
        String token = jsonObject.getString("token");
        JsonToken jsonToken = JsonTokenParse.parse("ss", token);
        /*当前发起聊天的用户*/
        String name = jsonToken.getString("name");
        /*房间id*/
        long roomId = jsonToken.getLongValue("roomId");
        /*私聊对象*/
        String privateName = jsonToken.getString("privateName");

        System.out.println("ws接受到消息:" + message);
    }

}
