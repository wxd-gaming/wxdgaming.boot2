package wxdgaming.game.server.module.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.global.DataBase;
import wxdgaming.game.server.bean.global.GlobalDataEntity;
import wxdgaming.game.server.bean.global.GlobalDataType;

import java.util.List;

/**
 * 本服的全局数据服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 11:05
 **/
@Slf4j
@Getter
@Service
public class GlobalDataService extends HoldApplicationContext {

    final GameServerProperties gameServerProperties;
    private SqlDataHelper sqlDataHelper;
    /** key: sid, key: type, value: 数据 */
    private final ConcurrentTable<Integer, GlobalDataType, GlobalDataEntity> globalDataTable = new ConcurrentTable<>();

    public GlobalDataService(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
    }

    @Start
    public void start() {
        this.sqlDataHelper = applicationContextProvider.getBean(SqlDataHelper.class);
        List<GlobalDataEntity> list = this.sqlDataHelper.findListByWhere(GlobalDataEntity.class, "merge = ?", false);
        for (GlobalDataEntity entity : list) {
            GlobalDataType globalDataType = GlobalDataType.ofOrException(entity.getId());
            globalDataTable.put(entity.getSid(), globalDataType, entity);
        }
    }

    @Order(100)
    @Stop
    public void stop() {
        globalDataTable.forEach(globalDataEntity -> {
            sqlDataHelper.dataBatch().save(globalDataEntity);
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends DataBase> T get(GlobalDataType type) {
        DataBase data = globalDataTable.computeIfAbsent(
                gameServerProperties.getSid(),
                type,
                l ->
                        new GlobalDataEntity()
                                .setId(type.getCode())
                                .setSid(gameServerProperties.getSid())
                                .setData(type.getFactory().get())
        ).getData();
        return (T) data;
    }

    public GlobalDataEntity get(int sid, GlobalDataType type) {
        return globalDataTable.get(sid, type);
    }

    public void save(GlobalDataEntity globalDataEntity) {
        sqlDataHelper.save(globalDataEntity);
    }

}
