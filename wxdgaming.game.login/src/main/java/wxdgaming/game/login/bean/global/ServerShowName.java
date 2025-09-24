package wxdgaming.game.login.bean.global;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 服务器冠名
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 14:52
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServerShowName {

    /** 服务器新名字 */
    private String name;
    /** 过期时间 */
    private long expireTime;

    @Override public String toString() {
        return "ServerShowName{name='%s', expireTime=%d}".formatted(name, expireTime);
    }
}
