package code;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.function.Function4;
import wxdgaming.boot2.core.util.LambdaFactory;

/**
 * lambda
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-01 13:56
 */
@Slf4j
public class LambdaFactoryTest {

    /** 自定义的函数式接口，用于lambda调用 */
    public interface Operator {
        /**
         * 入参应和被lambda调用的方法一致，在本例中是Operation中的operate方法
         *
         * @return 返回值应和被lambda调用的方法一致，在本例中是Operation中的operate方法
         */
        int apply(int a, int b, int c);
    }

    /** 被lambda调用的类和方法 */
    public static class Operation {
        public int operate(int a, int b, int c) {return a + b - c;}

        public int add(int a, int b, int c) {return a + b + c;}
    }

    public static void main(String[] args) {
        try {
            //通过全类名，获取类的实例
            Class<?> clazz = Operation.class;
            //获取到类的对象
            Operation object = (Operation) clazz.getDeclaredConstructor().newInstance();

            Function4<Operator, Integer, Integer, Integer, Integer> apply = Operator::apply;

            /*通过lambda 对象 创建一个代理实例，比反射效果好*/
            LambdaFactory.findDelegate(object, apply, (lambdaMapping) -> {
                System.out.println(lambdaMapping.getMethod().toString() + " - " + ((Operator) lambdaMapping.getMapping()).apply(1, 2, 5));
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
