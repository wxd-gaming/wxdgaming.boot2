package wxdgaming.game.server.script.http.yunying.filter;

import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.WebFilter;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;

import java.util.Map;

/**
 * 运营过滤器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:36
 **/
@Slf4j
@Component
public class YunyingFilter implements WebFilter {

    final ConnectLoginProperties connectLoginProperties;

    public YunyingFilter(ConnectLoginProperties connectLoginProperties) {
        this.connectLoginProperties = connectLoginProperties;
    }

    @Override public String filterPath() {
        return "/yunying/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sign = request.getHeader(HttpHeaderNames.AUTHORIZATION.toString());
        Map<String, String> stringStringMap = SpringUtil.readParameterMap(request);
        String selfSign = SignUtil.signByFormData(stringStringMap, connectLoginProperties.getJwtKey());
        AssertUtil.assertTrue(selfSign.equals(sign), "签名错误");
        return true;
    }


}
