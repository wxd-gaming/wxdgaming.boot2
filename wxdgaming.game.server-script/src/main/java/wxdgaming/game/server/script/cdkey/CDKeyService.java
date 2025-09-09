package wxdgaming.game.server.script.cdkey;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.server.bean.goods.BagChangeDTO4Item;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.message.cdkey.ResUseCdKey;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.inner.InnerService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.cdkey.bean.CDKeyReward;
import wxdgaming.game.server.script.tips.TipsService;

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
    final InnerService innerService;


    public CDKeyService(ConnectLoginProperties connectLoginProperties, GameServerProperties gameServerProperties,
                        DataCenterService dataCenterService,
                        TipsService tipsService, BagService bagService,
                        InnerService innerService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.bagService = bagService;
        this.connectLoginProperties = connectLoginProperties;
        this.gameServerProperties = gameServerProperties;
        this.innerService = innerService;
    }

    public void use(Player player, String cdKey) {

        String url = connectLoginProperties.getUrl();
        url = url + "/inner/cdkey/use";

        JSONObject params = new JSONObject();
        params.put("key", cdKey);
        params.put("sid", gameServerProperties.getSid());
        params.put("account", player.getAccount());
        params.put("rid", player.getUid());

        innerService.sign(params);

        HttpResponse execute = HttpRequestPost.ofJson(url, params).execute();
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
