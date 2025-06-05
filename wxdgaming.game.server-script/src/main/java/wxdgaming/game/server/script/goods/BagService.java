package wxdgaming.game.server.script.goods;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.goods.*;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.goods.gain.GainScript;
import wxdgaming.game.server.script.goods.use.UseItemAction;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.ArrayList;
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
public class BagService extends HoldRunApplication implements InitPrint {

    Table<Integer, Integer, GainScript> gainImplTable = new Table<>();
    Table<Integer, Integer, UseItemAction> useItemImplTable = new Table<>();

    @Inject private DataCenterService dataCenterService;
    @Inject private TipsService tipsService;
    @Inject private DataRepository dataRepository;


    @Init
    public void init(RunApplication runApplication) {
        {
            Table<Integer, Integer, GainScript> tmpGainScriptTable = new Table<>();
            runApplication.classWithSuper(GainScript.class)
                    .forEach(gainScript -> {
                        ItemTypeConst itemTypeConst = gainScript.type();
                        GainScript old = tmpGainScriptTable.put(itemTypeConst.getType(), itemTypeConst.getSubType(), gainScript);
                        AssertUtil.assertTrue(old == null, "重复注册类型：" + itemTypeConst);
                    });
            this.gainImplTable = tmpGainScriptTable;
        }

        {
            Table<Integer, Integer, UseItemAction> tmpUseImplTable = new Table<>();
            runApplication.classWithSuper(UseItemAction.class)
                    .forEach(gainScript -> {
                        ItemTypeConst itemTypeConst = gainScript.type();
                        UseItemAction old = tmpUseImplTable.put(itemTypeConst.getType(), itemTypeConst.getSubType(), gainScript);
                        AssertUtil.assertTrue(old == null, "重复注册类型：" + itemTypeConst);
                    });
            this.useItemImplTable = tmpUseImplTable;
        }
    }

    /** 创建角色之后创建背包 */
    @OnCreateRole
    @Order(1)
    public void onCreateRoleInitBag(Player player) {
        BagPack bagPack = player.getBagPack();
        bagPack.getItems().put(1, new ItemBag(100));/*背包*/
        bagPack.getItems().put(2, new ItemBag(100));/*仓库*/
    }

    /** 登录的时候检查背包问题 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
    }

    /** 登录的时候推送背包 */
    @OnLogin
    public void onLogin(Player player) {
        /*推送数据的*/
    }

    public GainScript getGainScript(int type, int subtype) {
        GainScript gainScript = gainImplTable.get(type, subtype);
        if (gainScript == null) {
            gainScript = gainImplTable.get(type, 0);
        }
        if (gainScript == null) {
            gainScript = gainImplTable.get(0, 0);
        }
        return gainScript;
    }

    public List<Item> newItems(ItemCfg itemCfg) {
        return newItems(List.of(itemCfg));
    }

    public List<Item> newItems(List<ItemCfg> itemCfgs) {
        List<Item> items = new ArrayList<>();
        for (ItemCfg itemCfg : itemCfgs) {
            QItem qItem = dataRepository.dataTable(QItemTable.class, itemCfg.getCfgId());
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = getGainScript(type, subtype);
            gainScript.newItem(items, itemCfg);
        }
        return items;
    }

    public ItemBag itemBag(BagPack bagPack, int bagType) {
        return bagPack.getItems().computeIfAbsent(bagType, k -> new ItemBag(100/*TODO 默认初始化格子数100个*/));
    }

    /** 获取背包指定道具数量 */
    public long itemCount(Player player, int bagType, int itemCfgId) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = itemBag(bagPack, bagType);
        QItem qItem = dataRepository.dataTable(QItemTable.class, itemCfgId);
        int type = qItem.getItemType();
        int subtype = qItem.getItemSubType();
        return getGainScript(type, subtype).gainCount(player, itemBag, itemCfgId);
    }

    /** 默认是往背包添加 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
    public boolean gainItems4Cfg(Player player, List<ItemCfg> itemCfgs, ReasonArgs reasonArgs) {
        List<Item> items = newItems(itemCfgs);
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = itemBag(bagPack, 1);
        if (itemBag.freeGrid() < items.size()) {
            if (log.isDebugEnabled()) {
                log.debug("添加道具背包空间不足 {} 背包空间：{} 需要格子数：{}, {}", player, itemBag.freeGrid(), items.size(), reasonArgs);
            }
            return false;/* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
        }
        gainItems(player, itemBag, items, reasonArgs);
        return true;
    }

    /** 默认是往背包添加 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
    public boolean gainItems4CfgNotice(Player player, List<ItemCfg> itemCfgs, ReasonArgs reasonArgs) {
        boolean gained = gainItems4Cfg(player, itemCfgs, reasonArgs);
        if (!gained) {
            tipsService.tips(player, "背包已满");
        }
        return gained;
    }

    /** 默认是往背包添加 */
    public void gainItems(Player player, List<Item> items, ReasonArgs reasonArgs) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = itemBag(bagPack, 1);
        gainItems(player, itemBag, items, reasonArgs);
    }

    private void gainItems(Player player, ItemBag itemBag, List<Item> items, ReasonArgs reasonArgs) {
        Iterator<Item> iterator = items.iterator();

        while (iterator.hasNext()) {
            Item newItem = iterator.next();

            if (itemBag.checkFull()) break; /* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */

            QItem qItem = dataRepository.dataTable(QItemTable.class, newItem.getCfgId());
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();

            GainScript gainScript = getGainScript(type, subtype);

            long oldCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
            long change = newItem.getCount();

            boolean gain = gainScript.gain(player, itemBag, newItem, reasonArgs);

            if (gain || newItem.getCount() != change) {
                long newCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
                log.info("获得道具：{}, {}({}-{}}) {} + {} = {}, 变更原因：{}", player, newItem.getUid(), qItem.getId(), qItem.getName(), oldCount, change, newCount, reasonArgs);
            }

            if (gain) {
                iterator.remove();
            } else {
                /* TODO 背包已满 */
                break;
            }
        }
        /* TODO 发送邮件 */
    }


}
