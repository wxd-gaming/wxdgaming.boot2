package wxdgaming.boot2.core;

import lombok.Getter;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;

/**
 * 持有RunApplication
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:38
 **/
@Getter
public abstract class HoldRunApplication {

    protected RunApplication runApplication;

    @Init
    @Order(1)
    public void initHold(RunApplication runApplication) {
        this.runApplication = runApplication;
    }

}
