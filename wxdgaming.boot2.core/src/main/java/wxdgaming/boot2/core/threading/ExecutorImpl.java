package wxdgaming.boot2.core.threading;


import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.chatset.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 异步化处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-04 19:23
 **/
public class ExecutorImpl {

    public static void action(AtomicBoolean vt,
                              AtomicReference<String> threadName,
                              AtomicReference<String> queueName,
                              Method method) {
        ExecutorWith ann = AnnUtil.ann(method, ExecutorWith.class);
        if (ann != null) {
            vt.set(ann.useVirtualThread());
            if (StringUtils.isNotBlank(ann.threadName())) {
                threadName.set(ann.threadName());
            }
            if (StringUtils.isNotBlank(ann.queueName())) {
                queueName.set(ann.queueName());
            }
        }
    }

}
