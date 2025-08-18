package wxdgaming.boot2.core.loader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 扩展加载器,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-23 16:05
 **/
@Getter
@Setter
public class LogbackExtendLoader extends ExtendLoader {

    public static void resetLogback(ClassLoader classLoader, String logbackXml, String key, String value) {
        // 加载logback.xml配置文件
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.putProperty(key, value);
        InputStream resourceAsStream = classLoader.getResourceAsStream(logbackXml);
        try {
            configurator.doConfigure(resourceAsStream);
        } catch (JoranException e) {
            throw new RuntimeException(e);
        }
        LoggerFactory.getLogger("root").info("--------------- init end ---------------");
    }

    public LogbackExtendLoader(ClassLoader mainClassLoader, String... paths) {
        super(mainClassLoader, URLUtil.javaClassPathArray());
        addURLs(paths);
        addExtendPackages(
                this.getClass().getName(),
                "org.slf4j",
                "org.springframework.boot.logging.logback",
                "ch.qos.logback",
                "org.apache.logging.log4j"
        );
    }
}
