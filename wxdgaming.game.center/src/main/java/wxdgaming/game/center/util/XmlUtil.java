package wxdgaming.game.center.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import wxdgaming.boot2.core.io.ObjectFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * xml 工具类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-19 10:19
 **/
public class XmlUtil {

    private static final ObjectFactory<Serializer> SIMPLE_XM_LOCAL = new ObjectFactory<>(
            20,
            () -> {
                String formatHead = """
                        <?xml version="1.0" encoding="utf-8"?>
                        <!-- @author wxd-gaming(無心道, 15388152619) -->""";
                Format format = new Format(formatHead);
                return new Persister(format);
            }
    );


    /** 读取XML文件，加载进相应Object类型 */
    public static <T> T fromXml(InputStream stream, Class<T> type) {
        return SIMPLE_XM_LOCAL.apply(serializer -> serializer.read(type, stream, false));
    }

    /**
     * simpleXml
     *
     * @param <T> 泛型类型
     * @param source xml
     * @param type 类型
     * @return 类
     */
    public static <T> T fromXml(String source, Class<T> type) {
        return SIMPLE_XM_LOCAL.apply(serializer -> serializer.read(type, source, false));
    }

    /**
     * simpleXml
     *
     * @param <T> 泛型类型
     * @param source xml
     * @param type 类型
     * @return 类
     */
    public static <T> T fromXml(byte[] source, Class<T> type) {
        try (InputStream inputStream = new ByteArrayInputStream(source)) {
            return SIMPLE_XM_LOCAL.apply(serializer -> serializer.read(type, inputStream, false));
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    /**
     * simpleXml
     *
     * @param obj 对象
     * @return xml 字符串
     */
    public static String toXml(Object obj) {
        try (StringWriter writer = new StringWriter()) {
            SIMPLE_XM_LOCAL.accept(serializer -> serializer.write(obj, writer));
            return writer.toString();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

}
