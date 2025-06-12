package wxdgaming.boot2.core.util;


import ch.qos.logback.core.LogbackUtil;
import wxdgaming.boot2.core.function.Consumer2;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全局处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-11 16:52
 **/
public class GlobalUtil {

    /** 当前服务器的debug状态 */
    public static final AtomicBoolean DEBUG = new AtomicBoolean();
    public static final AtomicBoolean Exiting = new AtomicBoolean();

    public static Consumer2<Object, Throwable> exceptionCall = null;

    public static void exception(Object msg, Throwable throwable) {
        LogbackUtil.logger(3).error("{}", msg, throwable);
        if (exceptionCall != null) {
            exceptionCall.accept(msg, throwable);
        }
    }

}
