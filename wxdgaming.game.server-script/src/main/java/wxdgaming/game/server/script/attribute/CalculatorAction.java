package wxdgaming.game.server.script.attribute;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.server.bean.MapNpc;
import wxdgaming.game.server.bean.MapObject;
import wxdgaming.game.bean.attr.AttrInfo;

/**
 * 计算器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:29
 **/
@Slf4j
public abstract class CalculatorAction {

    @Inject protected DataRepository dataRepository;

    public abstract MapObject.MapObjectType mapObjectType();

    public abstract CalculatorType calculatorType();

    public abstract AttrInfo calculate(MapNpc mapNpc, Object... args);

    public abstract AttrInfo calculatePro(MapNpc mapNpc, Object... args);

}
