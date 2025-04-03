package wxdgaming.boot2.core.threading;

import lombok.Getter;

/**
 * 辅助器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-03 14:13
 **/
public class ExecutorUtilImpl {

    @Getter static ExecutorUtil instance;

    public static ExecutorUtil impl() {
        instance = new ExecutorUtil();
        instance.init();
        return instance;
    }

}
