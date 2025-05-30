package wxdgaming.boot2.starter.net.server.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * http 监听 绑定工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:36
 **/
@Slf4j
@Getter
public class HttpListenerContent {

    final HttpServerConfig httpServerConfig;
    final RunApplication runApplication;
    final List<HttpFilter> httpFilterList;

    final HashMap<String, HttpMapping> httpMappingMap = new HashMap<>();

    public HttpListenerContent(HttpServerConfig httpServerConfig, RunApplication runApplication) {
        this.httpServerConfig = Objects.returnNonNull(httpServerConfig, HttpServerConfig.INSTANCE);
        this.runApplication = runApplication;

        this.httpFilterList = runApplication.classWithSuper(HttpFilter.class).toList();

        runApplication.getGuiceReflectContext()
                .withMethodAnnotated(HttpRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    HttpRequest methodRequestMapping = AnnUtil.ann(method, HttpRequest.class);

                    String path = "";

                    if (insRequestMapping != null) {
                        path += insRequestMapping.path();
                    } else {
                        String simpleName = ins.getClass().getSimpleName();
                        if (simpleName.endsWith("Spi")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        } else if (simpleName.endsWith("Impl")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 4);
                        } else if (simpleName.endsWith("Service")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 7);
                        } else if (simpleName.endsWith("Controller")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 10);
                        } else if (simpleName.endsWith("Api")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        }
                        path += "/" + simpleName;
                    }
                    if (!path.startsWith("/")) path = "/" + path;
                    if (!path.endsWith("/")) path += "/";
                    if (StringUtils.isBlank(methodRequestMapping.path())) {
                        path += method.getName();
                    } else {
                        path += methodRequestMapping.path();
                    }

                    String lowerCase = path.toLowerCase();
                    JavassistProxy javassistProxy = JavassistProxy.of(ins, method);
                    HttpMapping httpMapping = new HttpMapping(methodRequestMapping, lowerCase, javassistProxy);

                    HttpMapping old = httpMappingMap.put(lowerCase, httpMapping);
                    if (old != null && !Objects.equals(old.javassistProxy().getInstance().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s old = %s - new = %s"
                                .formatted(
                                        lowerCase,
                                        old.javassistProxy().getInstance().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("http listener url: {}", lowerCase);
                });
    }

}
