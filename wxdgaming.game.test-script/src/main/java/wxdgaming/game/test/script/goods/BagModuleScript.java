package wxdgaming.game.test.script.goods;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.goods.gain.GainScript;

import java.util.Iterator;
import java.util.List;

/**
 * 背包逻辑脚本
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:39
 **/
@Slf4j
@Singleton
public class BagModuleScript extends HoldRunApplication implements InitPrint {

    private final DataCenterService dataCenterService;
    Table<Integer, Integer, GainScript> gainScriptTable = new Table<>();

    @Inject
    public BagModuleScript(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
        {
            Table<Integer, Integer, GainScript> gainScriptTable = new Table<>();
            runApplication.classWithSuper(GainScript.class)
                    .forEach(gainScript -> {
                        GainScript old = gainScriptTable.put(gainScript.type(), gainScript.subType(), gainScript);
                        AssertUtil.assertTrue(old == null, "重复注册类型：" + gainScript.type() + " 子类型：" + gainScript.subType());
                    });
            this.gainScriptTable = gainScriptTable;
        }
    }

    public GainScript getGainScript(int type, int subtype) {
        GainScript gainScript = gainScriptTable.get(type, subtype);
        if (gainScript == null) {
            gainScript = gainScriptTable.get(type, 0);
        }
        if (gainScript == null) {
            gainScript = gainScriptTable.get(0, 0);
        }
        return gainScript;
    }

    @Init
    @Override public void init(RunApplication runApplication) {
        super.init(runApplication);
    }

    public ItemBag itemBag(BagPack bagPack, int bagType) {
        return bagPack.getItems().computeIfAbsent(bagType, k -> new ItemBag(100/*TODO 默认初始化格子数100个*/));
    }

    /** 获取背包指定道具数量 */
    public long itemCount(Player player, BagPack bagPack, int bagType, int itemCfgId) {
        ItemBag itemBag = itemBag(bagPack, bagType);
        int type = 0;
        int subtype = 0;
        return getGainScript(type, subtype).gainCount(player, itemBag, itemCfgId);
    }

    /** 默认是往背包添加 */
    public void gainItems(Player player, List<Item> items) {
        BagPack bagPack = dataCenterService.bagPack(player.getUid());
        gainItems(player, bagPack, 1, items);
    }

    public void gainItems(Player player, BagPack bagPack, int bagType, List<Item> items) {
        ItemBag itemBag = itemBag(bagPack, bagType);
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item newItem = iterator.next();

            if (itemBag.isFull()) break; /* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */

            int type = 0;
            int subtype = 0;
            GainScript gainScript = getGainScript(type, subtype);

            long oldCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
            long change = newItem.getCount();

            boolean gain = gainScript.gain(player, itemBag, newItem);
            if (gain) {
                long newCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
                log.info("{} 获得道具：{} {} + {} = {}", player, newItem.getCfgId(), change, oldCount, newCount);
                iterator.remove();
            } else {
                /* TODO 背包已满 */
                break;
            }
        }
        /* TODO 发送邮件 */
    }


}
