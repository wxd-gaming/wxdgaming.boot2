package wxdgaming.game.test.module.data;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.global.DataBase;
import wxdgaming.game.test.bean.global.GlobalDataEntity;
import wxdgaming.game.test.bean.global.GlobalDataType;

import java.util.List;

/**
 * 全局数据服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 11:05
 **/
@Slf4j
@Singleton
public class GlobalDataService extends HoldRunApplication {

    private int sid;
    private PgsqlService pgsqlService;
    /** key: sid, key: type, value: 数据 */
    private final ConcurrentTable<Integer, GlobalDataType, GlobalDataEntity> globalDataTable = new ConcurrentTable<>();


    @Init
    public void init(@Value(path = "sid") int sid, PgsqlService pgsqlService) {
        this.sid = sid;
        this.pgsqlService = pgsqlService;
        List<GlobalDataEntity> list = this.pgsqlService.findListByWhere(GlobalDataEntity.class, "merge = ?", false);
        for (GlobalDataEntity entity : list) {
            GlobalDataType globalDataType = GlobalDataType.ofOrException(entity.getId());
            globalDataTable.put(entity.getSid(), globalDataType, entity);
        }
    }

    public <T extends DataBase> T get(GlobalDataType type) {
        DataBase data = globalDataTable.computeIfAbsent(
                sid,
                type,
                l ->
                        new GlobalDataEntity()
                                .setId(type.getCode())
                                .setSid(sid)
                                .setData(type.getSupplier().get())
        ).getData();
        return (T) data;
    }

    public GlobalDataEntity get(int sid, GlobalDataType type) {
        return globalDataTable.get(sid, type);
    }

    public void save(GlobalDataEntity globalDataEntity) {
        pgsqlService.save(globalDataEntity);
    }

}
