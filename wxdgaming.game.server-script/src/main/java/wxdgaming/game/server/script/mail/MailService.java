package wxdgaming.game.server.script.mail;

import com.alibaba.fastjson.TypeReference;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.collection.ListOf;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.login.bean.ServerMailDTO;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.ServerMailData;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.mail.MailInfo;
import wxdgaming.game.server.bean.mail.MailPack;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.mail.slog.MailSlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 邮件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-12 16:22
 **/
@Slf4j
@Service
public class MailService extends HoldApplicationContext {

    final GlobalDataService globalDataService;
    final DataCenterService dataCenterService;
    final SlogService slogService;
    final ConnectLoginProperties connectLoginProperties;
    List<ServerMailDTO> serverMailDTOList = List.of();

    public MailService(GlobalDataService globalDataService, DataCenterService dataCenterService, SlogService slogService, ConnectLoginProperties connectLoginProperties) {
        this.globalDataService = globalDataService;
        this.dataCenterService = dataCenterService;
        this.slogService = slogService;
        this.connectLoginProperties = connectLoginProperties;
    }

    @Scheduled("0 * *")
    public void requestServerMail() {
        String url = connectLoginProperties.getUrl() + "/inner/game/serverMail/query";
        HashMap<String, String> stringHashMap = new HashMap<>();
        String sign = SignUtil.signByJsonKey(stringHashMap, connectLoginProperties.getJwtKey());
        HttpRequestPost.ofJson(url, stringHashMap)
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .executeAsync()
                .subscribe((response) -> {
                    log.debug("获取全服邮件列表 {}", response);
                    if (!response.isSuccess()) {
                        log.error("获取全服邮件列表失败, {}", response);
                        return;
                    }
                    RunResult runResult = response.bodyRunResult();
                    if (runResult.code() != 1) {
                        log.error("获取全服邮件列表失败, {}", runResult.msg());
                        return;
                    }
                    serverMailDTOList = runResult.getObject("data", new TypeReference<ArrayList<ServerMailDTO>>() {});
                });
    }

    @EventListener
    public void playerHeartMinute(EventConst.MapNpcHeartMinuteEvent event) {
        MapNpc mapNpc = event.mapNpc();
        Player player = (Player) mapNpc;
        MailPack mailPack = player.getMailPack();
        mailPack.getMailInfoList().removeIf(v -> {
            if (!v.checkValidity()) {
                log.info("{} 邮件 {} 已过期", player, v);
                return true;
            }
            return false;
        });

        ServerMailData serverMailData = globalDataService.get(GlobalDataConst.SERVER_MAIL_DATA);
        //        ArrayList<ServerMailInfo> mailInfoList = serverMailData.getMailInfoList();
        //        for (ServerMailInfo mailInfo : mailInfoList) {
        //            if (!mailInfo.checkValidity())
        //                continue;
        //            if (mailInfo.getLvMin() > mapNpc.getLevel() || mailInfo.getLvMax() < mapNpc.getLevel())
        //                continue;
        //            int vipLv = mapNpc.getVipInfo().getLv();
        //            if (mailInfo.getVipLvMin() > vipLv || mailInfo.getVipLvMax() < vipLv)
        //                continue;
        //            if (!mailInfo.getRidList().isEmpty() && !mailInfo.getRidList().contains(mapNpc.getUid()))
        //                /*指定的角色才可用领取*/
        //                continue;
        //            if (mailInfo.getRewardRidList().contains(mapNpc.getUid()))
        //                /*该角色已经领取过了*/
        //                continue;
        //            mailInfo.getRewardRidList().add(mapNpc.getUid());
        //            addMail(mapNpc, mailInfo);
        //        }
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
        addMail(player, mailInfo);
    }

    public void addMail(Player player, MailInfo mailInfo) {
        MailPack mailPack = player.getMailPack();
        mailPack.getMailInfoList().add(mailInfo);
        log.info("获得邮件：{}, {}", player, mailInfo);

        StringBuilder itemString = new StringBuilder();
        if (!ListOf.isEmpty(mailInfo.getItems())) {
            for (Item item : mailInfo.getItems()) {
                if (!itemString.isEmpty())
                    itemString.append(", ");
                itemString.append(item.qItem().getToName()).append("*").append(item.getCount());
            }
        }

        MailSlog mailSlog = new MailSlog(player, mailInfo.getUid(), mailInfo.getSender(),
                mailInfo.getTitle(), mailInfo.getContent(), mailInfo.getContentParams(),
                itemString.toString(),
                mailInfo.getSourceLog()
        );

        slogService.pushLog(mailSlog);
    }

}
