package wxdgaming.minitieba.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.service.UserService;

import java.util.Map;

/**
 * 用户API
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @RequestMapping("/register")
    public RunResult register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        return userService.register(username, password, nickname);
    }

    /** 登录 */
    @RequestMapping("/login")
    public RunResult login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return userService.login(username, password);
    }

    /** 获取当前用户信息（通过Token验证） */
    @RequestMapping("/info")
    public RunResult info(@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.verifyToken(token);
    }

    /** 修改昵称 - 需要登录 */
    @RequestMapping("/updateNickname")
    public RunResult updateNickname(@RequestBody Map<String, String> params,
                                     HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            return RunResult.fail("请先登录");
        }
        String nickname = params.get("nickname");
        return userService.updateNickname(username, nickname);
    }

    /** 获取用户公开信息 - 无需登录 */
    @RequestMapping("/public/info")
    public RunResult getUserInfo(@RequestParam String username) {
        return userService.getUserInfo(username);
    }

}
