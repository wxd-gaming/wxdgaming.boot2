package wxdgaming.boot2.core.loader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * url处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-23 20:06
 **/
public class URLUtil {

    public static boolean printLogger = false;

    public static List<URL> javaClassPathList() {
        ArrayList<URL> arrayList = new ArrayList<>();
        String[] split = System.getProperty("java.class.path").split(File.pathSeparator);
        for (int i = 0; i < split.length; i++) {
            String path = split[i];
            arrayList.add(toURL(path));
        }
        return arrayList;
    }

    public static URL[] javaClassPathArray() {
        String[] split = System.getProperty("java.class.path").split(File.pathSeparator);
        URL[] array = new URL[split.length];
        for (int i = 0; i < split.length; i++) {
            String path = split[i];
            array[i] = toURL(path);
        }
        return array;
    }

    public static List<URL> stringsToURLList(String... paths) {
        ArrayList<URL> arrayList = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            arrayList.add(toURL(path));
        }
        return arrayList;
    }

    public static URL[] stringsToURLArray(String... paths) {
        URL[] array = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            array[i] = toURL(path);
        }
        return array;
    }

    public static List<URL> scanJarURLList(String... paths) {
        List<URL> list = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            try {
                Files.walk(Paths.get(path), 99)
                        .filter(v -> v.toString().endsWith(".jar") || v.toString().endsWith(".zip"))
                        .map(URLUtil::toURL)
                        .forEach(list::add);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException(String.valueOf(path), e);
        }
    }

    public static URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.valueOf(path), e);
        }
    }

    public static URL toURL(String path) {
        return toURL(Paths.get(path));
    }

    public static URL toURL(File path) {
        try {
            return path.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.valueOf(path), e);
        }
    }

    /**
     * 保存 class 文件
     *
     * @param outPath   输出路径
     * @param stringMap 输出文件
     */
    public static void writeClassFile(String outPath, Map<String, byte[]> stringMap) {
        final File file_dir = new File(outPath);
        file_dir.mkdirs();
        for (Map.Entry<String, byte[]> stringEntry : stringMap.entrySet()) {
            final String replace = stringEntry.getKey().replace(".", "/");
            final File file = new File(outPath + "/" + replace + ".class");
            writeBytes(file, stringEntry.getValue());
        }
    }

    /**
     * 创建文件夹
     *
     * @param file 需要创建文件夹
     */
    public static void mkdirs(File file) {
        final File absoluteFile = file.getAbsoluteFile();
        if (absoluteFile.isDirectory()) {
            absoluteFile.mkdirs();
        } else {
            File parentFile = absoluteFile.getParentFile();
            if (parentFile != null)
                parentFile.mkdirs();
        }
    }

    public static void writeBytes(File file, byte[] bytes) {
        mkdirs(file);
        OpenOption[] openOptions;
        openOptions = new OpenOption[2];
        /*非追加文本，如果就要重新创建文件*/
        openOptions[0] = StandardOpenOption.CREATE;
        /*将文件字节流截断为0，这样就达到覆盖文件目的*/
        openOptions[1] = StandardOpenOption.TRUNCATE_EXISTING;
        try (OutputStream out = Files.newOutputStream(file.toPath(), openOptions)) {
            out.write(bytes);
            out.flush();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected static void deleteFile(String path) {
        try {
            Path start = Paths.get(path);
            if (Files.notExists(start)) return;
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

}
