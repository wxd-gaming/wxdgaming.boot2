package wxdgaming.game.server.script.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.global.GlobalDataType;
import wxdgaming.game.server.bean.global.impl.ServerMailData;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.mail.MailInfo;
import wxdgaming.game.server.bean.mail.MailPack;
import wxdgaming.game.server.bean.mail.ServerMailInfo;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnHeartMinute;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.data.GlobalDataService;

import java.util.ArrayList;
import java.util.List;

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
    final DataCenterService dataCenterService;

    @Inject
    public MailService(GlobalDataService globalDataService, DataCenterService dataCenterService) {
        this.globalDataService = globalDataService;
        this.dataCenterService = dataCenterService;
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

    public void sendBagFullMail(Player player, List<Item> items, ReasonArgs reasonArgs) {
        sendMail(player, "系统", "背包已满", "背包已满，无法获得物品", List.of(), items, reasonArgs.toString());
    }

    public void sendMail(Player player, String sender, String title, String content, List<String> contentArgs, List<Item> items, String logMsg) {
        MailInfo mailInfo = new MailInfo();
        mailInfo.setUid(dataCenterService.getMailHexid().newId());
        mailInfo.setSender(sender);
        mailInfo.setSendTime(MyClock.millis());
        mailInfo.setTitle(title);
        mailInfo.setContent(content);
        mailInfo.getContentParams().addAll(contentArgs);
        mailInfo.setItems(items);
        mailInfo.setSourceLog(logMsg);
        player.getMailPack().getMailInfoList().add(mailInfo);
        log.info("获得邮件：{}, {}", player, mailInfo);
    }

}
