package wxdgaming.game.login.inner.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.WebFilter;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.login.LoginServerProperties;

/**
 * 拦截器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 15:20
 **/
@Slf4j
@Component
public class InnerFilter implements WebFilter {

    final LoginServerProperties loginServerProperties;

    public InnerFilter(LoginServerProperties loginServerProperties) {
        this.loginServerProperties = loginServerProperties;
    }

    @Override public String filterPath() {
        return "/inner/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String body = SpringUtil.readBody(request);
        JSONObject parameter = JSON.parseObject(body);
        Object sign = parameter.remove("sign");
        String json = parameter.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginServerProperties.getJwtKey());
        if (!Objects.equals(sign, md5DigestEncode)) {
            throw new AssertException("签名错误");
        }
        return true;
    }

}
