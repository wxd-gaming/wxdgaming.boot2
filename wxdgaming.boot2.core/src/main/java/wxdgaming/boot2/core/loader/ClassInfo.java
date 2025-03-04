package wxdgaming.boot2.core.loader;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-08-06 17:10
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ClassInfo implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private Class<?> loadClass;
    private byte[] loadClassBytes;

    public String getLoadClassSimpleName() {
        return loadClass.getSimpleName();
    }

    public String getLoadClassClassName() {
        return loadClass.getName();
    }


    /**
     * 判断是否是继承关系
     *
     * @param clazz
     * @return
     */
    public boolean isAssignableFrom(Class<?> clazz) {
        return clazz.isAssignableFrom(this.loadClass);
    }

    /**
     * 获取加载类的实例对象
     *
     * @param <R>
     * @return
     * @throws Exception
     */
    public <R> R newInstance() throws Exception {
        return (R) this.loadClass.newInstance();
    }
}
