package wxdgaming.boot2.core.ann;

import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.Event;

/**
 * 执行init操作
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-22 17:24
 **/
public record InitEvent(ApplicationContextProvider applicationContextProvider) implements Event {

}
