package wxdgaming.boot2.starter;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.GuiceModuleBase;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 构建Singleton
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 10:35
 **/
@Slf4j
public class ConfigurationGuiceModule extends GuiceModuleBase {


    public ConfigurationGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }


    @Override protected void bind() throws Throwable {
        AtomicReference<BootstrapProperties> bootstrapProperties = new AtomicReference<>();
        reflectProvider.classWithAnnotated(Configuration.class)
                .forEach(cls -> {
                    Configuration configuration = AnnUtil.ann(cls, Configuration.class);

                    Object nestedValue = null;
                    ConfigurationProperties configurationProperties = AnnUtil.ann(cls, ConfigurationProperties.class);
                    if (configurationProperties != null) {
                        nestedValue = BootConfig.getIns().getValue("${" + configurationProperties.prefix() + "}", cls);

                        if (nestedValue == null) {
                            log.debug("{} {} 配置未找到", cls, configurationProperties.prefix());
                            nestedValue = ReflectProvider.newInstance(cls);
                        }
                    } else {
                        nestedValue = ReflectProvider.newInstance(cls);
                    }
                    log.debug("{} {} 配置", cls, nestedValue.hashCode());
                    if (nestedValue instanceof BootstrapProperties v) {
                        bootstrapProperties.set(v);
                    }
                    String benName = configuration.value();
                    if (StringUtils.isBlank(benName)) {
                        bindInstance(cls, nestedValue);
                    } else {
                        bindInstance(cls, benName, nestedValue);
                    }

                });

        if (bootstrapProperties.get() != null) {
            bindInstance(BootstrapProperties.class, bootstrapProperties.get());
            return;
        }

        BootstrapProperties boot = BootConfig.getIns().getObject("boot", BootstrapProperties.class);
        if (boot == null) {
            log.debug("{} {} 配置未找到", BootstrapProperties.class, "boot");
            boot = new BootstrapProperties();
        }
        bindInstance(BootstrapProperties.class, boot);
    }

}
