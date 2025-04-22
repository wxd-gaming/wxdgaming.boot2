package wxdgaming.boot2.core;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import wxdgaming.boot2.core.ann.PostConstruct;
import wxdgaming.boot2.core.reflect.MethodUtil;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 构造监听
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:57
 **/
public class PostConstructListener implements TypeListener {

    @Override
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        Class<? super T> clazz = typeLiteral.getRawType();
        Set<Method> methods = MethodUtil.allMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                typeEncounter.register(new PostConstructCallback<>(method));
            }
        }
    }

    private record PostConstructCallback<T>(Method method) implements com.google.inject.MembersInjector<T> {

        private PostConstructCallback(Method method) {
            this.method = method;
            this.method.setAccessible(true);
        }

        @Override
        public void injectMembers(T instance) {
            try {
                method.invoke(instance); // 调用初始化方法
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke @PostConstruct method", e);
            }
        }
    }
}
