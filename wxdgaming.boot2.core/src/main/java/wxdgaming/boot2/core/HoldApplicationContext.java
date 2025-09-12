package wxdgaming.boot2.core;

import lombok.Getter;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.runtime.IgnoreRunTimeRecord;

/**
 * 持有RunApplication
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 17:38
 **/
@Getter
public abstract class HoldApplicationContext implements InitPrint {

    protected ApplicationContextProvider applicationContextProvider;

    @Init
    @Order(Integer.MIN_VALUE)
    @IgnoreRunTimeRecord
    public void ___initHold(ApplicationContextProvider runApplication) {
        this.applicationContextProvider = runApplication;
    }

}
