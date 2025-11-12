package wxdgaming.boot2.core.io;


import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.function.Consumer2;
import wxdgaming.boot2.core.function.ConsumerE1;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.zip.ZipReadFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件读取
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-08-18 14:41
 **/
public class FileReadUtil implements Serializable {


    /** 递归查找所有文件 */
    public static Map<Path, byte[]> readBytesAll(String file, String... extendNames) {
        return readBytesStream(file, extendNames).collect(Collectors.toMap(Tuple2::getLeft, Tuple2::getRight));
    }

    /** 递归查找所有文件 */
    public static Stream<Tuple2<Path, byte[]>> readBytesStream(String file, String... extendNames) {
        return FileUtil.resourceStreams(file, extendNames)
                .map(f -> new Tuple2<>(f.getLeft(), f.getRight()));
    }

    /** 递归查找所有文件 */
    public static void readBytesAll(Path file, String[] extendNames, Consumer2<String, byte[]> call) {
        FileUtil.walkFiles(file, extendNames)
                .forEach(f -> call.accept(f.toString(), readBytes(f)));
    }

    public static String readString(String fileName) {
        return readString(fileName, StandardCharsets.UTF_8);
    }

    /** 获取jar包内资源 需要传入classloader */
    public static String readString(ClassLoader classLoader, String fileName) {
        return readString(classLoader, fileName, StandardCharsets.UTF_8);
    }

    public static String readString(String fileName, Charset charset) {
        return readString(Thread.currentThread().getContextClassLoader(), fileName, charset);
    }

    /** 获取jar包内资源 需要传入classloader */
    public static String readString(ClassLoader classLoader, String fileName, Charset charset) {
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(classLoader, fileName);
        if (inputStream == null) {
            System.out.printf("文件 %s 查找失败\n", fileName);
            return null;
        }
        return new String(inputStream.getRight(), charset);
    }

    public static String readString(File file) {
        return readString(file, StandardCharsets.UTF_8);
    }

    public static String readString(File file, Charset charset) {
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            return readString(fileInputStream, charset);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static String readString(InputStream inputStream) {
        return readString(inputStream, StandardCharsets.UTF_8);
    }

    public static String readString(InputStream inputStream, Charset charset) {
        byte[] readBytes = readBytes(inputStream);
        return new String(readBytes, charset);
    }

    public static List<String> readLines(String fileName) {
        return readLines(fileName, StandardCharsets.UTF_8);
    }

    public static List<String> readLines(String fileName, Charset charset) {
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(fileName);
        if (inputStream == null) {
            System.out.printf("文件 %s 查找失败\n", fileName);
            return null;
        }
        return readLines(new ByteArrayInputStream(inputStream.getRight()), charset);
    }


    public static List<String> readLines(Path path) {
        return readLines(path, StandardCharsets.UTF_8);
    }

    public static List<String> readLines(Path path, Charset charset) {
        try {
            return Files.readAllLines(path, charset);
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static void readLine(Path path, Charset charset, ConsumerE1<String> call) {
        try {
            List<String> strings = Files.readAllLines(path, charset);
            for (int i = 0; i < strings.size(); i++) {
                String string = strings.get(i);
                call.accept(string);
            }
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static List<String> readLines(InputStream fileInputStream, Charset charset) {
        List<String> lines = new ArrayList<>();
        readLine(fileInputStream, charset, lines::add);
        return lines;
    }

    public static void readLine(InputStream fileInputStream, Charset charset, ConsumerE1<String> call) {
        try (final Scanner sc = new Scanner(fileInputStream, charset)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line == null)
                    break;
                call.accept(line);
            }
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static byte[] readBytes(String file) {
        return readBytes(FileUtil.findPath(file));
    }

    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static byte[] readBytes(InputStream inputStream) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[200];
            int read = 0;
            while ((read = inputStream.read(bytes, 0, bytes.length)) >= 0) {
                outputStream.write(bytes, 0, read);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {}
        }
    }

    public static void readBytes(OutputStream outputStream, InputStream inputStream) {
        try (inputStream) {
            byte[] bytes = readBytes(inputStream);
            outputStream.write(bytes);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public static void readJarClass(HashMap<String, byte[]> maps, String jarPath, String... names) {
        try (ZipReadFile zipReadFile = new ZipReadFile(jarPath)) {
            zipReadFile.forEach((fileName, bytes) -> {
                String lowerCase = fileName.toLowerCase();
                for (String name : names) {
                    if (lowerCase.contains(name.toLowerCase())) {
                        String replace = fileName.replace("/", ".").replace("\\", ".");
                        maps.put(replace, bytes);
                        break;
                    }
                }
            });
        }
    }

}
