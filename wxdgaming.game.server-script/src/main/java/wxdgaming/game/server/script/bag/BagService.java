package wxdgaming.game.server.script.bag;

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
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResBagInfo;
import wxdgaming.game.server.bean.bag.BagChangesEvent;
import wxdgaming.game.server.bean.bag.BagPack;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.cost.CostScript;
import wxdgaming.game.server.script.bag.gain.GainScript;
import wxdgaming.game.server.script.bag.use.UseItemAction;
import wxdgaming.game.server.script.mail.MailService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.*;

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
    Table<Integer, Integer, CostScript> costImplTable = new Table<>();
    Table<Integer, Integer, UseItemAction> useItemImplTable = new Table<>();

    final DataCenterService dataCenterService;
    final TipsService tipsService;
    final DataRepository dataRepository;
    final MailService mailService;

    @Inject
    public BagService(DataCenterService dataCenterService, TipsService tipsService, DataRepository dataRepository, MailService mailService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.dataRepository = dataRepository;
        this.mailService = mailService;
    }

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
            Table<Integer, Integer, CostScript> tmpCostScriptTable = new Table<>();
            runApplication.classWithSuper(CostScript.class)
                    .forEach(costScript -> {
                        ItemTypeConst itemTypeConst = costScript.type();
                        CostScript old = tmpCostScriptTable.put(itemTypeConst.getType(), itemTypeConst.getSubType(), costScript);
                        AssertUtil.assertTrue(old == null, "重复注册类型：" + itemTypeConst);
                    });
            this.costImplTable = tmpCostScriptTable;
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
        onLoginBefore(player);
    }

    /** 登录的时候检查背包问题 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        BagPack bagPack = player.getBagPack();
        bagPack.getBagMap().computeIfAbsent(BagType.Bag, k -> new ItemBag(100).resetGrid());/*背包*/
        bagPack.getBagMap().computeIfAbsent(BagType.Store, k -> new ItemBag(100).resetGrid());/*仓库*/
    }

    /** 登录的时候推送背包 */
    @OnLogin
    public void onLogin(Player player) {
        /*推送数据的*/
        sendBagInfo(player, BagType.Bag);
    }

    /** 登录的时候推送背包 */
    public void sendBagInfo(Player player, BagType bagType) {
        ItemBag itemBag = player.getBagPack().getBagMap().get(bagType);
        ResBagInfo resBagInfo = new ResBagInfo();
        resBagInfo.setBagType(bagType);
        HashMap<Integer, Long> currencyMap = itemBag.getCurrencyMap();
        resBagInfo.getCurrencyMap().putAll(currencyMap);
        Item[] items = itemBag.getItemGrids();
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            if (item == null) continue;
            resBagInfo.getItems().put(i, item.toItemBean());
        }
        player.write(resBagInfo);
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

    public CostScript getCostScript(int type, int subtype) {
        CostScript costScript = costImplTable.get(type, subtype);
        if (costScript == null) {
            costScript = costImplTable.get(type, 0);
        }
        if (costScript == null) {
            costScript = costImplTable.get(0, 0);
        }
        return costScript;
    }

    public List<Item> newItems(ItemCfg itemCfg) {
        return newItems(List.of(itemCfg));
    }

    public List<Item> newItems(List<ItemCfg> itemCfgList) {
        List<Item> items = new ArrayList<>();
        for (ItemCfg itemCfg : itemCfgList) {
            QItem qItem = dataRepository.dataTable(QItemTable.class, itemCfg.getCfgId());
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = getGainScript(type, subtype);
            gainScript.newItem(items, itemCfg);
        }
        return items;
    }

    /** 获取背包指定道具数量 */
    public long itemCount(Player player, BagType bagType, int itemCfgId) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = bagPack.itemBag(bagType);
        QItem qItem = dataRepository.dataTable(QItemTable.class, itemCfgId);
        int type = qItem.getItemType();
        int subtype = qItem.getItemSubType();
        return getGainScript(type, subtype).getCount(player, itemBag, itemCfgId);
    }

    /** 默认是往背包添加 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
    public boolean gainItems4Cfg(Player player, List<ItemCfg> itemCfgs, ReasonArgs reasonArgs) {
        List<Item> items = newItems(itemCfgs);
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = bagPack.itemBag(BagType.Bag);
        if (itemBag.freeGrid() < items.size()) {
            if (log.isDebugEnabled()) {
                log.debug("添加道具背包空间不足 {} 背包空间：{} 需要格子数：{}, {}", player, itemBag.freeGrid(), items.size(), reasonArgs);
            }
            return false;/* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
        }
        gainItems(player, BagType.Bag, itemBag, items, reasonArgs);
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
        ItemBag itemBag = bagPack.itemBag(BagType.Bag);
        gainItems(player, BagType.Bag, itemBag, items, reasonArgs);
    }

    /** 最终入口 */
    private void gainItems(Player player, BagType bagType, ItemBag itemBag, List<Item> items, ReasonArgs reasonArgs) {
        Iterator<Item> iterator = items.iterator();
        BagChangesEvent bagChangesEvent = new BagChangesEvent(player, bagType, itemBag, reasonArgs);
        while (iterator.hasNext()) {
            Item newItem = iterator.next();

            if (itemBag.checkFull()) break; /* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */

            QItem qItem = dataRepository.dataTable(QItemTable.class, newItem.getCfgId());
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();

            GainScript gainScript = getGainScript(type, subtype);

            long oldCount = gainScript.getCount(player, itemBag, newItem.getCfgId());
            long change = newItem.getCount();
            AssertUtil.assertTrue(change >= 0, "添加数量不能是负数");

            boolean gain = gainScript.gain(bagChangesEvent, newItem);

            if (gain || newItem.getCount() != change) {
                long newCount = gainScript.getCount(player, itemBag, newItem.getCfgId());
                if (!gain) {
                    /*表示叠加之后，剩余的东西没办法入包，需要发邮件*/
                    change -= newItem.getCount();
                }
                log.info("获得道具：{}, {}, {} {}+{}={}, {}", player, bagType, qItem.getToName(), oldCount, change, newCount, reasonArgs);
            }

            if (gain) {
                iterator.remove();
            } else {
                /* TODO 背包已满 */
                break;
            }
        }

        player.write(bagChangesEvent.toResUpdateBagInfo());

        /* TODO 发送邮件 */
        if (!items.isEmpty()) {
            mailService.sendMail(player, "系统", "背包已满", "背包已满", List.of(), items, reasonArgs.toString());
        }

    }

    public boolean checkCostNotice(Player player, List<ItemCfg> costList) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = bagPack.getBagMap().get(BagType.Bag);
        return checkCostNotice(player, BagType.Bag, itemBag, costList);
    }

    /** 在执行 cost 之前切记用这个 */
    public boolean checkCostNotice(Player player, BagType bagType, ItemBag itemBag, List<ItemCfg> costList) {
        Integer cfgId = checkCost(player, bagType, itemBag, costList);
        if (cfgId == null) {
            return true;
        }
        QItem qItem = dataRepository.dataTable(QItemTable.class, cfgId);
        tipsService.tips(player, qItem.getToName() + "道具不足");
        return false;
    }

    /** 在执行 cost 之前切记用这个 */
    public Integer checkCost(Player player, List<ItemCfg> costList) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = bagPack.getBagMap().get(BagType.Bag);
        return checkCost(player, BagType.Bag, itemBag, costList);
    }

    /** 在执行 cost 之前切记用这个 */
    public Integer checkCost(Player player, BagType bagType, ItemBag itemBag, List<ItemCfg> costList) {
        HashMap<Integer, Long> costMap = new HashMap<>();
        for (ItemCfg itemCfg : costList) {
            int cfgId = itemCfg.getCfgId();
            long change = itemCfg.getNum();
            costMap.merge(cfgId, change, Math::addExact);
        }
        for (Map.Entry<Integer, Long> entry : costMap.entrySet()) {
            int cfgId = entry.getKey();
            long change = entry.getValue();
            AssertUtil.assertTrue(change >= 0, "扣除数量不能是负数");
            QItem qItem = dataRepository.dataTable(QItemTable.class, cfgId);
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = getGainScript(type, subtype);
            long oldCount = gainScript.getCount(player, itemBag, cfgId);
            if (oldCount < change) {
                return cfgId;
            }
        }
        return null;
    }

    /** 调用之前请使用 {@link BagService#checkCost(Player, List)} 函数 是否够消耗 */
    public void cost(Player player, List<ItemCfg> costList, ReasonArgs reasonArgs) {
        BagPack bagPack = player.getBagPack();
        ItemBag itemBag = bagPack.getBagMap().get(BagType.Bag);
        cost(player, BagType.Bag, itemBag, costList, reasonArgs);
    }

    /** 调用之前请使用 {@link BagService#checkCost(Player, BagType, ItemBag, List)} 函数 是否够消耗 */
    public void cost(Player player, BagType bagType, ItemBag itemBag, List<ItemCfg> costList, ReasonArgs reasonArgs) {

        BagChangesEvent bagChangesEvent = new BagChangesEvent(player, bagType, itemBag, reasonArgs);

        for (ItemCfg itemCfg : costList) {
            int cfgId = itemCfg.getCfgId();
            long change = itemCfg.getNum();
            AssertUtil.assertTrue(change >= 0, "扣除数量不能是负数");

            QItem qItem = dataRepository.dataTable(QItemTable.class, cfgId);
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = getGainScript(type, subtype);

            long oldCount = gainScript.getCount(player, itemBag, cfgId);
            CostScript costScript = getCostScript(type, subtype);

            costScript.cost(player, bagChangesEvent, qItem, change, reasonArgs);
            long newCount = gainScript.getCount(player, itemBag, cfgId);

            log.info("消耗道具：{}, {}, {} {}-{}={}, {}", player, bagType, qItem.getToName(), oldCount, change, newCount, reasonArgs);
        }
        player.write(bagChangesEvent.toResUpdateBagInfo());

    }

}
