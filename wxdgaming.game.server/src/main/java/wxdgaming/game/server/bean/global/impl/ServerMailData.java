package wxdgaming.game.server.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.global.DataBase;
import wxdgaming.game.bean.mail.ServerMailInfo;

import java.util.ArrayList;

/**
 * 全服邮件数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 10:59
 **/
@Getter
@Setter
public class ServerMailData extends DataBase {

    private ArrayList<ServerMailInfo> mailInfoList = new ArrayList<>();

}
