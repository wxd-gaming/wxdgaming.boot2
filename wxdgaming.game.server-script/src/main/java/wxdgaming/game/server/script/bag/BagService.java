package wxdgaming.game.server.script.bag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.goods.*;
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResBagInfo;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.bag.BagPack;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.cost.CostScript;
import wxdgaming.game.server.script.bag.gain.GainScript;
import wxdgaming.game.server.script.bag.log.ItemSlog;
import wxdgaming.game.server.script.bag.use.UseItemAction;
import wxdgaming.game.server.script.mail.MailService;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.game.basic.slog.SlogService;

import java.util.*;

/**
 * 背包逻辑脚本
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 09:39
 **/
@Slf4j
@Service
public class BagService extends HoldApplicationContext implements InitPrint {

    final BagScriptProvider<GainScript> gainScriptProvider = new BagScriptProvider<>(GainScript.class);
    final BagScriptProvider<CostScript> costScriptProvider = new BagScriptProvider<>(CostScript.class);
    final BagScriptProvider<UseItemAction> useItemScriptProvider = new BagScriptProvider<>(UseItemAction.class);

    final DataCenterService dataCenterService;
    final TipsService tipsService;
    final DataRepository dataRepository;
    final MailService mailService;
    final SlogService slogService;

    public BagService(DataCenterService dataCenterService,
                      TipsService tipsService,
                      DataRepository dataRepository,
                      MailService mailService,
                      SlogService slogService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.dataRepository = dataRepository;
        this.mailService = mailService;
        this.slogService = slogService;
    }

