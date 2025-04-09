package code.reactor;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 函数参数泛型类型获取
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-09 17:17
 **/
public class ReactorTest {

    public void login(String command, Mono<String> stringMono, Mono<List<String>> stringMonoList, Map<String, Object> map) {

    }

    @Test
    public void t1() throws Exception {
        Method declaredMethod = Arrays.stream(this.getClass().getDeclaredMethods()).filter(method -> method.getName().equals("login")).findFirst().orElse(null);
        Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            Class<?> type = null;
            Class<?> firstT = null;
            if (genericParameterType instanceof Class<?> clazz) {
                type = clazz;
            } else if (genericParameterType instanceof ParameterizedType parameterizedType) {
                type = (Class<?>) parameterizedType.getRawType();
                Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                if (actualTypeArgument instanceof Class<?> clazz) {
                    firstT = clazz;
                } else if (actualTypeArgument instanceof ParameterizedType parameterizedType1) {
                    firstT = (Class<?>) parameterizedType1.getRawType();
                }
            }
            System.out.println(type + " - " + firstT);
        }
    }

}
