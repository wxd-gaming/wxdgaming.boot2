package wxdgaming.boot2.starter.lua;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {


    public static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }


    public static List<ImmutablePair<Path, byte[]>> resourcesList(String path, String[] extensions) {
        try {

            Predicate<String> filter = test -> {
                boolean found = true;
                if (extensions.length > 0) {
                    found = false;
                    for (int i = 0; i < extensions.length; i++) {
                        String extension = extensions[i];
                        found |= test.endsWith(extension);
                    }
                }
                return found;
            };

            List<ImmutablePair<Path, byte[]>> extendList = new ArrayList<>();
            // 获取 resources 目录的 URL
            Enumeration<URL> resources = Utils.class.getClassLoader().getResources(path);
            label:
            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();
                // 判断是否是 JAR 文件
                String protocol = resourceUrl.getProtocol().toLowerCase();
                switch (protocol) {
                    case "jar":
                    case "war":
                    case "zip": // 解析 JAR 文件路径
                        String jarFilePath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!")); // 去掉 "file:" 前缀和 "!/"


                        try (JarFile jarFile = new JarFile(jarFilePath)) {
                            // 获取 JAR 文件中的所有条目
                            Enumeration<JarEntry> entries = jarFile.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (!entry.getName().startsWith(path)) continue;
                                if (entry.isDirectory()) continue;
                                boolean found = filter.test(entry.getName());
                                if (found) {
                                    extendList.add(ImmutablePair.of(Paths.get(entry.getName()), IOUtils.toByteArray(jarFile.getInputStream(entry))));
                                }
                            }
                        }
                        break;
                    case "file": // 如果是文件系统路径
                        File directory = new File(resourceUrl.toURI());
                        Collection<File> files = FileUtils.listFiles(directory, extensions, true);

                        for (File file : files) {
                            byte[] bytes = FileUtils.readFileToByteArray(file);
                            extendList.add(ImmutablePair.of(file.toPath(), bytes));
                        }
                        break;
                    case "resource":
                        ClassUtil.getResources(Utils.class.getClassLoader())
                                .stream()
                                .filter(f -> f.startsWith(path))
                                .filter(filter)
                                .forEach(resource -> {
                                    try (InputStream resourceAsStream = Utils.class.getClassLoader().getResourceAsStream(resource)) {
                                        byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
                                        extendList.add(ImmutablePair.of(Paths.get(resource), byteArray));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                        break label;
                }
            }
            return extendList;
        } catch (IOException | URISyntaxException e) {
            RuntimeException runtimeException = new RuntimeException(e);
            runtimeException.setStackTrace(e.getStackTrace());
            throw runtimeException;
        }
    }


}
