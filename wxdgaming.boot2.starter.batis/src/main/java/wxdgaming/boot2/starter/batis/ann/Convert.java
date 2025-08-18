package wxdgaming.boot2.starter.batis.ann;

import wxdgaming.boot2.starter.batis.convert.Converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;


/**
 * 转换器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 13:45
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, ElementType.FIELD})
public @interface Convert {

    Class<? extends Converter> value();

}
