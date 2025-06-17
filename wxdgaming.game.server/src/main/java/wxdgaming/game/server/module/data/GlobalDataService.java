package wxdgaming.game.server.module.data;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.ann.Shutdown;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.server.bean.BackendConfig;
import wxdgaming.game.server.bean.global.DataBase;
import wxdgaming.game.server.bean.global.GlobalDataEntity;
import wxdgaming.game.server.bean.global.GlobalDataType;

import java.util.List;

/**
 * 本服的全局数据服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 11:05
 **/
@Slf4j
@Getter
@Singleton
public class GlobalDataService extends HoldRunApplication {

    private int sid;
    private SqlDataHelper<?> sqlDataHelper;
    /** key: sid, key: type, value: 数据 */
    private final ConcurrentTable<Integer, GlobalDataType, GlobalDataEntity> globalDataTable = new ConcurrentTable<>();
    @Value(path = "backends")
    private BackendConfig backendConfig;

    @Start
    public void start(@Value(path = "sid") int sid, SqlDataHelper<?> sqlDataHelper) {
        this.sid = sid;
        this.sqlDataHelper = sqlDataHelper;
        List<GlobalDataEntity> list = this.sqlDataHelper.findListByWhere(GlobalDataEntity.class, "merge = ?", false);
        for (GlobalDataEntity entity : list) {
            GlobalDataType globalDataType = GlobalDataType.ofOrException(entity.getId());
            globalDataTable.put(entity.getSid(), globalDataType, entity);
        }
    }

    @Shutdown
    public void shutdown() {
        globalDataTable.forEach(globalDataEntity -> {
            sqlDataHelper.dataBatch().save(globalDataEntity);
        });
    }

    public <T extends DataBase> T get(GlobalDataType type) {
        DataBase data = globalDataTable.computeIfAbsent(
                sid,
                type,
                l ->
                        new GlobalDataEntity()
                                .setId(type.getCode())
                                .setSid(sid)
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
