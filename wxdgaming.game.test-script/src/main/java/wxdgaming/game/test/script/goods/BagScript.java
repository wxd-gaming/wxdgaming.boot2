package wxdgaming.game.test.script.goods;

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
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.event.OnCreateRole;
import wxdgaming.game.test.script.event.OnLogin;
import wxdgaming.game.test.script.event.OnLoginBefore;
import wxdgaming.game.test.script.goods.gain.GainScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 背包逻辑脚本
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:39
 **/
@Slf4j
@Singleton
public class BagScript extends HoldRunApplication implements InitPrint {

    private final DataCenterService dataCenterService;
    Table<Integer, Integer, GainScript> gainScriptTable = new Table<>();

    @Inject
    public BagScript(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    @Init
    @Override public void init(RunApplication runApplication) {
        super.init(runApplication);
        {
            Table<Integer, Integer, GainScript> tmpGainScriptTable = new Table<>();
            runApplication.classWithSuper(GainScript.class)
                    .forEach(gainScript -> {
                        GainScript old = tmpGainScriptTable.put(gainScript.type(), gainScript.subType(), gainScript);
                        AssertUtil.assertTrue(old == null, "重复注册类型：" + gainScript.type() + " 子类型：" + gainScript.subType());
                    });
            this.gainScriptTable = tmpGainScriptTable;
        }
        //
        // Thread.ofPlatform().start(() -> {
        //     LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        //     Player player = new Player();
        //     player.setUid(System.nanoTime());
        //     player.setName("无心道");
        //     player.setAccount(StringUtils.randomString(8));
        //
        //     dataCenterService.getPgsqlService().getCacheService().cache(Player.class).put(player.getUid(), player);
        //
        //     ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        //     List<ItemCfg> rewards = new ArrayList<>();
        //     rewards.add(builder.cfgId(10001).count(100).build());
        //     rewards.add(builder.cfgId(30001).count(100).build());
        //     gainItems4Cfg(player, rewards, "业务号:", System.nanoTime(), "完成任务:", 1001);
        //
        // });
    }

    /** 创建角色之后赠送初始化道具 */
    @OnCreateRole
    @Order(1)
    public void onCreateRoleInitBag(Player player) {
        BagPack bagPack = new BagPack();
        bagPack.setUid(player.getUid());
        bagPack.getItems().put(1, new ItemBag(100));/*背包*/
        bagPack.getItems().put(2, new ItemBag(100));/*仓库*/
        dataCenterService.getPgsqlService().getCacheService().cache(BagPack.class).put(player.getUid(), bagPack);
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
        GainScript gainScript = gainScriptTable.get(type, subtype);
        if (gainScript == null) {
            gainScript = gainScriptTable.get(type, 0);
        }
        if (gainScript == null) {
            gainScript = gainScriptTable.get(0, 0);
        }
        return gainScript;
    }

    public List<Item> newItems(ItemCfg itemCfg) {
        return newItems(List.of(itemCfg));
    }

    public List<Item> newItems(List<ItemCfg> itemCfgs) {
        List<Item> items = new ArrayList<>();
        for (ItemCfg itemCfg : itemCfgs) {
            int type = 0;
            int subtype = 0;
            GainScript gainScript = getGainScript(type, subtype);
            gainScript.newItem(items, itemCfg);
        }
        return items;
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

    /** 默认是往背包添加 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
    public boolean gainItems4Cfg(Player player, long serialNumber, List<ItemCfg> itemCfgs, Object... args) {
        List<Item> items = newItems(itemCfgs);
        BagPack bagPack = dataCenterService.bagPack(player.getUid());
        ItemBag itemBag = itemBag(bagPack, 1);
        if (itemBag.freeGrid() < items.size())
            return false;/* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */
        gainItems(player, itemBag, serialNumber, items, args);
        return true;
    }

    /** 默认是往背包添加 */
    public void gainItems(Player player, long serialNumber, List<Item> items, Object... args) {
        BagPack bagPack = dataCenterService.bagPack(player.getUid());
        ItemBag itemBag = itemBag(bagPack, 1);
        gainItems(player, itemBag, serialNumber, items, args);
    }

    private void gainItems(Player player, ItemBag itemBag, long serialNumber, List<Item> items, Object... args) {
        Iterator<Item> iterator = items.iterator();

        String collect = Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(" "));

        while (iterator.hasNext()) {
            Item newItem = iterator.next();

            if (itemBag.isFull()) break; /* TODO 背包已满 不要去关心能不能叠加 只要没有空格子就不操作 */

            int type = 0;
            int subtype = 0;
            GainScript gainScript = getGainScript(type, subtype);

            long oldCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
            long change = newItem.getCount();

            boolean gain = gainScript.gain(player, itemBag, serialNumber, newItem);

            if (gain || newItem.getCount() != change) {
                long newCount = gainScript.gainCount(player, itemBag, newItem.getCfgId());
                log.info("{} 业务流水号：{} 获得道具：{}({}-xxx) {} + {} = {}, 变更原因：{}", player, serialNumber, newItem.getUid(), newItem.getCfgId(), oldCount, change, newCount, collect);
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
