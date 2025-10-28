package wxdgaming.game.login.notice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.starter.batis.DataTable;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.entity.NoticeEntity;

import java.util.Collection;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-23 14:25
 **/
@Slf4j
@Getter
@Service
public class NoticeService extends HoldApplicationContext {

    final DataTable<NoticeEntity> dataTable;
    final SqlDataHelper sqlDataHelper;

    public NoticeService(PgsqlDataHelper pgsqlDataHelper) {
        this.dataTable = new DataTable<>(NoticeEntity.class, pgsqlDataHelper, EntityIntegerUID::getUid);
        this.sqlDataHelper = pgsqlDataHelper;
    }

    @EventListener
    public void startEvent(StartEvent startEvent) {
        dataTable.loadAll();
    }

    public Collection<NoticeEntity> list() {
        return dataTable.getList();
    }

}
