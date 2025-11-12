package wxdgaming.boot2.core.zip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.io.FileReadUtil;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 读取 zip 文件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-05-09 20:17
 **/
@Slf4j
public class ZipReadFile implements Serializable, Closeable {

    final ZipFile zip;

    public ZipReadFile(String zipPath) {
        try {
            zip = new ZipFile(new File(zipPath));
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public ZipReadFile(File zipPath) {
        try {
            zip = new ZipFile(zipPath);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (zip != null) zip.close();
        } catch (Exception e) {
            log.error("关闭流：{}", zip.getName(), e);
        }
    }

    public Stream<? extends ZipEntry> stream() {
        return zip.stream();
    }

    public byte[] find(String entryName) {
        ZipEntry zipEntry = zip.getEntry(entryName);
        return unzipFile(zipEntry);
    }

    /** 读取一个 .zip 所有内容 */
    public Map<String, byte[]> readAll() {
        Map<String, byte[]> map = new LinkedHashMap<>();
        forEach(map::put);
        return map;
    }

    /**
     * 循环读取所有
     *
     * @param call 循环
     * @return
     */
    public void forEach(BiConsumer<String, byte[]> call) {
        zip.stream()
                .filter(z -> !z.isDirectory())
                .forEach(z -> {
                    byte[] bytes = unzipFile(z);
                    call.accept(z.getName(), bytes);
                });
    }

    /** InputStream 需要自己关闭 */
    public void forEachStream(BiConsumer<String, InputStream> call) {
        zip.stream()
                .filter(z -> !z.isDirectory())
                .forEach(z -> {
                    try (InputStream inputStream = unzipFileStream(z)) {
                        call.accept(z.getName(), inputStream);
                    } catch (Throwable e) {
                        throw ExceptionUtils.asRuntimeException(e);
                    }
                });
    }

    public byte[] unzipFile(ZipEntry zipEntry) {
        InputStream inputStream = unzipFileStream(zipEntry);
        return FileReadUtil.readBytes(inputStream);
    }

    /** InputStream 需要自己关闭 */
    public InputStream unzipFileStream(ZipEntry zipEntry) {
        try {
            return zip.getInputStream(zipEntry);
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

}
