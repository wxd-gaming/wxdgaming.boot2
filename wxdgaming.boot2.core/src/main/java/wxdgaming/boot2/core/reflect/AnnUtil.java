package wxdgaming.boot2.core.reflect;


import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.Const;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

/**
 * 注解帮助类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-06-11 11:17
 **/
public class AnnUtil {

    public static int orderValue(Class<?> source) {
        Order order = source.getAnnotation(Order.class);
        return order != null ? order.value() : Const.SORT_DEFAULT;
    }

    public static boolean hasAnn(Class<?> source, Class<? extends Annotation> annClass) {
        return ann(source, annClass) != null;
    }

    public static <A extends Annotation> Optional<A> annOpt(Class<?> source, Class<A> annClass) {
        return Optional.ofNullable(ann(source, annClass, false));
    }


    /** 获取注解 */
    public static <A extends Annotation> A ann(Class<?> source, Class<A> annClass) {
        if (source == null) return null;
        return ann(source, annClass, false);
    }

    public static <A extends Annotation> A ann(Class<?> source, Class<A> annClass, boolean findSuperClass) {
        if (source == null) return null;
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return annotationsByType[0];
        if (findSuperClass) {
            Class<?> superclass = source.getSuperclass();
            if (superclass != null && !Object.class.equals(superclass)) {
                return ann(superclass, annClass, findSuperClass);
            }
        }
        return null;
    }

    public static int orderValue(Field source) {
        Order order = source.getAnnotation(Order.class);
        return order != null ? order.value() : Const.SORT_DEFAULT;
    }

    public static int orderValue(Field source, IntSupplier defaultOrder) {
        Order order = source.getAnnotation(Order.class);
        return order != null ? order.value() : defaultOrder.getAsInt();
    }

    public static boolean hasAnn(Field source, Class<? extends Annotation> annClass) {
        return ann(source, annClass) != null;
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解字段
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> Optional<A> annOpt(Field source, Class<A> annClass) {
        return Optional.ofNullable(ann(source, annClass));
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解字段
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> A ann(Field source, Class<A> annClass) {
        if (source == null) return null;
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return annotationsByType[0];
        return null;
    }

    public static int orderValue(Method source) {
        Order order = source.getAnnotation(Order.class);
        return order != null ? order.value() : Const.SORT_DEFAULT;
    }

    public static int orderValue(Method source, IntSupplier defaultOrder) {
        Order order = source.getAnnotation(Order.class);
        return order != null ? order.value() : defaultOrder.getAsInt();
    }

    public static boolean hasAnn(Method source, Class<? extends Annotation> annClass) {
        return ann(source, annClass) != null;
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解的函数
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> Optional<A> annOpt(Method source, Class<A> annClass) {
        return Optional.ofNullable(ann(source, annClass));
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解的函数
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> A ann(Method source, Class<A> annClass) {
        if (source == null) return null;
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return annotationsByType[0];
        return null;
    }

    /**
     * 获取注解
     *
     * @param source   需要获取注解的类
     * @param annClass 注解
     * @param <A>      注解
     * @return 返回注册的所有注解
     */
    public static <A extends Annotation> Stream<A> annStream(Class<?> source, Class<A> annClass) {
        if (source == null) return Stream.of();
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return Stream.of(annotationsByType);
        return Stream.of();
    }

    /**
     * 获取注解
     *
     * @param source         需要获取注解的类
     * @param annClass       注解
     * @param findSuperClass 注解
     * @param <A>            注解
     * @return 返回注册的所有注解
     */
    public static <A extends Annotation> Stream<A> annStream(Class<?> source, Class<A> annClass, boolean findSuperClass) {
        if (source == null) return Stream.of();
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return Stream.of(annotationsByType);
        if (findSuperClass) {
            Class<?> superclass = source.getSuperclass();
            if (superclass != null && !Object.class.equals(superclass)) {
                return annStream(superclass, annClass, findSuperClass);
            }
        }
        return Stream.of();
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解字段
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> Stream<A> annStream(Field source, Class<A> annClass) {
        if (source == null) return Stream.of();
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return Stream.of(annotationsByType);
        return Stream.of();
    }

    /**
     * 获取字段上的所有注解
     *
     * @param source   获取注解的函数
     * @param annClass 需要回去的注解
     * @param <A>
     * @return 返回符合条件的注解
     */
    public static <A extends Annotation> Stream<A> annStream(Method source, Class<A> annClass) {
        if (source == null) return Stream.of();
        A[] annotationsByType = source.getAnnotationsByType(annClass);
        if (annotationsByType.length > 0) return Stream.of(annotationsByType);
        return Stream.of();
    }

}
