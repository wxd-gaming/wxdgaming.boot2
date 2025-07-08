package wxdgaming.boot2.module.user.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.bean.ChatRoom;
import wxdgaming.boot2.bean.ChatUser;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.module.data.DataService;
import wxdgaming.boot2.module.user.ChatUserService;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;

import java.util.List;
import java.util.Map;

/**
 * 注册接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 11:04
 **/
@Slf4j
@Singleton
@RequestMapping(path = "/api/chat/user")
public class ChatUserController {

    final ChatUserService chatUserService;
    final DataService dataService;

    @Inject
    public ChatUserController(ChatUserService chatUserService, DataService dataService) {
        this.chatUserService = chatUserService;
        this.dataService = dataService;
    }

    @HttpRequest
    public RunResult register(HttpContext httpContext, @Param(path = "name") String name, @Param(path = "password") String token) {

        AssertUtil.assertNull(StringUtils.isBlank(name) || StringUtils.length(name) < 6, "用户名长度不能小于6");
        AssertUtil.assertNull(StringUtils.isBlank(token) || StringUtils.length(token) < 6, "密码长度不能小于6");

        SingletonLockUtil.lock(name);
        try {
            ChatUser chatUser = chatUserService.chatUser(name);
            if (chatUser != null) {
                return RunResult.fail("用户已存在");
            }
            chatUser = new ChatUser();
            chatUser.setName(name);
            chatUser.setToken(token);
            chatUserService.addChatUser(chatUser);
            return login(httpContext, name, token);
        } finally {
            SingletonLockUtil.unlock(name);
        }
    }

    @HttpRequest
    public RunResult checkToken(HttpContext httpContext, @Param(path = "token") String token) {
        ChatUser chatUser = chatUserService.parseChatUser(token);
        return loginSuccess(chatUser);
    }

    @HttpRequest
    public RunResult login(HttpContext httpContext, @Param(path = "name") String name, @Param(path = "password") String token) {
        ChatUser chatUser = chatUserService.chatUser(name);
        if (chatUser == null) {
            return RunResult.fail("用户不存在");
        }

        if (!Objects.equals(token, chatUser.getToken())) {
            return RunResult.fail("密码错误");
        }

        return loginSuccess(chatUser);
    }

    RunResult loginSuccess(ChatUser chatUser) {
        String token = chatUserService.token(chatUser);
        ChatRoom publicChatRoom = dataService.getPublicChatRoom();

        List<Map<String, String>> roomList = dataService.getRoomMap().values().stream()
                .filter(room -> room.getUserMap().contains(chatUser.getName()))
                .map(ChatRoom::toBean)
                .toList();

        return RunResult.ok()
                .fluentPut("name", chatUser.getName())
                .fluentPut("token", token)
                .fluentPut("publicChatRoom", publicChatRoom)
                .fluentPut("roomList", roomList);

    }


}
