package wxdgaming.game.login.smail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.bean.ServerMailDTO;
import wxdgaming.game.login.entity.ServerMailEntity;

import java.util.List;

/**
 * 全服邮件服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-22 20:33
 **/
@Slf4j
@Service
public class ServerMailService extends HoldApplicationContext {

    private final SqlDataHelper sqlDataHelper;

    public ServerMailService(PgsqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @EventListener
    public void init(InitEvent event) {
        ServerMailEntity serverMailEntity = sqlDataHelper.findByKey(ServerMailEntity.class, 1);
        if (serverMailEntity == null) {
            serverMailEntity = new ServerMailEntity();
            serverMailEntity.setUid(1);
            serverMailEntity.setTitle("欢迎来到無心道游戏系统");
            serverMailEntity.setContent("欢迎来到無心道游戏系统");
            serverMailEntity.setCreateAdmin("系统");
            serverMailEntity.setCreateTime(System.currentTimeMillis());
            serverMailEntity.setAuditAdmin("系统");
            serverMailEntity.setAuditTime(System.currentTimeMillis());
            sqlDataHelper.save(serverMailEntity);
        }
    }

    public List<ServerMailEntity> serverMailEntities() {
        return sqlDataHelper.findList(ServerMailEntity.class);
    }

    public List<ServerMailDTO> serverMailDTOList(int serverId) {
        List<ServerMailEntity> list = serverMailEntities();
        return list.stream()
                .filter(entity -> entity.getAuditTime() > 0/*TODO 说明已经审核了*/)
                .filter(entity -> entity.checkServerId(serverId) && entity.validTime())
                .map(entity -> entity.toJSONObject().toJavaObject(ServerMailDTO.class))
                .toList();
    }

}
