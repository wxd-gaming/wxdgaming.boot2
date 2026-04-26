package wxdgaming.minitieba.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 静态资源配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${server.port:18080}")
    private int serverPort;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = new File("upload").getAbsolutePath();
        String resourceLocation = "file:" + uploadPath + "/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(resourceLocation);

        System.out.println("[StaticResourceConfig] Upload path: " + uploadPath);
    }
}
