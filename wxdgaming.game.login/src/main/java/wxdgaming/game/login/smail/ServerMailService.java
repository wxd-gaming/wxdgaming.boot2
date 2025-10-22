package wxdgaming.game.login.smail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
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

    public List<ServerMailEntity> serverMailEntities() {
        return sqlDataHelper.findList(ServerMailEntity.class);
    }

    public List<ServerMailDTO> serverMailDTOList(int serverId) {
        List<ServerMailEntity> list = sqlDataHelper.findList(ServerMailEntity.class);
        return list.stream()
                .filter(entity -> entity.checkServerId(serverId) && entity.validTime())
                .map(entity -> entity.toJSONObject().toJavaObject(ServerMailDTO.class))
                .toList();
    }

}
