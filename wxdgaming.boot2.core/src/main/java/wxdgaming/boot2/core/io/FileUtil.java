package wxdgaming.boot2.core.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.JvmUtil;
import wxdgaming.boot2.core.zip.ZipReadFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件辅助
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-08-18 14:40
 **/
public class FileUtil implements Serializable {

    private static Logger log() {
        return LoggerFactory.getLogger(FileUtil.class);
    }

    private static final String[] empty = new String[0];

    /** 返回绝对路径 */
    public static String getCanonicalPath(String fileName) {
        return getCanonicalPath(new File(fileName));
    }

    /** 返回绝对路径 */
    public static String getCanonicalPath(File fileName) {
        try {
            return fileName.getCanonicalPath();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /** 根据传入的类，获取类的jar包路径 */
    public static String clazzJarPath(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * 优先读取 config 目录下面
     * <p> 如果没有，从resources 文件夹下面读取
     */
    public static Path findPath(String fileName) {
        return findPath(fileName, Thread.currentThread().getContextClassLoader());
    }

    public static Path findPath(String fileName, ClassLoader classLoader) {

        fileName = fileName.replace("\\", "/");

        Path filePath = Paths.get(fileName);
        if (exists(filePath)) {
            return filePath;
        }

        if (!fileName.startsWith("config")) {
            filePath = Paths.get("config/" + fileName);
            if (exists(filePath)) {
                return filePath;
            }
        }

        {
            URL resource = classLoader.getResource(fileName);
            if (resource != null) {
                String decode = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
                return Paths.get(decode);
            }
        }
        return null;
    }

    /** graalvm 打包需要 resources.json */
    private static List<String> resources = null;

    /** graalvm 打包需要 resources.json */
    public static List<String> getResources() {
        if (resources == null) {
            InputStream resourceAsStream = ReflectProvider.class.getResourceAsStream("resources.json");
            if (resourceAsStream != null) {
                byte[] bytes = FileReadUtil.readBytes(resourceAsStream);
                String string = new String(bytes, StandardCharsets.UTF_8);
                resources = JSON.parseObject(string, new TypeReference<ArrayList<String>>() {});
            }
        }
        if (resources == null) {
            resources = Collections.emptyList();
        }
        return resources;
    }

    /** 所有jar包的资源名称 */
    public static List<String> jarResources() throws Exception {
        List<String> resourcesPath = new ArrayList<>();
        String x = JvmUtil.javaClassPath();
        String[] split = x.split(File.pathSeparator);
        for (String string : split) {
            Path startPath = Paths.get(string);
            if (!string.endsWith(".jar") && !string.endsWith(".war") && !string.endsWith(".zip")) {
                try (Stream<Path> stream = Files.walk(startPath)) {
                    String target = startPath.toString();
                    stream
                            .map(Path::toString)
                            .filter(s -> s.startsWith(target) && s.length() > target.length())
                            .map(s -> {
                                String replace = s.replace(target + File.separator, "");
                                if (replace.endsWith(".class")) {
                                    replace = replace.replace(".class", "").replace(File.separator, ".");
                                }
                                return replace;
                            })
                            .forEach(resourcesPath::add);
                }
                continue;
            }
            try (InputStream inputStream = Files.newInputStream(startPath);
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                ZipEntry nextEntry = null;
                while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                    /* todo 读取的资源字节可以做解密操作 */
                    resourcesPath.add(nextEntry.getName());
                }
            }
        }
        return resourcesPath;
    }

    /** 获取资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Tuple2<Path, byte[]> findInputStream(String fileName) {
        return findInputStream(Thread.currentThread().getContextClassLoader(), fileName);
    }

    /** 获取资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Tuple2<Path, byte[]> findInputStream(ClassLoader classLoader, String fileName) {
        return resourceStreams(classLoader, fileName).findFirst().orElse(null);
    }

    /** 获取所有资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Stream<Tuple2<Path, byte[]>> resourceStreams(final String path, String... extendNames) {
        return resourceStreams(Thread.currentThread().getContextClassLoader(), path, extendNames);
    }

    /** 获取所有资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Stream<Tuple2<Path, byte[]>> resourceStreams(ClassLoader classLoader, final String path, String... extendNames) {
        try {
            List<Tuple2<Path, byte[]>> list = new ArrayList<>();
            Predicate<String> filter = test -> {
                boolean found = true;
                if (extendNames.length > 0) {
                    found = false;
                    for (int i = 0; i < extendNames.length; i++) {
                        String extension = extendNames[i];
                        found |= test.endsWith(extension);
                    }
                }
                return found;
            };

            String findPath = path;
            boolean fileExists = true;
            if (!new File(path).exists()) {
                fileExists = false;
                if (!path.startsWith("/")) {
                    if (!path.startsWith("config/")) {
                        if (new File("config/" + path).exists()) {
                            findPath = "config/" + path;
                            fileExists = true;
                        }
                    }
                }
            }
            if (fileExists) {/*当本地文件存在*/
                walkFiles(findPath, extendNames)
                        .map(filePath -> new Tuple2<>(filePath, FileReadUtil.readBytes(filePath)))
                        .forEach(list::add);
            } else {
                Enumeration<URL> rs = classLoader.getResources(path);
                while (rs.hasMoreElements()) {
                    URL resource = rs.nextElement();
                    if (resource != null) {
                        findPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
                        String protocol = resource.getProtocol();
                        if ("file".equals(protocol)) {
                            File file1 = new File(resource.toURI());
                            walkFiles(file1.toPath(), extendNames)
                                    .map(filePath -> new Tuple2<>(filePath, FileReadUtil.readBytes(filePath)))
                                    .forEach(list::add);
                        } else if ("zip".equals(protocol) || "jar".equals(protocol) || "war".equals(protocol)) {
                            findPath = findPath.substring(5, findPath.indexOf("!/"));
                            try (ZipReadFile zipFile = new ZipReadFile(findPath)) {
                                zipFile.stream()
                                        .filter(z -> !z.isDirectory())
                                        .filter(v -> v.getName().startsWith(path))
                                        .filter(p -> {
                                            String name = p.getName();
                                            return filter.test(name);
                                        })
                                        .map(z -> new Tuple2<>(Paths.get(z.getName()), zipFile.unzipFile(z)))
                                        .forEach(list::add);
                            }
                        } else if ("resource".equals(protocol)) {
                            getResources().stream()
                                    .filter(v -> v.startsWith(path))
                                    .filter(filter)
                                    .map(v -> {
                                        try (InputStream resourceAsStream = FileUtil.class.getClassLoader().getResourceAsStream(v)) {
                                            byte[] byteArray = FileReadUtil.readBytes(resourceAsStream);
                                            return new Tuple2<>(Paths.get(v), byteArray);
                                        } catch (IOException e) {
                                            throw ExceptionUtils.asRuntimeException(e);
                                        }
                                    })
                                    .forEach(list::add);
                            break;
                        }
                    }
                }
            }
            return list.stream();
        } catch (Exception e) {
            throw Throw.of("resources:" + path, e);
        }
    }

    /** 获取文件名 */
    public static String fileName(File file) {
        String fileName = file.getName();
        int indexOf = fileName.indexOf(".");
        return fileName.substring(0, indexOf);
    }

    /** 获取扩展名 */
    public static String extendName(File file) {
        return extendName(file.getName());
    }

    /** 获取扩展名 */
    public static String extendName(String fileName) {
        int indexOf = fileName.indexOf("?");
        if (indexOf >= 0) {
            fileName = fileName.substring(0, indexOf);
        }
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileName;
    }

    public static boolean exists(String fileName) {
        return new File(fileName).exists();
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /** 下载网络资源 */
    public static void downloadFile(String url, String saveFileName) {
        try {
            long millis = System.currentTimeMillis();
            URL url1 = URI.create(url).toURL();
            try (InputStream inputStream = url1.openStream()) {
                File file = new File(saveFileName);
                FileWriteUtil.fileOutputStream(
                        file,
                        false,
                        outputStream -> FileReadUtil.readBytes(outputStream, inputStream)
                );
                long costTime = System.currentTimeMillis() - millis;
                log().info("下载文件：{}, 保存：{}, 大小：{} kb, 耗时：{} ms", url, saveFileName, file.length() / 1024f, costTime);
            }
        } catch (Exception e) {
            throw Throw.of(url, e);
        }
    }

    public static File createFile(String fileName) {
        return createFile(new File(fileName));
    }

    public static File createFile(File file) {
        return createFile(file, false);
    }

    /**
     * 创建文件
     *
     * @param file  路径
     * @param fugai 覆盖文件
     * @return
     */
    public static File createFile(File file, boolean fugai) {
        try {
            if (!fugai) {
                if (file.exists()) {
                    /*如果文件已经存在，无需创建*/
                    return file;
                }
            }
            mkdirs(file);
            file.createNewFile();
            return file;
        } catch (Exception e) {
            throw Throw.of(file.getPath(), e);
        }
    }

    /**
     * 删除文件 或者 文件夹
     *
     * @param filePath 需要删除的文件或者文件夹
     */
    public static void del(String filePath) {
        del(Paths.get(filePath));
    }

    /**
     * 删除文件 或者 文件夹
     *
     * @param path 删除文件 或者 文件夹
     */
    public static void del(Path path) {
        Stream<Path> walk = walk(path);
        walk.forEach(f -> {
            try {
                boolean delete = Files.deleteIfExists(f);
                log().info("删除 {}：{}, {}", Files.isDirectory(f) ? "文件夹" : "文件", f, delete);
            } catch (Exception ignore) {}
        });
    }

    /**
     * 创建文件夹
     *
     * @param fileName 需要创建文件夹的名字
     */
    public static void mkdirs(String fileName) {
        mkdirs(new File(fileName));
    }

    /**
     * 创建文件夹
     *
     * @param file 需要创建文件夹
     */
    public static void mkdirs(File file) {
        if (file == null) return;
        final File absoluteFile = file.getAbsoluteFile();
        if (absoluteFile.isDirectory()) {
            absoluteFile.mkdirs();
        } else {
            File parentFile = absoluteFile.getParentFile();
            if (parentFile != null)
                parentFile.mkdirs();
        }
    }

    /** 所有的文件夹 */
    public static Stream<Path> walkDirs(Path path, String... extendNames) {
        return walkDirs(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkDirs(Path path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isDirectory);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(String path, String... extendNames) {
        return walkFiles(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(Path path, String... extendNames) {
        return walkFiles(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(String path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isRegularFile);
    }

    public static Stream<Path> walkFiles(Path path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isRegularFile);
    }

    /** 查找所有文件, 文件夹 */
    public static Stream<Path> walk(String path, String... extendNames) {
        return walk(path, Integer.MAX_VALUE, extendNames);
    }

    /**
     * 查找文件, 文件夹
     *
     * @param path     路径
     * @param maxDepth 深度 当前目录是1
     * @return
     */
    public static Stream<Path> walk(String path, int maxDepth, String... extendNames) {
        return walk(Paths.get(path), maxDepth, extendNames);
    }

    /** 查找所有文件, 文件夹 */
    public static Stream<Path> walk(Path path, String... extendNames) {
        return walk(path, Integer.MAX_VALUE, extendNames);
    }

    /**
     * 查找文件, 文件夹
     *
     * @param path     路径
     * @param maxDepth 深度 当前目录是1
     * @return
     */
    public static Stream<Path> walk(Path path, int maxDepth, String... extendNames) {
        if (!Files.exists(path))
            return Stream.of();
        try {
            Stream<Path> walk = Files.walk(path, maxDepth);
            if (extendNames.length > 0) {
                walk = walk.filter(f -> {
                    boolean check = false;
                    for (String extendName : extendNames) {
                        if (f.toString().endsWith(extendName)) {
                            check = true;
                            break;
                        }
                    }
                    return check;
                });
            }
            return walk;
        } catch (Exception e) {
            throw Throw.of(path.toString(), e);
        }
    }

}
