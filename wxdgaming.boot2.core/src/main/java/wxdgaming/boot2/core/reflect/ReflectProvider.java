package wxdgaming.boot2.core.reflect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.io.FileReadUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-10-16 10:11
 **/
@Getter
public class ReflectProvider {

    public static <R> R newInstance(Class<R> cls, Object... args) {
        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            Constructor<R> constructor = cls.getConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /** 判定 接口, 枚举, 注解, 抽象类 返回 false */
    public static boolean checked(Class<?> aClass) {
        /* 判定 是否可用 */
        return !(
                Object.class.equals(aClass)
                || aClass.isInterface()
                || aClass.isEnum()
                || aClass.isAnnotation()
                || Modifier.isAbstract(aClass.getModifiers())
        );
    }

    /** 获取类型实现的接口 */
    public static Stream<Class<?>> getInterfaces(Class<?> cls) {
        TreeMap<String, Class<?>> classCollection = new TreeMap<>();
        getInterfaces(classCollection, cls);
        return classCollection.values().stream();
    }

    /** 获取类实现的接口 */
    public static void getInterfaces(TreeMap<String, Class<?>> classCollection, Class<?> cls) {
        if (cls == null || Object.class.equals(cls)) {
            return;
        }
        /*查找父类*/
        getInterfaces(classCollection, cls.getSuperclass());
        Class<?>[] interfaces = cls.getInterfaces();
        for (Class<?> aInterface : interfaces) {
            /*查找接口，实现的接口*/
            getInterfaces(classCollection, aInterface);
            /*查找父类*/
            getInterfaces(classCollection, aInterface.getSuperclass());
            if (aInterface.isInterface()) {
                classCollection.put(aInterface.getName(), aInterface);
            }
        }
    }

    /** 获取泛型的第一个 */
    public static <T> Class<T> getTClass(Class<?> source) {
        return getTClass(source, 0);
    }

    /** 获取泛型的类型 */
    public static <T> Class<T> getTClass(Class<?> source, int index) {
        Type genType = source.getGenericSuperclass();
        return getTType(genType, index);
    }

    /** 获取泛型的类型 */
    public static <T> Class<T> getTType(Type source, int index) {
        Type[] params = ((ParameterizedType) source).getActualTypeArguments();
        return (Class) params[index];
    }

    /** 根据 {@link Order} 注解排序，如果值相同使用类名值排序 */
    public static Comparator<Class<?>> ComparatorClassBySort = new Comparator<Class<?>>() {
        @Override public int compare(Class<?> o1, Class<?> o2) {
            int o1Sort = AnnUtil.annOpt(o1, Order.class).map(Order::value).orElse(Const.SORT_DEFAULT);
            int o2Sort = AnnUtil.annOpt(o2, Order.class).map(Order::value).orElse(Const.SORT_DEFAULT);
            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return o1.getName().compareTo(o2.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }
    };

    /** 根据 {@link Order} 注解排序，如果值相同使用类名值排序 */
    public static Comparator<Object> ComparatorBeanBySort = new Comparator<Object>() {
        @Override public int compare(Object o1, Object o2) {
            return ComparatorClassBySort.compare(o1.getClass(), o2.getClass());
        }
    };

    /** 所有的类 */
    private final List<Class<?>> classList;

    public ReflectProvider(Collection<Class<?>> classList) {
        ArrayList<Class<?>> classes = new ArrayList<>(classList);
        classes.sort(ComparatorClassBySort);
        this.classList = Collections.unmodifiableList(classes);
    }

    /** 所有的类 */
    public Stream<Class<?>> classStream() {
        return classList.stream();
    }

    /** 父类或者接口 */
    @SuppressWarnings("unchecked")
    public <U> Stream<Class<U>> classWithSuper(Class<U> cls) {
        return classStream()
                .filter(cls::isAssignableFrom)
                .map(c -> (Class<U>) c);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Class<?>> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classStream().filter(c -> AnnUtil.ann(c, annotation) != null);
    }

    @Setter
    @Accessors(chain = true)
    public static class Builder {

        public static Builder of(String... packageNames) {
            return of(Thread.currentThread().getContextClassLoader(), packageNames);
        }

        public static Builder of(ClassLoader classLoader, String... packageNames) {
            return new Builder(classLoader, packageNames);
        }

        private final ClassLoader classLoader;
        private final String[] packageNames;
        private Predicate<Class<?>> filter;
        /** 是否读取子包 */
        private boolean findChild = true;
        /** 查找类的时候忽略接口 */
        private boolean filterInterface = true;
        /** 过滤掉抽象类 */
        private boolean filterAbstract = true;
        /** 过滤掉枚举类 */
        private boolean filterEnum = true;
        /** graalvm 打包需要 resources.json */
        private ArrayList<String> resources = null;

        /** graalvm 打包需要 resources.json */
        public ArrayList<String> getResources() {
            if (resources == null) {
                InputStream resourceAsStream = classLoader.getResourceAsStream("resources.json");
                if (resourceAsStream != null) {
                    byte[] bytes = FileReadUtil.readBytes(resourceAsStream);
                    String string = new String(bytes, StandardCharsets.UTF_8);
                    resources = JSON.parseObject(string, new TypeReference<ArrayList<String>>() {});
                }
            }
            if (resources == null) {
                resources = new ArrayList<>();
            }
            return resources;
        }

        private Builder(ClassLoader classLoader, String[] packageNames) {
            this.classLoader = classLoader;
            this.packageNames = packageNames;
        }

        /** 所有的类 */
        public ReflectProvider build() {
            TreeMap<String, Class<?>> classCollection = new TreeMap<>();
            for (String packageName : packageNames) {
                findClasses(packageName, aClass -> {
                    if (filter != null && !filter.test(aClass)) {
                        return;
                    }
                    classCollection.put(aClass.getName(), aClass);
                });
            }
            List<Class<?>> list = classCollection.values()
                    .stream()
                    .filter(v -> !Object.class.equals(v))
                    .filter(v -> !filterInterface || !v.isInterface())
                    .filter(v -> !filterAbstract || !Modifier.isAbstract(v.getModifiers()))
                    .filter(v -> !filterEnum || !v.isEnum())
                    .filter(v -> !v.isAnnotation())
                    .toList();
            return new ReflectProvider(list);
        }

        private void findClasses(String packageName, Consumer<Class<?>> consumer) {
            String packagePath = packageName;
            if (packageName.endsWith(".jar") || packageName.endsWith(".war")) {
                packagePath = packageName;
            } else if (!".".equals(packageName)) {
                packagePath = packageName.replace(".", "/");
            }
            try {
                Enumeration<URL> resources = classLoader.getResources(packagePath);
                if (resources != null) {
                    URL url = null;
                    while (resources.hasMoreElements()) {
                        url = resources.nextElement();
                        if (url != null) {
                            String type = url.getProtocol();
                            String urlPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
                            switch (type) {
                                case "file" -> {
                                    String dir = urlPath.substring(0, urlPath.lastIndexOf(packagePath));
                                    findClassByFile(dir, urlPath, consumer);
                                }
                                case "jar", "zip" -> findClassByJar(urlPath, consumer);
                                case "resource" -> {
                                    /* graalvm 打包需要 */
                                    getResources()
                                            .stream()
                                            .filter(v -> v.startsWith(packageName))
                                            .forEach(v -> {
                                                loadClass(v, consumer);
                                            });
                                }
                                case null, default -> System.out.println("未知类型：" + type + " - " + urlPath);
                            }
                        } else {
                            findClassByJars(
                                    ((URLClassLoader) classLoader).getURLs(),
                                    packagePath,
                                    consumer
                            );
                        }
                    }
                }
            } catch (Throwable e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }

        /**
         * 从项目文件获取某包下所有类
         *
         * @param dir      父级文件夹
         * @param filePath 文件路径
         */
        private void findClassByFile(String dir, String filePath, Consumer<Class<?>> consumer) {
            File file = new File(filePath);
            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        if (findChild) {
                            findClassByFile(dir, childFile.getPath(), consumer);
                        }
                    } else {
                        String childFilePath = childFile.getPath();
                        if (childFilePath.endsWith(".class")) {
                            childFilePath = childFilePath.substring(dir.length() - 1, childFilePath.lastIndexOf("."));
                            childFilePath = childFilePath.replace("\\", ".");

                            loadClass(childFilePath, consumer);

                        }
                    }
                }
            }
        }

        /**
         * 从所有jar中搜索该包，并获取该包下所有类
         *
         * @param urls        URL集合
         * @param packagePath 包路径
         */
        private void findClassByJars(URL[] urls, String packagePath, Consumer<Class<?>> consumer) {

            if (urls != null) {
                for (URL url : urls) {
                    String urlPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
                    // 不必搜索classes文件夹
                    if (urlPath.endsWith("classes/")) {
                        continue;
                    }
                    String jarPath = urlPath + "!/" + packagePath;
                    findClassByJar(jarPath, consumer);
                }
            }
        }

        /**
         * 从jar获取某包下所有类
         *
         * @param jarPath jar文件路径
         */
        private void findClassByJar(String jarPath, Consumer<Class<?>> consumer) {
            if (jarPath.startsWith("http://") || jarPath.startsWith("https://")) return;
            String[] jarInfo = jarPath.split("!");
            String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
            String packagePath = jarInfo[1].substring(1);
            String entryName;
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entrys = jarFile.entries();
                while (entrys.hasMoreElements()) {
                    JarEntry jarEntry = entrys.nextElement();
                    entryName = jarEntry.getName();
                    if (entryName.endsWith(".class")) {
                        if (findChild) {
                            if (!entryName.startsWith(packagePath)) {
                                continue;
                            }
                        } else {
                            int index = entryName.lastIndexOf("/");
                            String myPackagePath;
                            if (index != -1) {
                                myPackagePath = entryName.substring(0, index);
                            } else {
                                myPackagePath = entryName;
                            }
                            if (!myPackagePath.equals(packagePath)) {
                                continue;
                            }
                        }
                        entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                        loadClass(entryName, consumer);
                    }
                }
            } catch (Throwable e) {
                throw Throw.of(jarPath, e);
            }
        }

        private void loadClass(String childFilePath, Consumer<Class<?>> consumer) {
            try {
                Class<?> clazz = classLoader.loadClass(childFilePath);
                consumer.accept(clazz);
            } catch (Throwable e) {
                LoggerFactory.getLogger(ReflectProvider.class).error(childFilePath, e);
            }
        }

    }

}
