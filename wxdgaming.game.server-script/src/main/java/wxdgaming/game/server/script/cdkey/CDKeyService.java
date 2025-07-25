package wxdgaming.game.server.script.cdkey;

import com.alibaba.fastjson.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpContent;
import wxdgaming.boot2.starter.net.httpclient5.PostRequest;
import wxdgaming.game.bean.goods.BagChangeArgs4Item;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.cdkey.ResUseCdKey;
import wxdgaming.game.server.bean.BackendConfig;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.cdkey.bean.CDKeyReward;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * cdkey
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-29 11:14
 **/
@Slf4j
@Getter
@Setter
@Singleton
public class CDKeyService {

    private final DataCenterService dataCenterService;
    private final TipsService tipsService;
    private final BagService bagService;
    private BackendConfig backendConfig;

    public void init(@Value(path = "backends") BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
    }

    @Inject
    public CDKeyService(DataCenterService dataCenterService, TipsService tipsService, BagService bagService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.bagService = bagService;
    }

    public void use(Player player, String cdKey) {

        String url = backendConfig.getUrl();
        url = url + "/cdkey/use";

        HashMap<String, Object> params = new HashMap<>();
        params.put("gameId", backendConfig.getGameId());
        params.put("appToken", backendConfig.getAppToken());
        params.put("key", cdKey);
        params.put("account", player.getAccount());
        params.put("rid", player.getUid());

        HttpContent execute = PostRequest.ofJson(url, FastJsonUtil.toJSONString(params)).execute();
        RunResult runResult = execute.bodyRunResult();

        if (runResult.isFail()) {
            tipsService.tips(player, runResult.msg());
            return;
        }

        ArrayList<CDKeyReward> rewardCfgList = runResult.getObject("rewards", new TypeReference<ArrayList<CDKeyReward>>() {});

        List<ItemCfg> rewards = new ArrayList<>();
        for (CDKeyReward reward : rewardCfgList) {
            ItemCfg itemCfg = ItemCfg.builder()
                    .cfgId(reward.getItemId())
                    .num(reward.getCount())
                    .bind(reward.getBind() == 1)
                    .expirationTime(reward.getExpireTime())
                    .build();
            rewards.add(itemCfg);
        }

        ReasonArgs reasonArgs = ReasonArgs.of(
                Reason.USE_CDKEY,
                "cdkey=", cdKey, "cid=%d(%s)".formatted(runResult.getIntValue("cid"), runResult.getString("comment"))
        );

        List<Item> itemList = bagService.newItems(rewards);

        BagChangeArgs4Item rewardItemArgs = BagChangeArgs4Item.builder()
                .setItemList(itemList)
                .setBagFullSendMail(true)
                .setBagErrorNoticeClient(false)
                .setReasonArgs(reasonArgs)
                .build();

        bagService.gainItems(player, rewardItemArgs);

        ResUseCdKey resUseCdKey = new ResUseCdKey();
        resUseCdKey.setCdKey(cdKey);
        player.write(resUseCdKey);
    }

}
