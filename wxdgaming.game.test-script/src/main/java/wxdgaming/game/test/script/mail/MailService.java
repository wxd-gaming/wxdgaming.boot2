package wxdgaming.game.test.script.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.game.test.bean.global.GlobalDataType;
import wxdgaming.game.test.bean.global.impl.ServerMailData;
import wxdgaming.game.test.bean.mail.MailPack;
import wxdgaming.game.test.bean.mail.ServerMailInfo;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.OnHeartMinute;
import wxdgaming.game.test.module.data.GlobalDataService;

import java.util.ArrayList;

/**
 * 邮件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-12 16:22
 **/
@Slf4j
@Singleton
public class MailService extends HoldRunApplication {

    final GlobalDataService globalDataService;

    @Inject
    public MailService(GlobalDataService globalDataService) {
        this.globalDataService = globalDataService;
    }

    @OnHeartMinute
    public void playerHeartMinute(Player player) {
        MailPack mailPack = player.getMailPack();
        mailPack.getMailInfoList().removeIf(v -> {
            if (!v.checkValidity()) {
                log.info("{} 邮件 {} 已过期", player, v);
                return true;
            }
            return false;
        });

        ServerMailData serverMailData = globalDataService.get(GlobalDataType.SERVER_MAIL_DATA);
        ArrayList<ServerMailInfo> mailInfoList = serverMailData.getMailInfoList();
        for (ServerMailInfo mailInfo : mailInfoList) {
            if (!mailInfo.checkValidity())
                continue;
            if (mailInfo.getLvMin() > player.getLevel() || mailInfo.getLvMax() < player.getLevel())
                continue;
            int vipLv = player.getVipInfo().getLv();
            if (mailInfo.getVipLvMin() > vipLv || mailInfo.getVipLvMax() < vipLv)
                continue;
            if (mailPack.getMailInfoList().contains(mailInfo))
                continue;
            mailPack.getMailInfoList().add(mailInfo);
            log.info("{} 领取邮件 {}", player, mailInfo);
        }
    }

}
