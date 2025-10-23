package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.UserDataVo;
import wxdgaming.game.login.entity.UserData;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.login.login.LoginService;

import java.util.List;

/**
 * 列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-18 20:12
 **/
@Slf4j
@Controller
@RequestMapping("/gameServer")
public class GameServerController implements InitPrint {

    final LoginServerProperties loginServerProperties;
    final InnerService innerService;
    final LoginService loginService;

    public GameServerController(LoginServerProperties loginServerProperties, InnerService innerService, LoginService loginService) {
        this.loginServerProperties = loginServerProperties;
        this.innerService = innerService;
        this.loginService = loginService;
    }

    @RequestMapping(value = "/list")
    public RunResult list(@RequestParam(value = "token", required = false) String token) {
        UserData userData = null;
        if (StringUtils.isNotBlank(token)) {
            try {
                JsonToken jsonToken = JsonTokenParse.parse(loginServerProperties.getJwtKey(), token);
                UserDataVo userDataVo = jsonToken.getObject("user", UserDataVo.class);
                String account = userDataVo.getAccount();
                userData = loginService.userData(account);
            } catch (Exception e) {
                log.debug("token解析失败:{}", e.getMessage());
            }
        }
        List<JSONObject> list = innerService.gameServerList(userData);
        return RunResult.ok().data(list);
    }

}
