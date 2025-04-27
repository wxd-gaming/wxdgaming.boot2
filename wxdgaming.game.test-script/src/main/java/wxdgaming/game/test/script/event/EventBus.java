package wxdgaming.game.test.script.event;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.HoldRunApplication;

import java.lang.annotation.Annotation;

/**
 * 事件处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 10:59
 **/
@Singleton
public class EventBus extends HoldRunApplication {

    public void post(Class<? extends Annotation> annotation, Object... args) {
        getRunApplication().getGuiceReflectContext().executeMethodWithAnnotatedException(annotation, args);
    }

}
