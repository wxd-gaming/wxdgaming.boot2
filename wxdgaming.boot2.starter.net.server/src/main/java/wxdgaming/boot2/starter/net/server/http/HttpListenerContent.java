package wxdgaming.boot2.starter.net.server.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http 监听 绑定工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 16:36
 **/
@Slf4j
@Getter
public class HttpListenerContent {

    final String END = "--isend--";
    final String TopPath = "/**";

    final List<HttpFilter> httpFilterList;

    final HashMap<String, HttpMapping> httpMappingMap = new HashMap<>();
    final HashMap<String, Object> httpMappingMap2 = new HashMap<>();

    static final Tuple2<HttpMapping, Map<String, String>> NUllTuple = new Tuple2<>(null, MapOf.of());

    public HttpListenerContent(RunApplication runApplication, SocketServerConfig serverConfig) {

        this.httpFilterList = runApplication.classWithSuper(HttpFilter.class).toList();

        runApplication.getGuiceBeanProvider()
                .withMethodAnnotated(HttpRequest.class)
                .forEach(provider -> {
                    Object ins = provider.getBean();
                    Method method = provider.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    HttpRequest methodRequestMapping = AnnUtil.ann(method, HttpRequest.class);

                    String path = "";

                    if (insRequestMapping != null) {
                        path += insRequestMapping.value();
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
                    if (StringUtils.isBlank(methodRequestMapping.value())) {
                        path += method.getName();
                    } else {
                        path += methodRequestMapping.value();
                    }

                    if (path.endsWith(TopPath)) {
                        path = TopPath;
                    }

                    String lowerCase = path.toLowerCase();
                    JavassistProxy javassistProxy = JavassistProxy.of(ins, method);
                    HttpMapping httpMapping = new HttpMapping(methodRequestMapping, lowerCase, javassistProxy, new ArrayList<>(8));
                    if (!path.endsWith(TopPath)) {
                        HashMap<String, Object> tmp = httpMappingMap2;
                        String[] split = lowerCase.split("/");
                        for (int i = 0; i < split.length; i++) {
                            String s = split[i];
                            if (StringUtils.isBlank(s)) continue;
                            if (s.startsWith("{") && s.endsWith("}")) {
                                httpMapping.pathMatcherList().add(s.substring(1, s.length() - 1));
                                s = "*";
                            }
                            tmp = (HashMap<String, Object>) tmp.computeIfAbsent(s, k -> new HashMap<>());
                        }
                        tmp.put(END, httpMapping);
                    }
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
                    log.debug("http listener url: http://localhost:{}{}", serverConfig.getPort(), lowerCase);
                });

        log.debug("http listener url: http://localhost:{}/index.html", serverConfig.getPort());

    }

    @SuppressWarnings("unchecked")
    public HttpMapping getHttpMapping(HttpContext httpContext) {
        String path = httpContext.getRequest().getUriPath();
        HttpMapping hm = httpMappingMap.get(path);
        if (hm != null)
            return hm;

        HashMap<String, Object> map = httpMappingMap2;
        String[] split = path.split("/");
        ArrayList<String> pathMatcherList = new ArrayList<>(8);
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (StringUtils.isBlank(s)) continue;
            HashMap<String, Object> tmp = (HashMap<String, Object>) map.get(s);
            if (tmp == null) {
                tmp = (HashMap<String, Object>) map.get("*");
                if (tmp != null) {
                    pathMatcherList.add(s);
                }
            }
            if (tmp == null) {
                return null;
            }
            map = tmp;
        }
        Object o = map.get(END);
        if (o instanceof HttpMapping httpMapping) {
            if (!httpMapping.pathMatcherList().isEmpty()) {
                Map<String, String> pathMatcherMap = new HashMap<>();
                for (int i = 0; i < httpMapping.pathMatcherList().size(); i++) {
                    String key = httpMapping.pathMatcherList().get(i);
                    String value = pathMatcherList.get(i);
                    pathMatcherMap.put(key, value);
                }
                httpContext.getRequest().pathMatcherMap = pathMatcherMap;
            }
            return httpMapping;
        }
        return null;
    }

    public HttpMapping getTopHttpMapping() {
        return httpMappingMap.get(TopPath);
    }

}
