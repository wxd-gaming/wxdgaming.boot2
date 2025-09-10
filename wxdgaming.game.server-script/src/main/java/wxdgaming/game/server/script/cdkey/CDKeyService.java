package wxdgaming.game.server.script.cdkey;

import com.alibaba.fastjson.TypeReference;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.login.bean.UseCDKeyDTO;
import wxdgaming.game.message.cdkey.ResUseCdKey;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.goods.BagChangeDTO4Item;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.inner.ConnectLoginService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.cdkey.bean.CDKeyReward;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.boot2.core.util.SignUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * cdkey
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-29 11:14
 **/
@Slf4j
@Getter
@Service
public class CDKeyService implements InitPrint {

    final ConnectLoginProperties connectLoginProperties;
    final GameServerProperties gameServerProperties;
    private final DataCenterService dataCenterService;
    private final TipsService tipsService;
    private final BagService bagService;
    final ConnectLoginService connectLoginService;


    public CDKeyService(ConnectLoginProperties connectLoginProperties, GameServerProperties gameServerProperties,
                        DataCenterService dataCenterService,
                        TipsService tipsService, BagService bagService,
                        ConnectLoginService connectLoginService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.bagService = bagService;
        this.connectLoginProperties = connectLoginProperties;
        this.gameServerProperties = gameServerProperties;
        this.connectLoginService = connectLoginService;
    }

    public void use(Player player, String cdKey) {

        String url = connectLoginProperties.getUrl();
        url = url + "/inner/game/cdkey/use";

        UseCDKeyDTO cdKeyDTO = new UseCDKeyDTO();
        cdKeyDTO.setCdKey(cdKey);
        cdKeyDTO.setSid(gameServerProperties.getSid());
        cdKeyDTO.setRoleId(player.getUid());
        cdKeyDTO.setRoleName(player.getName());
        cdKeyDTO.setAccount(player.getAccount());

        String sign = SignUtil.signByJsonKey(cdKeyDTO, connectLoginProperties.getJwtKey());
        HttpResponse execute = HttpRequestPost.ofJson(url, cdKeyDTO.toJSONString())
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .execute();
        RunResult runResult = execute.bodyRunResult();

        if (runResult.isFail()) {
            tipsService.tips(player, runResult.msg());
            return;
        }

        ArrayList<CDKeyReward> rewardCfgList = runResult.getObject("rewards", new TypeReference<ArrayList<CDKeyReward>>() {});

        List<ItemCfg> rewards = new ArrayList<>();
        for (CDKeyReward reward : rewardCfgList) {
            ItemCfg itemCfg = ItemCfg.builder()
                    .cfgId(reward.getCfgId())
                    .num(reward.getNum())
                    .bind(reward.isBind())
                    .expirationTime(reward.getExpirationTime())
                    .build();
            rewards.add(itemCfg);
        }

        ReasonDTO reasonDTO = ReasonDTO.of(
                ReasonConst.USE_CDKEY,
                "cdkey=", cdKey, "cid=%d(%s)".formatted(runResult.getIntValue("cid"), runResult.getString("comment"))
        );

        List<Item> itemList = bagService.newItems(rewards);

        BagChangeDTO4Item rewardItemArgs = BagChangeDTO4Item.builder()
                .setItemList(itemList)
                .setBagFullSendMail(true)
                .setBagErrorNoticeClient(false)
                .setReasonDTO(reasonDTO)
                .build();

        bagService.gainItems(player, rewardItemArgs);

        ResUseCdKey resUseCdKey = new ResUseCdKey();
        resUseCdKey.setCdKey(cdKey);
        player.write(resUseCdKey);
    }

}
