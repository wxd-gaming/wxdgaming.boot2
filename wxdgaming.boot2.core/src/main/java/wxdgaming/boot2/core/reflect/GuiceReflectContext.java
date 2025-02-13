package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import wxdgaming.boot2.core.AnnUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-16 10:11
 **/
@Getter
public class GuiceReflectContext {

    /** 所有的类 */
    private final List<Object> classList;

    public GuiceReflectContext(Collection<Class<?>> classList) {
        this.classList = new ArrayList<>(classList);
    }

    /** 所有的类 */
    public Stream<Object> classStream() {
        return classList.stream();
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls, Predicate<U> predicate) {
        Stream<U> tmp = classStream().filter(o -> cls.isAssignableFrom(o.getClass())).map(cls::cast);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Object> tmp = classStream().filter(o -> AnnUtil.ann(o.getClass(), annotation) != null);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    public Stream<Content<?>> stream() {
        return classList.stream().map(Content::new);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls) {
        return withSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls, Predicate<U> predicate) {
        return classWithSuper(cls, predicate).map(Content::new);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<?>> withAnnotated(Class<? extends Annotation> annotation) {
        return withAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<?>> withAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        return classWithAnnotated(annotation, predicate).map(Content::new);
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Stream<Method> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return withMethodAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Method> withMethodAnnotated(Class<? extends Annotation> annotation, Predicate<Method> predicate) {
        Stream<Method> methodStream = stream().flatMap(info -> info.methodsWithAnnotated(annotation));
        if (predicate != null) {
            methodStream = methodStream.filter(predicate);
        }
        return methodStream;
    }

    @Getter
    public static class Content<T> {

        private final T cls;

        public static <U> Content<U> of(U cls) {
            return new Content<>(cls);
        }

        Content(T cls) {
            this.cls = cls;
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(cls.getClass(), annotation) != null;
        }

        /** 所有的方法 */
        public Collection<Method> getMethods() {
            return MethodUtil.readAllMethod(cls.getClass()).values();
        }

        /** 所有的方法 */
        public Stream<Method> methodStream() {
            return getMethods().stream();
        }

        /** 所有添加了这个注解的方法 */
        public Stream<Method> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methodStream().filter(m -> AnnUtil.ann(m, annotation) != null);
        }

        /** 所有的字段 */
        public Collection<Field> getFields() {
            return FieldUtil.getFields(false, cls.getClass()).values();
        }

        /** 所有的字段 */
        public Stream<Field> fieldStream() {
            return getFields().stream();
        }

        /** 所有添加了这个注解的字段 */
        public Stream<Field> fieldWithAnnotated(Class<? extends Annotation> annotation) {
            return fieldStream().filter(f -> AnnUtil.ann(f, annotation) != null);
        }

    }

}
