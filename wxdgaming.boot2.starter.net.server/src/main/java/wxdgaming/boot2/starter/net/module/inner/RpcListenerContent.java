package wxdgaming.boot2.starter.net.module.inner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.assist.JavassistInvoke;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.ann.RpcRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * rpc 监听 绑定工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:36
 **/
@Slf4j
@Getter
public class RpcListenerContent {

    final RunApplication runApplication;
    final List<RpcFilter> rpcFilterList;
    final HashMap<String, RpcMapping> rpcMappingMap = new HashMap<>();

    public RpcListenerContent(RunApplication runApplication) {
        this.runApplication = runApplication;
        this.rpcFilterList = runApplication.getGuiceReflectContext().classWithSuper(RpcFilter.class).toList();
        this.runApplication.getGuiceReflectContext()
                .withMethodAnnotated(RpcRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    RpcRequest methodRequestMapping = AnnUtil.ann(method, RpcRequest.class);

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

                        path += simpleName + "/";
                    }
                    if (!path.startsWith("/")) path = "/" + path;
                    if (!path.endsWith("/")) path += "/";
                    if (StringUtils.isBlank(methodRequestMapping.path())) {
                        path += method.getName();
                    } else {
                        path += methodRequestMapping.path();
                    }

                    String lowerCase = path.toLowerCase();
                    JavassistInvoke javassistInvoke = JavassistInvoke.of(ins, method);
                    RpcMapping rpcMapping = new RpcMapping(methodRequestMapping, lowerCase, javassistInvoke);

                    RpcMapping old = rpcMappingMap.put(lowerCase, rpcMapping);
                    if (old != null && !Objects.equals(old.javassistInvoke().getInstance().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s old = %s - new = %s"
                                .formatted(
                                        lowerCase,
                                        old.javassistInvoke().getInstance().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("rpc listener url: {}", lowerCase);
                });
    }

}
