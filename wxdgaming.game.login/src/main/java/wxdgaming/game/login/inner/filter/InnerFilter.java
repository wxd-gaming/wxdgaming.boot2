package wxdgaming.game.login.inner.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.boot2.starter.net.server.http.HttpFilter;
import wxdgaming.game.login.LoginConfig;

import java.lang.reflect.Method;

/**
 * 拦截器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-11 15:20
 **/
@Slf4j
@Singleton
public class InnerFilter implements HttpFilter {

    final LoginConfig loginConfig;

    @Inject
    public InnerFilter(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }


    @Override public Object doFilter(HttpRequest httpRequest, Method method, HttpContext httpContext) {
        if (httpContext.getRequest().getUriPath().startsWith("/inner")) {
            JSONObject reqParams = httpContext.getRequest().getReqParams();
            Object sign = reqParams.remove("sign");
            String json = reqParams.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
            String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginConfig.getJwtKey());
            if (!Objects.equals(sign, md5DigestEncode)) {
                return RunResult.fail("签名错误");
            }
        }
        return null;
    }

}
