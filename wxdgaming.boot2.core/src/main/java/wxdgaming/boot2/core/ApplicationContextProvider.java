package wxdgaming.boot2.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.event.Event;
import wxdgaming.boot2.core.event.InitEvent;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.executor.ExecutorContext;
import wxdgaming.boot2.core.reflect.InstanceFieldProvider;
import wxdgaming.boot2.core.reflect.InstanceMethodProvider;
import wxdgaming.boot2.core.reflect.InstanceProvider;
import wxdgaming.boot2.core.util.AssertUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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

    private ApplicationContext applicationContext;
    /** 所有的类 */
    private List<ProviderBean> beanList;
    private final ConcurrentHashMap<String, List<InstanceMethodProvider>> eventMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<ProviderBean>> annotationCacheMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<InstanceFieldProvider>> annotationFieldContentCacheMap = new ConcurrentHashMap<>();
    /** 实现了某个注解的类 */
    private final ConcurrentHashMap<Class<? extends Annotation>, List<InstanceMethodProvider>> annotationMethodContentCacheMap = new ConcurrentHashMap<>();
    /** 继承某个类接口或者实现某个接口 */
    private final ConcurrentHashMap<Class<?>, List<ProviderBean>> superCacheMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        log.info("{} setApplicationContext", this.getClass().getSimpleName());
    }

    public <T> T registerInstance(T instance) {
        return SpringUtil.registerInstance((ConfigurableApplicationContext) this.getApplicationContext(), instance);
    }

    public synchronized List<ProviderBean> getBeanList() {
        if (beanList == null) {
            beanList = buildBeans(applicationContext).map(ProviderBean::new).sorted().toList();
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
        return Stream.concat(parent, objectStream).filter(obj -> !obj.getClass().getPackageName().startsWith("org.springframework"));
    }

    /** 所有的类 */
    public Stream<ProviderBean> stream() {
        return getBeanList().stream();
    }

    /** 继承某个类接口或者实现某个接口 */
    Collection<ProviderBean> classWithSuperCache(Class<?> cls) {
        return superCacheMap.computeIfAbsent(cls, k ->
                getBeanList().stream()
                        .filter(provider -> provider.withSuper(cls))
                        .toList()
        );
    }

    /** 实现了某个注解的类 */
    Collection<ProviderBean> classWithAnnotatedCache(Class<? extends Annotation> annotation) {
        return annotationCacheMap.computeIfAbsent(annotation, k ->
                getBeanList().stream()
                        .filter(provider -> provider.hasAnn(annotation))
                        .toList()
        );
    }

    /** 父类或者接口 */
    public <U> U instance(Class<U> cls) {
        return classWithSuperCache(cls).stream()
                .findFirst()
                .map(provider -> cls.cast(provider.getInstanceProvider().getInstance()))
                .orElse(null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuperStream(Class<U> cls) {
        return classWithSuperCache(cls).stream().map(provider -> cls.cast(provider.getInstanceProvider().getInstance()));
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<InstanceFieldProvider> withFieldAnnotatedCache(Class<? extends Annotation> annotation) {
        return annotationFieldContentCacheMap.computeIfAbsent(
                annotation,
                k -> stream()
                        .flatMap(provider -> provider.fieldWithAnnotated(annotation))
                        .sorted()
                        .toList()
        );
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<InstanceMethodProvider> withMethodAnnotatedCache(Class<? extends Annotation> annotation) {
        return annotationMethodContentCacheMap.computeIfAbsent(
                annotation,
                k -> stream()
                        .flatMap(provider -> provider.methodsWithAnnotated(annotation))
                        .sorted()
                        .toList()
        );
    }

    /** 所有的bean，函数的参数是指定类型 */
    public Stream<InstanceMethodProvider> withMethodParameters(Class<?>... args) {
        return stream()
                .flatMap(provider -> provider.methodsEqualsParameters(args))
                .sorted();
    }

    /** 所有的bean，函数的参数是指定类型继承关系 */
    public Stream<InstanceMethodProvider> withMethodAssignableFrom(Class<?>... args) {
        return stream()
                .flatMap(provider -> provider.methodsAssignableFrom(args))
                .sorted();
    }

    public <K, B> Map<K, B> toMap(Class<B> cls, Function<B, K> convertKey) {
        return toMap(cls, convertKey, bean -> bean);
    }

    /** 将指定类或者接口的转换成map对象 */
    public <B, K, V> Map<K, V> toMap(Class<B> cls, Function<B, K> convertKey, Function<B, V> convertValue) {
        return toMap4Collection(classWithSuperStream(cls).toList(), convertKey, convertValue);
    }

    /** 将添加了注解的类转换成map对象 */
    public <K, V> Map<K, V> toMapWithAnnotated(Class<? extends Annotation> cls, Function<Object, K> convertKey, Function<Object, V> convertValue) {
        return toMap4Collection(
                withFieldAnnotatedCache(cls),
                pb -> convertKey.apply(pb.getInstance()),
                pb -> convertValue.apply(pb.getInstance())
        );
    }

    /** 针对方法添加注解的实例类转换成map对象 */
    public <K, V> Map<K, V> toMapWithMethodAnnotated(Class<? extends Annotation> cls,
                                                     Function<InstanceMethodProvider, K> convertKey,
                                                     Function<InstanceMethodProvider, V> convertValue) {
        Collection<InstanceMethodProvider> providerMethods = withMethodAnnotatedCache(cls);
        return toMap4Collection(providerMethods, convertKey, convertValue);
    }

    public <B, K, V> Map<K, V> toMap4Collection(Collection<B> stream,
                                                Function<B, K> convertKey,
                                                Function<B, V> convertValue) {
        HashMap<K, V> tmp = new HashMap<>();
        for (B bean : stream) {
            K key = convertKey.apply(bean);
            V value = convertValue.apply(bean);
            V oldPut = tmp.put(key, value);
            AssertUtil.isTrue(oldPut == null, "重复类型：" + key);
            log.debug("register {}: {}", key, value);
        }
        return Collections.unmodifiableMap(tmp);
    }

    public synchronized List<InstanceMethodProvider> findEventMap(Class<? extends Event> eventClass) {
        return eventMap.computeIfAbsent(
                eventClass.getName(),
                l -> withMethodAssignableFrom(eventClass)
                        .filter(method -> method.hasAnn(EventListener.class))
                        .sorted()
                        .toList()
        );
    }

    /**
     * 抛出事件，但是如果执行遇到异常会中断
     * <p>如果事件执行需要先后顺序 {@link org.springframework.core.annotation.Order}
     */
    public ApplicationContextProvider postEvent(Event event) {
        List<InstanceMethodProvider> methods = findEventMap(event.getClass());
        methods.forEach(providerMethod -> {
            log.debug("post event {}, args: {}", providerMethod.toString(), event);
            providerMethod.invoke(event);
        });
        return this;
    }

    /**
     * 抛出事件，如果遇到异常会继续执行
     * <p>如果事件执行需要先后顺序 {@link org.springframework.core.annotation.Order}
     */
    public ApplicationContextProvider postEventIgnoreException(Event event) {
        List<InstanceMethodProvider> methods = findEventMap(event.getClass());
        for (InstanceMethodProvider providerMethod : methods) {
            try {
                providerMethod.invoke(event);
            } catch (Throwable throwable) {
                log.error("执行方法异常：{}-{}", providerMethod.getInstance().getClass(), providerMethod.getMethod().getName(), throwable);
            }
        }
        return this;
    }

    public ApplicationContextProvider postInitEvent() {
        postEvent(new InitEvent(this));
        return this;
    }

    public ApplicationContextProvider postStartEvent() {
        postEvent(new StartEvent());
        return this;
    }

    /** 执行循环过程中某一个函数执行失败中断执行 */
    public ApplicationContextProvider executeMethodWithAnnotated(Class<? extends Annotation> annotation, Object... args) {
        Collection<InstanceMethodProvider> providerMethodStream = withMethodAnnotatedCache(annotation);
        providerMethodStream.forEach(providerMethod -> {
            /* 会重新组织参数列表 */
            Object[] objects = this.injectorParameters(providerMethod.getInstance(), providerMethod.getMethod(), args);
            providerMethod.invoke(objects);
        });
        return this;
    }

    /** 执行循环过程中某一个函数执行失败会继续执行其它函数 */
    public ApplicationContextProvider executeMethodWithAnnotatedException(Class<? extends Annotation> annotation, Object... args) {
        Collection<InstanceMethodProvider> providerMethodStream = withMethodAnnotatedCache(annotation);
        for (InstanceMethodProvider providerMethod : providerMethodStream) {
            try {
                /* 会重新组织参数列表 */
                Object[] objects = this.injectorParameters(providerMethod.getInstance(), providerMethod.getMethod(), args);
                providerMethod.invoke(objects);
            } catch (Throwable e) {
                log.error("执行方法异常：{}-{}", providerMethod.getMethod(), providerMethod.getMethod().getName(), e);
            }
        }
        return this;
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
                    params[i] = ExecutorContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            /*实现注入*/
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                params[i] = getBean(qualifier, parameterType);
                continue;
            }
            params[i] = holderArgument.next();
        }
        return params;
    }

    public <R> R getBean(Qualifier qualifier, Class<R> beanClass) {
        String name = qualifier.value();
        if (StringUtils.isBlank(name))
            return getBean(beanClass);
        else
            return getBean(name);
    }

    public <R> R getBean(Class<R> cls) {
        return applicationContext.getBean(cls);
    }

    @SuppressWarnings("unchecked")
    public <R> R getBean(String names) {
        return (R) applicationContext.getBean(names);
    }

    @Getter
    public class ProviderBean implements Comparable<ProviderBean> {

        private final InstanceProvider instanceProvider;

        ProviderBean(Object bean) {
            this.instanceProvider = new InstanceProvider(bean);
        }

        @Override public int compareTo(ProviderBean o) {
            return this.instanceProvider.compareTo(o.instanceProvider);
        }

        public Collection<InstanceFieldProvider> getFields() {
            return instanceProvider.getFieldProviderMap().values();
        }

        public Collection<InstanceMethodProvider> getMethods() {
            return instanceProvider.getMethodMap().values();
        }

        /** 是否添加了注解 */
        public boolean hasAnn(Class<? extends Annotation> annotation) {
            return instanceProvider.hasAnn(annotation);
        }

        /** 是否添加了注解 */
        public boolean withSuper(Class<?> cls) {
            return instanceProvider.withSuper(cls);
        }

        public Stream<InstanceMethodProvider> methodStream() {
            return getMethods().stream();
        }

        /** 所有添加了这个注解的方法 */
        public Stream<InstanceMethodProvider> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methodStream().filter(provider -> provider.hasAnn(annotation));
        }

        /** 所有添加了这个注解的方法 */
        public Stream<InstanceMethodProvider> methodsEqualsParameters(Class<?>... args) {
            return methodStream().filter(provider -> provider.equalsParameters(args));
        }

        /** 所有添加了这个注解的方法 */
        public Stream<InstanceMethodProvider> methodsAssignableFrom(Class<?>... args) {
            return methodStream().filter(provider -> provider.isAssignableFrom(args));
        }

        /** 所有的字段 */
        public Stream<InstanceFieldProvider> fieldStream() {
            return getFields().stream();
        }

        /** 所有添加了这个注解的字段 */
        public Stream<InstanceFieldProvider> fieldWithAnnotated(Class<? extends Annotation> annotation) {
            return fieldStream().filter(provider -> provider.hasAnn(annotation));
        }

        @Override public String toString() {
            return instanceProvider.toString();
        }
    }

}
