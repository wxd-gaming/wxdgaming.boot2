package wxdgaming.boot2.core.reflect;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Qualifier;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.threading.ThreadContext;
import wxdgaming.boot2.core.util.AnnUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-16 10:11
 **/
@Slf4j
@Getter
public class GuiceReflectContext {

    private final RunApplication runApplication;
    /** 所有的类 */
    private final List<Content<Object>> classList;
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<Content<Object>>> annotationCacheMap = new ConcurrentHashMap<>();
    /** 继承某个类接口或者实现某个接口 */
    private final ConcurrentHashMap<Class<?>, List<Content<Object>>> superCacheMap = new ConcurrentHashMap<>();

    public GuiceReflectContext(RunApplication runApplication, Collection<Object> classList) {
        this.runApplication = runApplication;
        this.classList = classList.stream().sorted(ReflectContext.ComparatorBeanBySort)
                .map(Content::of)
                .toList();
    }

    /** 所有的类 */
    public Stream<Content<Object>> stream() {
        return classList.stream();
    }

    /** 继承某个类接口或者实现某个接口 */
    Stream<Content<Object>> classWithSuperStream(Class<?> cls) {
        return superCacheMap.computeIfAbsent(cls, k ->
                        classList.stream()
                                .filter(content -> content.withSuper(cls))
                                .toList()
                )
                .stream();
    }

    /** 实现了某个注解的类 */
    Stream<Content<Object>> classWithAnnotatedStream(Class<? extends Annotation> annotation) {
        return annotationCacheMap.computeIfAbsent(annotation, k ->
                        classList.stream()
                                .filter(content -> content.withAnnotated(annotation))
                                .toList()
                )
                .stream();
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls, Predicate<U> predicate) {
        Stream<U> tmp = classWithSuperStream(cls).map(content -> cls.cast(content.instance));
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Object> tmp = classWithAnnotatedStream(annotation).map(Content::getInstance);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls) {
        return withSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls, Predicate<U> predicate) {
        Stream<Content<U>> tmp = classWithSuperStream(cls).map(content -> (Content<U>) content);
        if (predicate != null) tmp = tmp.filter(content -> predicate.test(content.getInstance()));
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation) {
        return withAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Content<Object>> tmp = classWithAnnotatedStream(annotation);
        if (predicate != null) tmp = tmp.filter(content -> predicate.test(content.instance));
        return tmp;
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Stream<MethodContent> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return withMethodAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<MethodContent> withMethodAnnotated(Class<? extends Annotation> annotation, Predicate<MethodContent> predicate) {
        Stream<MethodContent> methodStream = stream()
                .flatMap(content ->
                        content.methodsWithAnnotated(annotation)
                                .map(m -> new MethodContent(content.instance, m))
                )
                .sorted(MethodContent::compareTo);

        if (predicate != null) {
            methodStream = methodStream.filter(predicate);
        }
        return methodStream;
    }

    public void executeMethodWithAnnotated(Class<? extends Annotation> annotation) {
        Stream<MethodContent> methodContentStream = withMethodAnnotated(annotation);
        List<MethodContent> list = methodContentStream.toList();
        list.forEach(MethodContent::invoke);
    }

    public Object[] injectorParameters(Object bean, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (GuiceReflectContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(this);
                continue;
            } else if (Injector.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication.getInjector());
                continue;
            } else if (RunApplication.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication);
                continue;
            }
            /*实现注入*/
            Value value = parameter.getAnnotation(Value.class);
            if (value != null) {
                params[i] = BootConfig.getIns().value(value, parameterizedType);
                continue;
            }

            {
                ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                if (threadParam != null) {
                    params[i] = ThreadContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            try {
                params[i] = runApplication.getInstance(parameterType);
            } catch (Exception e) {
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier != null && qualifier.required()) {
                    throw new RuntimeException("bean:" + parameterType.getName() + " is not bind");
                }
            }
        }
        return params;
    }

    @Getter
    public static class Content<T> {

        private final T instance;
        /** 所有的字段 */
        private final Collection<Field> fields;
        /** 所有的方法 */
        private final Collection<Method> methods;

        public static <U> Content<U> of(U cls) {
            return new Content<>(cls);
        }

        Content(T instance) {
            this.instance = instance;
            this.fields = Collections.unmodifiableCollection(FieldUtil.getFields(false, instance.getClass()).values());
            this.methods = Collections.unmodifiableCollection(MethodUtil.readAllMethod(false, instance.getClass()).values());
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(instance.getClass(), annotation) != null;
        }

        /** 是否添加了注解 */
        public boolean withSuper(Class<?> cls) {
            return cls.isAssignableFrom(instance.getClass());
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
        public Stream<Field> fieldStream() {
            return getFields().stream();
        }

        /** 所有添加了这个注解的字段 */
        public Stream<Field> fieldWithAnnotated(Class<? extends Annotation> annotation) {
            return fieldStream().filter(f -> AnnUtil.ann(f, annotation) != null);
        }

        @Override public String toString() {
            return "Content{" + "instance=" + instance + '}';
        }
    }

    @Getter
    public class MethodContent implements Comparable<MethodContent> {

        private final Object ins;
        private final Method method;

        public MethodContent(Object ins, Method method) {
            this.ins = ins;
            this.method = method;
        }

        public Object invoke() {
            try {
                log.debug("{}.{}", ins.getClass().getSimpleName(), this.method.getName());
                Object[] objects = GuiceReflectContext.this.injectorParameters(ins, method);
                return method.invoke(ins, objects);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override public int compareTo(MethodContent o) {

            int o1Sort = AnnUtil.annOpt(method, Sort.class)
                    .or(() -> AnnUtil.annOpt(ins.getClass(), Sort.class))
                    .map(Sort::value)
                    .orElse(999999);

            int o2Sort = AnnUtil.annOpt(o.method, Sort.class)
                    .or(() -> AnnUtil.annOpt(o.ins.getClass(), Sort.class))
                    .map(Sort::value)
                    .orElse(999999);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return method.getName().compareTo(o.method.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }

        @Override public String toString() {
            return "MethodContent{" +
                   "ins=" + ins +
                   ", method=" + method +
                   '}';
        }
    }

}
