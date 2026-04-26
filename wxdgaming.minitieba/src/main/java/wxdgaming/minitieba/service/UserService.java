package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.minitieba.MinitiebaProperties;
import wxdgaming.minitieba.bean.User;

import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class UserService {

    private final RocksDBHelper db;
    private final MinitiebaProperties properties;

    private static final String USER_PREFIX = "user:";
    private static final String USER_ID_GENERATOR = "user_id_generator";

    public UserService(RocksDBHelper db, MinitiebaProperties properties) {
        this.db = db;
        this.properties = properties;
    }

    /** 密码签名：MD5(password + jwtKey) */
    private String signPassword(String password) {
        return Md5Util.md5(password + properties.getJwtKey());
    }

    /** 生成自增ID（synchronized 保证并发安全） */
    private synchronized long nextId() {
        long current = db.getLongValue(USER_ID_GENERATOR);
        long next = current + 1;
        db.put(USER_ID_GENERATOR, next);
        return next;
    }

    /** 注册（synchronized 防止并发重复注册） */
    public synchronized RunResult register(String username, String password, String nickname) {
        if (username == null || username.length() < 2 || username.length() > 20) {
            return RunResult.fail("用户名长度需2-20位");
        }
        if (password == null || password.length() < 4) {
            return RunResult.fail("密码长度至少4位");
        }

        // 检查用户名是否已存在
        User existing = db.getObject(USER_PREFIX + username, User.class);
        if (existing != null) {
            return RunResult.fail("用户名已存在");
        }

        String signedPwd = signPassword(password);
        if (nickname == null || nickname.isBlank()) {
            nickname = username;
        }

        User user = new User(username, signedPwd, nickname);
        db.put(USER_PREFIX + username, user);

        log.info("用户注册成功: username={}", username);
        return RunResult.ok().fluentPut("msg", "注册成功");
    }

    /** 登录 */
    public RunResult login(String username, String password) {
        if (username == null || password == null) {
            return RunResult.fail("用户名和密码不能为空");
        }

        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user == null) {
            return RunResult.fail("用户不存在");
        }

        String signedPwd = signPassword(password);
        if (!user.getPassword().equals(signedPwd)) {
            return RunResult.fail("密码错误");
        }

        // 更新登录时间
        user.setLastLoginTime(System.currentTimeMillis());
        db.put(USER_PREFIX + username, user);

        // 生成Token
        String token = JsonTokenBuilder.of(properties.getJwtKey(), TimeUnit.HOURS, properties.getTokenExpireHours())
                .put("username", user.getUsername())
                .put("nickname", user.getNickname())
                .compact();

        log.info("用户登录成功: username={}", username);
        return RunResult.ok()
                .fluentPut("token", token)
                .fluentPut("username", user.getUsername())
                .fluentPut("nickname", user.getNickname())
                .fluentPut("avatar", user.getAvatar());
    }

    /** 根据用户名获取用户 */
    public User getUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return db.getObject(USER_PREFIX + username, User.class);
    }

    /** 验证Token并返回用户信息 */
    public RunResult verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return RunResult.fail("Token为空");
        }
        try {
            wxdgaming.boot2.core.token.JsonToken jsonToken = JsonTokenParse.parse(properties.getJwtKey(), token);
            if (jsonToken == null) {
                return RunResult.fail("Token无效");
            }
            String username = jsonToken.getString("username");
            String nickname = jsonToken.getString("nickname");
            // 从数据库获取头像
            User user = db.getObject(USER_PREFIX + username, User.class);
            String avatar = user != null ? user.getAvatar() : null;
            return RunResult.ok()
                    .fluentPut("username", username)
                    .fluentPut("nickname", nickname)
                    .fluentPut("avatar", avatar);
        } catch (IllegalArgumentException e) {
            return RunResult.fail("Token已过期");
        }
    }

    /** 修改昵称 */
    public RunResult updateNickname(String username, String newNickname) {
        if (username == null || username.isBlank()) {
            return RunResult.fail("用户名不能为空");
        }
        if (newNickname == null || newNickname.isBlank()) {
            return RunResult.fail("昵称不能为空");
        }
        if (newNickname.length() > 20) {
            return RunResult.fail("昵称长度不能超过20位");
        }

        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user == null) {
            return RunResult.fail("用户不存在");
        }

        user.setNickname(newNickname);
        db.put(USER_PREFIX + username, user);

        log.info("用户修改昵称: username={}, oldNickname={}, newNickname={}", 
                username, user.getNickname(), newNickname);
        return RunResult.ok().fluentPut("nickname", newNickname);
    }

    /** 获取用户公开信息 */
    public RunResult getUserInfo(String username) {
        if (username == null || username.isBlank()) {
            return RunResult.fail("用户名不能为空");
        }
        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user == null) {
            return RunResult.fail("用户不存在");
        }
        // 确保 nickname 不为空
        String displayName = user.getNickname();
        if (displayName == null || displayName.isBlank()) {
            displayName = user.getUsername();
        }
        return RunResult.ok()
                .fluentPut("username", user.getUsername())
                .fluentPut("nickname", displayName)
                .fluentPut("avatar", user.getAvatar())
                .fluentPut("signature", user.getSignature());
    }

    /** 修改头像 */
    public RunResult updateAvatar(String username, String avatar) {
        if (username == null || username.isBlank()) {
            return RunResult.fail("用户名不能为空");
        }
        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user == null) {
            return RunResult.fail("用户不存在");
        }
        user.setAvatar(avatar);
        db.put(USER_PREFIX + username, user);
        log.info("用户修改头像: username={}, avatar={}", username, avatar);
        return RunResult.ok().fluentPut("avatar", avatar);
    }

    /** 修改签名 */
    public RunResult updateSignature(String username, String signature) {
        if (username == null || username.isBlank()) {
            return RunResult.fail("用户名不能为空");
        }
        if (signature != null && signature.length() > 128) {
            return RunResult.fail("签名不能超过128个字");
        }
        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user == null) {
            return RunResult.fail("用户不存在");
        }
        user.setSignature(signature != null ? signature.trim() : null);
        db.put(USER_PREFIX + username, user);
        log.info("用户修改签名: username={}, signature={}", username, signature);
        return RunResult.ok().fluentPut("signature", user.getSignature());
    }

}
