package wxdgaming.boot2.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.util.JvmUtil;

/**
 * 运行类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 16:55
 **/
@Slf4j
@Component
public final class MainApplicationContextProvider extends ApplicationContextProvider {

    public static SpringApplicationBuilder builder(Class<?>... sources) {
        Class<?>[] merge = ArrayUtils.add(sources, MainApplicationContextProvider.class);
        return new SpringApplicationBuilder(merge);
    }

    public MainApplicationContextProvider() {
        SpringUtil.mainApplicationContextProvider = this;
    }

    @Override public MainApplicationContextProvider postInitEvent() {
        super.postInitEvent();
        return this;
    }

    @Override public MainApplicationContextProvider postStartEvent() {
        super.postStartEvent();
        return this;
    }

    public MainApplicationContextProvider startBootstrap() {
        postStartEvent();
        log.info("""
                
                =========================================================
                
                                  启动完成 PID:%s
                
                =========================================================
                """.formatted(JvmUtil.processIDString()));
        return this;
    }

}
