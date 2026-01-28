package wxdgaming.game.authority;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.executor.ExecutorContext;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;

import java.util.Collections;
import java.util.List;

/**
 * 用于网页传递的token信息 权限 wxdgaming.game.authority
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 15:18
 **/
@Slf4j
@Getter
@Setter
public class AdminUserToken extends ObjectBase {

    public static AdminUserToken threadContext() {
        return ExecutorContext.contextT("adminUserToken");
    }

    public static void threadContext(AdminUserToken adminUserToken) {
        ExecutorContext.context().getData().put("adminUserToken", adminUserToken);
    }

    public static AdminUserToken parse(HttpServletRequest request, String jwtKey) {
        String token = SpringUtil.getAuthor(request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            JsonToken parse = JsonTokenParse.parse(jwtKey, token);
            return parse.getObject("user", AdminUserToken.class);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("token解析错误 {}", StackUtils.stack(e.getStackTrace()));
            }
            return null;
        }
    }

    private long loginCount;
    private boolean admin;
    private String userName;
    private String phone;
    /** 过期时间 */
    private long expireTime;
    private int lv = 0;
    /** 路由权限 */
    private List<String> routes = Collections.emptyList();


}
