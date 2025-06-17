package wxdgaming.boot2.core.reflect;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.*;
import wxdgaming.boot2.core.executor.ThreadContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
    private final List<Content<Object>> beanList;
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<Content<Object>>> annotationCacheMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<MethodContent>> annotationMethodContentCacheMap = new ConcurrentHashMap<>();
    /** 继承某个类接口或者实现某个接口 */
    private final ConcurrentHashMap<Class<?>, List<Content<Object>>> superCacheMap = new ConcurrentHashMap<>();

    public GuiceReflectContext(RunApplication runApplication, Collection<Object> beanList) {
        this.runApplication = runApplication;
        this.beanList = beanList.stream()
                .sorted(ReflectContext.ComparatorBeanBySort)
                .map(Content::new)
                .toList();
    }

    /** 所有的类 */
    public Stream<Content<Object>> stream() {
        return beanList.stream();
    }

    /** 继承某个类接口或者实现某个接口 */
    Collection<Content<Object>> classWithSuperStream(Class<?> cls) {
        return superCacheMap.computeIfAbsent(cls, k ->
                beanList.stream()
                        .filter(content -> content.withSuper(cls))
                        .toList()
        );
    }

    /** 实现了某个注解的类 */
    Collection<Content<Object>> classWithAnnotatedStream(Class<? extends Annotation> annotation) {
        return annotationCacheMap.computeIfAbsent(annotation, k ->
                beanList.stream()
                        .filter(content -> content.withAnnotated(annotation))
                        .toList()
        );
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuperStream(cls).stream().map(content -> cls.cast(content.instance));
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotatedStream(annotation).stream().map(Content::getInstance);
    }

    /** 父类或者接口 */
    @SuppressWarnings("unchecked")
    public <U> Stream<Content<U>> withSuper(Class<U> cls) {
        return classWithSuperStream(cls).stream().map(content -> (Content<U>) content);
    }

    /** 所有添加了这个注解的类 */
    public Collection<Content<Object>> withAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotatedStream(annotation);
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<MethodContent> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return annotationMethodContentCacheMap.computeIfAbsent(annotation, k -> stream()
                .flatMap(content -> content.methodsWithAnnotated(annotation))
                .sorted(MethodContent::compareTo)
                .toList());
    }

    /** 执行循环过程中某一个函数执行失败中断执行 */
    public void executeMethodWithAnnotated(Class<? extends Annotation> annotation, Object... args) {
        Collection<MethodContent> methodContentStream = withMethodAnnotated(annotation);
        methodContentStream.forEach(methodContent -> methodContent.invoke(args));
    }

    /** 执行循环过程中某一个函数执行失败会继续执行其它函数 */
    public void executeMethodWithAnnotatedException(Class<? extends Annotation> annotation, Object... args) {
        Collection<MethodContent> methodContentStream = withMethodAnnotated(annotation);
        methodContentStream.forEach(methodContent -> {
            try {
                methodContent.invoke(args);
            } catch (Exception e) {
                log.error("执行方法异常：{}-{}", methodContent.getMethod(), methodContent.getMethod().getName(), e);
            }
        });
    }

    public Object[] injectorParameters(Object bean, Method method, Object... args) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        HolderArgument holderArgument = new HolderArgument(args);
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
            {
                HoldParam param = parameter.getAnnotation(HoldParam.class);
                if (param != null) {
                    params[i] = holderArgument.next();
                    continue;
                }
            }
            try {
                params[i] = runApplication.getInstance(parameterType);
            } catch (Exception e) {
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier == null) {
                    params[i] = holderArgument.next();
                    continue;
                } else if (qualifier.required()) {
                    throw new RuntimeException("bean:" + parameterType.getName() + " is not bind");
                }
            }
        }
        return params;
    }

    static class HolderArgument {

        private final Object[] arguments;
        private int argumentIndex = 0;

        public HolderArgument(Object[] arguments) {
            this.arguments = arguments;
        }

        @SuppressWarnings("unchecked")
        public <R> R next() {
            return (R) arguments[argumentIndex++];
        }

    }

    @Getter
    public class Content<T> {

        private final T instance;
        /** 所有的字段 */
        private final Collection<Field> fields;
        /** 所有的方法 */
        private final Collection<MethodContent> methods;

        Content(T instance) {
            this.instance = instance;
            this.fields = Collections.unmodifiableCollection(FieldUtil.getFields(false, instance.getClass()).values());
            this.methods = MethodUtil.readAllMethod(false, instance.getClass()).values().stream().map(m -> new MethodContent(instance, m)).toList();
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(instance.getClass(), annotation) != null;
        }

        /** 是否添加了注解 */
        public boolean withSuper(Class<?> cls) {
            return cls.isAssignableFrom(instance.getClass());
        }

        /** 所有添加了这个注解的方法 */
        public Stream<MethodContent> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methods.stream().filter(mc -> AnnUtil.ann(mc.getMethod(), annotation) != null);
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
            return "Content{instance=%s}".formatted(instance);
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

        public Object invoke(Object... args) {
            try {
                if (log.isTraceEnabled())
                    log.trace("{}.{}", ins.getClass().getSimpleName(), this.method.getName());
                Object[] objects = GuiceReflectContext.this.injectorParameters(ins, method, args);
                return method.invoke(ins, objects);
            } catch (Throwable throwable) {
                if (throwable instanceof InvocationTargetException) {
                    throwable = throwable.getCause();
                }
                throw Throw.of(throwable);
            }
        }

        @Override public int compareTo(MethodContent o) {

            int o1Sort = AnnUtil.annOpt(method, Order.class)
                    .or(() -> AnnUtil.annOpt(ins.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            int o2Sort = AnnUtil.annOpt(o.method, Order.class)
                    .or(() -> AnnUtil.annOpt(o.ins.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return method.getName().compareTo(o.method.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }

        @Override public String toString() {
            return "MethodContent{ins=%s, method=%s}".formatted(ins, method);
        }
    }

}
