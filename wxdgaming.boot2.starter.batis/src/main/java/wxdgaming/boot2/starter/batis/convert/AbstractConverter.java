package wxdgaming.boot2.starter.batis.convert;

import lombok.Getter;
import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.lang.reflect.Type;

/**
 * 转换器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 09:20
 **/
@Getter
public abstract class AbstractConverter<X, Y> {

    private final Class<X> clazzX;
    private final Class<Y> clazzY;

    public AbstractConverter() {
        this.clazzX = ReflectProvider.getTClass(this.getClass(), 0);
        this.clazzY = ReflectProvider.getTClass(this.getClass(), 1);
    }

    /** 转换成数据库 */
    public abstract Y toDb(X x);

    public abstract X fromDb(Type type, Y y);

}
