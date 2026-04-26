package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 用户名 */
    private String username;
    /** 密码（MD5签名） */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 头像URL */
    private String avatar;
    /** 个性签名 */
    private String signature;
    /** 最后修改昵称时间 */
    private long nicknameUpdateTime;
    /** 注册时间 */
    private long createTime;
    /** 最后登录时间 */
    private long lastLoginTime;

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.createTime = System.currentTimeMillis();
        this.lastLoginTime = this.createTime;
    }

}
