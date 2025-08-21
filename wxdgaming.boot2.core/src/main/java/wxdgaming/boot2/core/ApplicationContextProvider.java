package wxdgaming.boot2.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.FieldUtil;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.util.JvmUtil;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-10-16 10:11
 **/
@Slf4j
@Getter
public abstract class ApplicationContextProvider implements InitPrint, ApplicationContextAware {

    public static final Comparator<Object> OBJECT_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getClass().getAnnotation(org.springframework.core.annotation.Order.class)).map(org.springframework.core.annotation.Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getClass().getAnnotation(org.springframework.core.annotation.Order.class)).map(org.springframework.core.annotation.Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getClass().getName().compareTo(o2.getClass().getName());
    };

    private ApplicationContext applicationContext;
    /** 所有的类 */
    private List<Provider<Object>> beanList;
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<Provider<Object>>> annotationCacheMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<ProviderField>> annotationFieldContentCacheMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<ProviderMethod>> annotationMethodContentCacheMap = new ConcurrentHashMap<>();
    /** 继承某个类接口或者实现某个接口 */
    private final ConcurrentHashMap<Class<?>, List<Provider<Object>>> superCacheMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public synchronized List<Provider<Object>> getBeanList() {
        if (beanList == null) {
            beanList = buildBeans(applicationContext).map(Provider::new).toList();
        }
        return beanList;
    }

    /** 所有的bean */
    private Stream<Object> buildBeans(ApplicationContext __applicationContext) {
        Stream<Object> parent = Stream.empty();
        if (__applicationContext.getParent() != null) {
            parent = buildBeans(__applicationContext.getParent());
        }
        String[] beanDefinitionNames = __applicationContext.getBeanDefinitionNames();
        Stream<Object> objectStream = Arrays.stream(beanDefinitionNames).map(__applicationContext::getBean);
        return Stream.concat(parent, objectStream).sorted(OBJECT_COMPARATOR);
    }

    /** 所有的类 */
    public Stream<Provider<Object>> stream() {
        return getBeanList().stream();
    }

    /** 继承某个类接口或者实现某个接口 */
    Collection<Provider<Object>> classWithSuperStream(Class<?> cls) {
        return superCacheMap.computeIfAbsent(cls, k ->
                getBeanList().stream()
                        .filter(provider -> provider.withSuper(cls))
                        .toList()
        );
    }

    /** 实现了某个注解的类 */
    Collection<Provider<Object>> classWithAnnotatedStream(Class<? extends Annotation> annotation) {
        return annotationCacheMap.computeIfAbsent(annotation, k ->
                getBeanList().stream()
                        .filter(provider -> provider.withAnnotated(annotation))
                        .toList()
        );
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuperStream(cls).stream().map(provider -> cls.cast(provider.bean));
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotatedStream(annotation).stream().map(Provider::getBean);
    }

    /** 父类或者接口 */
    @SuppressWarnings("unchecked")
    public <U> Stream<Provider<U>> withSuper(Class<U> cls) {
        return classWithSuperStream(cls).stream().map(provider -> (Provider<U>) provider);
    }

    /** 所有添加了这个注解的类 */
    public Collection<Provider<Object>> withAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotatedStream(annotation);
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<ProviderField> withFieldAnnotated(Class<? extends Annotation> annotation) {
        return annotationFieldContentCacheMap.computeIfAbsent(
                annotation,
                k -> stream()
                        .flatMap(provider -> provider.fieldWithAnnotated(annotation))
                        .sorted(ProviderField::compareTo)
                        .toList()
        );
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<ProviderMethod> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return annotationMethodContentCacheMap.computeIfAbsent(
                annotation,
                k -> stream()
                        .flatMap(provider -> provider.methodsWithAnnotated(annotation))
                        .sorted(ProviderMethod::compareTo)
                        .toList()
        );
    }

    /** 执行循环过程中某一个函数执行失败中断执行 */
    public void executeMethodWithAnnotated(Class<? extends Annotation> annotation, Object... args) {
        Collection<ProviderMethod> providerMethodStream = withMethodAnnotated(annotation);
        providerMethodStream.forEach(providerMethod -> providerMethod.invoke(args));
    }

    /** 执行循环过程中某一个函数执行失败会继续执行其它函数 */
    public void executeMethodWithAnnotatedException(Class<? extends Annotation> annotation, Object... args) {
        Collection<ProviderMethod> providerMethodStream = withMethodAnnotated(annotation);
        providerMethodStream.forEach(providerMethod -> {
            try {
                providerMethod.invoke(args);
            } catch (Exception e) {
                log.error("执行方法异常：{}-{}", providerMethod.getMethod(), providerMethod.getMethod().getName(), e);
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
            if (ApplicationContextProvider.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(this);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(applicationContext);
                continue;
            }
            {
                ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                if (threadParam != null) {
                    params[i] = ThreadContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            /*实现注入*/
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                String name = qualifier.value();
                if (StringUtils.isBlank(name))
                    params[i] = applicationContext.getBean(parameterType);
                else
                    params[i] = applicationContext.getBean(name);
                continue;
            }
            params[i] = holderArgument.next();
        }
        return params;
    }

    public <R> R getBean(Class<R> cls) {
        return applicationContext.getBean(cls);
    }

    public <R> R getBean(String names) {
        return (R) applicationContext.getBean(names);
    }

    public ApplicationContextProvider executeMethodWithAnnotatedInit() {
        executeMethodWithAnnotated(Init.class);
        return this;
    }

    public ApplicationContextProvider executeMethodWithAnnotatedStart() {
        executeMethodWithAnnotated(Start.class);
        return this;
    }

    public ApplicationContextProvider executeMethodWithAnnotatedStop() {
        ExecutorFactory executorFactory = getBean(ExecutorFactory.class);
        executeMethodWithAnnotatedException(Stop.class);
        classWithSuper(AutoCloseable.class).forEach(bean -> {
            if (bean instanceof Closeable) {
                try {
                    ((Closeable) bean).close();
                    log.debug("关闭bean：{}", bean);
                } catch (Exception e) {
                    log.error("关闭bean异常...", e);
                }
            }
        });
        executorFactory.getEXECUTOR_MONITOR().getExit().set(true);
        JvmUtil.halt(0);
        return this;
    }

    public ApplicationContextProvider addShutdownHook() {
        JvmUtil.addShutdownHook(this::executeMethodWithAnnotatedStop);
        return this;
    }

    /** 传递参数提取 */
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
    public class Provider<T> {

        private final T bean;
        /** 所有的字段 */
        private final Collection<ProviderField> fields;
        /** 所有的方法 */
        private final Collection<ProviderMethod> methods;

        Provider(T bean) {
            this.bean = bean;
            this.fields = FieldUtil.getFields(false, bean.getClass()).values().stream().map(f -> new ProviderField(bean, f)).toList();
            this.methods = MethodUtil.readAllMethod(false, bean.getClass()).values().stream().map(m -> new ProviderMethod(bean, m)).toList();
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(bean.getClass(), annotation) != null;
        }

        /** 是否添加了注解 */
        public boolean withSuper(Class<?> cls) {
            return cls.isAssignableFrom(bean.getClass());
        }

        /** 所有添加了这个注解的方法 */
        public Stream<ProviderMethod> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methods.stream().filter(provider -> AnnUtil.ann(provider.getMethod(), annotation) != null);
        }

        /** 所有的字段 */
        public Stream<ProviderField> fieldStream() {
            return getFields().stream();
        }

        /** 所有添加了这个注解的字段 */
        public Stream<ProviderField> fieldWithAnnotated(Class<? extends Annotation> annotation) {
            return fieldStream().filter(provider -> AnnUtil.ann(provider.getField(), annotation) != null);
        }

        @Override public String toString() {
            return "Provider{instance=%s}".formatted(bean);
        }
    }

    @Getter
    public class ProviderField implements Comparable<ProviderField> {

        private final Object bean;
        private final Field field;

        public ProviderField(Object bean, Field field) {
            this.bean = bean;
            this.field = field;
        }

        public void invoke(Object arg) {
            try {
                if (log.isTraceEnabled())
                    log.trace("{}.{}", bean.getClass().getSimpleName(), this.field.getName());
                field.set(bean, arg);
            } catch (Throwable throwable) {
                if (throwable instanceof InvocationTargetException) {
                    throwable = throwable.getCause();
                }
                throw Throw.of(throwable);
            }
        }

        @Override public int compareTo(ProviderField o) {
            int o1Sort = AnnUtil.annOpt(field, Order.class)
                    .or(() -> AnnUtil.annOpt(bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            int o2Sort = AnnUtil.annOpt(o.getField(), Order.class)
                    .or(() -> AnnUtil.annOpt(o.bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return field.getName().compareTo(o.getField().getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }

        @Override public String toString() {
            return "FieldProvider{ins=%s, method=%s}".formatted(bean, field);
        }
    }

    @Getter
    public class ProviderMethod implements Comparable<ProviderMethod> {

        private final Object bean;
        private final Method method;

        public ProviderMethod(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        public Object invoke(Object... args) {
            try {
                if (log.isTraceEnabled())
                    log.trace("{}.{}", bean.getClass().getSimpleName(), this.method.getName());
                Object[] objects = ApplicationContextProvider.this.injectorParameters(bean, method, args);
                return method.invoke(bean, objects);
            } catch (Throwable throwable) {
                if (throwable instanceof InvocationTargetException) {
                    throwable = throwable.getCause();
                }
                throw Throw.of(bean.getClass().getName() + "#" + method.getName(), throwable);
            }
        }

        @Override public int compareTo(ProviderMethod o) {

            int o1Sort = AnnUtil.annOpt(method, Order.class)
                    .or(() -> AnnUtil.annOpt(bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            int o2Sort = AnnUtil.annOpt(o.method, Order.class)
                    .or(() -> AnnUtil.annOpt(o.bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(Const.SORT_DEFAULT);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return method.getName().compareTo(o.method.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }

        @Override public String toString() {
            return "MethodProvider{ins=%s, method=%s}".formatted(bean, method);
        }
    }

}