    @Init
    public void init() {
        this.gainScriptProvider.init(getApplicationContextProvider());
        this.costScriptProvider.init(getApplicationContextProvider());
        this.useItemScriptProvider.init(getApplicationContextProvider());
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

    public List<Item> newItems(ItemCfg itemCfg) {
        return newItems(List.of(itemCfg));
    }

    public List<Item> newItems(List<ItemCfg> itemCfgList) {
        List<Item> items = new ArrayList<>();
        for (ItemCfg itemCfg : itemCfgList) {
            QItem qItem = dataRepository.dataTable(QItemTable.class, itemCfg.getCfgId());
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = gainScriptProvider.getScript(type, subtype);
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
        return gainScriptProvider.getScript(type, subtype).getCount(player, itemBag, itemCfgId);
    }

    /** 默认是往背包添加 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
    public boolean gainItemCfg(Player player, BagChangeDTO4ItemCfg rewardArgs4ItemCfg) {
        List<Item> items = newItems(rewardArgs4ItemCfg.getItemCfgList());
        return _gainItems(player, rewardArgs4ItemCfg, items);
    }

    /** 默认是往背包添加 */
    public boolean gainItems(Player player, BagChangeDTO4Item changeArgs4Item) {
        return _gainItems(player, changeArgs4Item, changeArgs4Item.getItemList());
    }

    private boolean _gainItems(Player player, BagChangeDTO bagChangeDTO, List<Item> items) {
        BagType bagType = bagChangeDTO.getBagType();
        ItemBag itemBag = player.getBagPack().itemBag(bagType);
        if (!bagChangeDTO.isBagFullSendMail()) {
            if (itemBag.freeGrid() < items.size()) {
                if (log.isInfoEnabled()) {
                    log.info(
                            "添加道具背包空间不足 {} 背包空间：{} 需要格子数：{}, {}",
                            player, itemBag.freeGrid(), items.size(), bagChangeDTO.getReasonDTO()
                    );
                }
                if (bagChangeDTO.isBagErrorNoticeClient()) {
                    tipsService.tips(player, "背包已满");
                }
                return false;/* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
            }
        }
        Iterator<Item> iterator = items.iterator();
        BagChangesContext bagChangesContext = new BagChangesContext(player, bagType, itemBag, bagChangeDTO.getReasonDTO());
        while (iterator.hasNext()) {
            Item newItem = iterator.next();

            if (itemBag.checkFull()) break; /* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */

            int cfgId = newItem.getCfgId();
            QItem qItem = dataRepository.dataTable(QItemTable.class, cfgId);
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();

            GainScript gainScript = gainScriptProvider.getScript(type, subtype);

            long oldCount = gainScript.getCount(player, itemBag, cfgId);
            long change = newItem.getCount();
            AssertUtil.assertTrue(change >= 0, "添加数量不能是负数");

            boolean gain = gainScript.gain(bagChangesContext, newItem);

            if (gain || newItem.getCount() != change) {
                long newCount = gainScript.getCount(player, itemBag, cfgId);
                if (!gain) {
                    /*表示叠加之后，剩余的东西没办法入包，需要发邮件*/
                    change -= newItem.getCount();
                }
                log.info("获得道具：{}, {}, {} {}+{}={}, {}", player, bagType, qItem.getToName(), oldCount, change, newCount, bagChangeDTO.getReasonDTO());

                ItemSlog itemLog = new ItemSlog(player, bagType.name(),
                        "获得",
                        cfgId, qItem.getName(),
                        oldCount, change, newCount,
                        bagChangeDTO.getReasonDTO().getReason().name(),
                        bagChangeDTO.getReasonDTO().getReasonText()
                );
                slogService.pushLog(itemLog);
            }

            if (gain) {
                iterator.remove();
            } else {
                /* TODO 背包已满 */
                break;
            }
        }

        player.write(bagChangesContext.toResUpdateBagInfo());

        /* TODO 发送邮件 */
        if (!items.isEmpty()) {
            mailService.sendMail(player, "系统", "背包已满", "背包已满", List.of(), items, bagChangeDTO.toString());
        }
        return true;
    }

    /** 在执行 cost 之前切记用这个 */
    public boolean checkCost(Player player, BagChangeDTO4ItemCfg bagChangeArgs) {
        BagType bagType = bagChangeArgs.getBagType();
        ItemBag itemBag = player.getBagPack().itemBag(bagType);
        HashMap<Integer, Long> costMap = new HashMap<>();
        for (ItemCfg itemCfg : bagChangeArgs.getItemCfgList()) {
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
            GainScript gainScript = gainScriptProvider.getScript(type, subtype);
            long oldCount = gainScript.getCount(player, itemBag, cfgId);
            if (oldCount < change) {
                if (bagChangeArgs.isBagErrorNoticeClient()) {
                    tipsService.tips(player, qItem.getToName() + "道具不足", bagChangeArgs.getReasonDTO().getReason());
                }
                return false;
            }
        }
        return true;
    }

    /** 调用之前请使用 {@link BagService#checkCost(Player, BagChangeDTO4ItemCfg)} 函数 是否够消耗 */
    public void cost(Player player, BagChangeDTO4ItemCfg bagChangeDTO) {
        BagType bagType = bagChangeDTO.getBagType();
        ItemBag itemBag = player.getBagPack().itemBag(bagType);
        BagChangesContext bagChangesContext = new BagChangesContext(player, bagType, itemBag, bagChangeDTO.getReasonDTO());

        for (ItemCfg itemCfg : bagChangeDTO.getItemCfgList()) {
            int cfgId = itemCfg.getCfgId();
            long change = itemCfg.getNum();

            AssertUtil.assertTrue(change >= 0, "扣除数量不能是负数");

            QItem qItem = dataRepository.dataTable(QItemTable.class, cfgId);
            int type = qItem.getItemType();
            int subtype = qItem.getItemSubType();
            GainScript gainScript = gainScriptProvider.getScript(type, subtype);

            long oldCount = gainScript.getCount(player, itemBag, cfgId);
            CostScript costScript = costScriptProvider.getScript(type, subtype);

            costScript.cost(player, bagChangesContext, qItem, change);
            long newCount = gainScript.getCount(player, itemBag, cfgId);

            log.info("消耗道具：{}, {}, {} {}-{}={}, {}", player, bagType, qItem.getToName(), oldCount, change, newCount, bagChangeDTO.getReasonDTO());

            ItemSlog itemLog = new ItemSlog(player, bagType.name(),
                    "消耗",
                    cfgId, qItem.getName(),
                    oldCount, change, newCount,
                    bagChangeDTO.getReasonDTO().getReason().name(),
                    bagChangeDTO.getReasonDTO().getReasonText()
            );
            slogService.pushLog(itemLog);

        }
        player.write(bagChangesContext.toResUpdateBagInfo());

    }

}
