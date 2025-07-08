package wxdgaming.boot2.module.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.bean.ChatUser;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.util.concurrent.TimeUnit;

/**
 * 聊天用户服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 11:01
 **/
@Singleton
public class ChatUserService extends HoldRunApplication {

    @Value(path = "json.token.key", nestedPath = true)
    private String jsonTokenKey;

    final MapDBDataHelper mapDBDataHelper;

    @Inject
    public ChatUserService(MapDBDataHelper mapDBDataHelper) {
        this.mapDBDataHelper = mapDBDataHelper;
    }

    public String token(ChatUser chatUser) {
        JsonTokenBuilder ss = JsonTokenBuilder.of(jsonTokenKey, TimeUnit.DAYS, 7);
        ss.put("name", chatUser.getName());
        return ss.compact();
    }

    public ChatUser parseChatUser(String token) {
        JsonToken jsonToken = JsonTokenParse.parse(jsonTokenKey, token);
        String name = jsonToken.getString("name");
        ChatUser chatUser = chatUser(name);
        AssertUtil.assertNull(chatUser, "用户不存在");
        return chatUser;
    }

    /** 查询用户 */
    public ChatUser chatUser(String name) {
        HoldMap holdMap = mapDBDataHelper.bMap("chat-user");
        return holdMap.get(name);
    }

    /** 添加用户 */
    public void addChatUser(ChatUser chatUser) {
        HoldMap holdMap = mapDBDataHelper.bMap("chat-user");
        holdMap.put(chatUser.getName(), chatUser);
    }

}
