package wxdgaming.game.server.bean.global.impl;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.common.bean.global.AbstractGlobalData;
import wxdgaming.game.server.bean.mail.ServerMailInfo;

import java.util.ArrayList;

/**
 * 全服邮件数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 10:59
 **/
@Getter
@Setter
public class ServerMailData extends AbstractGlobalData {

    private ArrayList<ServerMailInfo> mailInfoList = new ArrayList<>();

}
