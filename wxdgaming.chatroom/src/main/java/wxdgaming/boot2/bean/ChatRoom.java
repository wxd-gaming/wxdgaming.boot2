package wxdgaming.boot2.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.SessionGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 房间
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 10:58
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ChatRoom extends ObjectBase {

    private long roomId;
    /** 归属的角色 */
    private String master;
    /** 房间标题 */
    private String title;
    private int maxUser;
    /** 进入房间的密钥 */
    private String token;

    private final Set<String> userMap = new ConcurrentSkipListSet<>();
    private final SessionGroup sessionGroup = new SessionGroup();

    public Map<String, String> toBean() {
        HashMap<String, String> objectObjectHashMap = MapOf.newHashMap();
        objectObjectHashMap.put("roomId", String.valueOf(roomId));
        objectObjectHashMap.put("master", master);
        objectObjectHashMap.put("title", title);
        objectObjectHashMap.put("maxUser", String.valueOf(maxUser));
        objectObjectHashMap.put("innerUser", String.valueOf(userMap.size()));
        return objectObjectHashMap;
    }


}
