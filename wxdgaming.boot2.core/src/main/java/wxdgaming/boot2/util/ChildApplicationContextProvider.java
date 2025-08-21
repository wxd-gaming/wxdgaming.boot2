package wxdgaming.boot2.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import wxdgaming.boot2.core.ApplicationContextProvider;

/**
 * 运行类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 16:55
 **/
public final class ChildApplicationContextProvider extends ApplicationContextProvider {

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
    }
}
