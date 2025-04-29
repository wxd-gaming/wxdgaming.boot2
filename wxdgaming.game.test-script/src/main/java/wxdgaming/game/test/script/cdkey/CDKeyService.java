package wxdgaming.game.test.script.cdkey;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.CDKeyUtil;
import wxdgaming.game.test.bean.entity.UseCDKeyRecord;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.cdkey.bean.CDKeyEntity;
import wxdgaming.game.test.script.cdkey.message.ResUseCdKey;
import wxdgaming.game.test.script.goods.BagService;
import wxdgaming.game.test.script.tips.TipsService;

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

    private HashMap<Integer, CDKeyEntity> cdKeyMap = new HashMap<>();
    private final DataCenterService dataCenterService;
    private final TipsService tipsService;
    private final BagService bagService;

    @Inject
    public CDKeyService(DataCenterService dataCenterService, TipsService tipsService, BagService bagService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.bagService = bagService;
    }

    public void use(Player player, String cdKey) {
        int cdKeyId = CDKeyUtil.getCdKeyId(cdKey);
        CDKeyEntity cdKeyEntity = cdKeyMap.get(cdKeyId);
        if (cdKeyEntity == null) {
            tipsService.tips(player.getSocketSession(), "激活码异常");
            return;
        }

        Runnable sourceCall = null;

        if (cdKeyEntity.getUseType() == 1) {
            int useCount = player.getUseCDKeyMap().getOrDefault(cdKeyId, 0);
            if (useCount >= cdKeyEntity.getUseCount()) {
                tipsService.tips(player.getSocketSession(), "激活码使用次数已达上限");
                return;
            }
            sourceCall = () -> {
                /* 添加使用次数 */
                player.getUseCDKeyMap().put(cdKeyId, useCount + 1);
            };
        } else if (cdKeyEntity.getUseType() == 2) {
            UseCDKeyRecord useCDKeyRecord = dataCenterService.getPgsqlService().findByKey(UseCDKeyRecord.class, cdKey);
            if (useCDKeyRecord == null) {
                useCDKeyRecord = new UseCDKeyRecord();
                useCDKeyRecord.setCdkey(cdKey);
                useCDKeyRecord.setSid(player.getSid());
            }

            if (useCDKeyRecord.getUseCount() != cdKeyEntity.getUseCount()) {
                tipsService.tips(player.getSocketSession(), "激活码使用次数已达上限");
                return;
            }

            final UseCDKeyRecord useCDKeyRecordFinal = useCDKeyRecord;

            sourceCall = () -> {
                /* 添加使用次数 */
                useCDKeyRecordFinal.setUseCount(useCDKeyRecordFinal.getUseCount() + 1);
                useCDKeyRecordFinal.setLastUseTime(MyClock.millis());
                dataCenterService.getPgsqlService().save(useCDKeyRecordFinal);
            };

        } else if (cdKeyEntity.getUseType() == 3) {
            /*检查一下 cdkey 是否全局唯一， 比如 redis */
            sourceCall = () -> {

            };
        } else {
            throw new RuntimeException("激活码类型异常");
        }

        List<ItemCfg> rewards = new ArrayList<>();
        for (CDKeyEntity.CDKeyReward reward : cdKeyEntity.getRewards()) {
            ItemCfg itemCfg = ItemCfg.builder()
                    .cfgId(reward.getItemId())
                    .count(reward.getCount())
                    .bind(reward.getBind() == 1)
                    .expirationTime(reward.getExpireTime())
                    .build();
            rewards.add(itemCfg);
        }

        long nanoTime = System.nanoTime();

        if (!bagService.gainItems4Cfg(player, nanoTime, rewards, "use cdkey", cdKey, cdKeyId)) {
            return;
        }

        sourceCall.run();

        ResUseCdKey resUseCdKey = new ResUseCdKey();
        resUseCdKey.setCdKey(cdKey);
        player.write(resUseCdKey);
    }

}
