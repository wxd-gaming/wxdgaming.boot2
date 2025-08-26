package wxdgaming.game.server.script.bag;

import lombok.Getter;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.bean.goods.ItemTypeConst;

/**
 * 背包相关的脚本持有
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-25 10:52
 **/
@Getter
public class BagScriptProvider<T extends IBagScript> {

    private final Class<T> cls;
    private Table<Integer, Integer, T> implTable = new Table<>();

    public BagScriptProvider(Class<T> cls) {
        this.cls = cls;
    }

    public void init(ApplicationContextProvider runApplication) {
        Table<Integer, Integer, T> scriptTable = new Table<>();
        runApplication.classWithSuperStream(cls).forEach(script -> {
            ItemTypeConst itemTypeConst = script.type();
            T old = scriptTable.put(itemTypeConst.getType(), itemTypeConst.getSubType(), script);
            AssertUtil.assertTrue(old == null, "重复注册类型：" + itemTypeConst);
        });
        this.implTable = scriptTable;
    }

    public T getScript(int type, int subtype) {
        T script = implTable.get(type, subtype);
        if (script == null) {
            script = implTable.get(type, 0);
        }
        if (script == null) {
            script = implTable.get(0, 0);
        }
        return script;
    }

}
