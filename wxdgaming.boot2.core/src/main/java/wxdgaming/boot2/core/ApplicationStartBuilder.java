package wxdgaming.boot2.core;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 启动构建器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 10:12
 **/
@Getter
public class ApplicationStartBuilder {

    public static ApplicationStartBuilder builder(Class<?>... sources) {
        Class<?>[] merge = ArrayUtils.add(sources, MainApplicationContextProvider.class);
        return new ApplicationStartBuilder(merge);
    }

    private final SpringApplicationBuilder springApplicationBuilder;

    private ApplicationStartBuilder(Class<?>... sources) {
        this.springApplicationBuilder = new SpringApplicationBuilder(sources);
    }

    public ApplicationStartBuilder web(WebApplicationType webApplicationType) {
        springApplicationBuilder.web(webApplicationType);
        return this;
    }

    public ApplicationStartBuilder run(String[] args) {
        springApplicationBuilder.run(args);
        return this;
    }

    public ApplicationStartBuilder postInitEvent() {
        SpringUtil.mainApplicationContextProvider.postInitEvent();
        return this;
    }

    public void startBootstrap() {
        SpringUtil.mainApplicationContextProvider.startBootstrap();
    }

}
