package wxdgaming.boot2.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.bean.ChatRoom;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-08 11:00
 **/
@Slf4j
@Getter
@Singleton
public class DataService extends HoldRunApplication {

    private final ChatRoom publicChatRoom = new ChatRoom().setRoomId(1).setTitle("公共聊天室").setMaxUser(1000);
    private final AtomicLong atomicLong = new AtomicLong(1000);
    private final Map<Long, ChatRoom> roomMap = new java.util.concurrent.ConcurrentHashMap<>();
    final MapDBDataHelper mapDBDataHelper;

    @Inject
    public DataService(MapDBDataHelper mapDBDataHelper) {
        this.mapDBDataHelper = mapDBDataHelper;
    }


}
