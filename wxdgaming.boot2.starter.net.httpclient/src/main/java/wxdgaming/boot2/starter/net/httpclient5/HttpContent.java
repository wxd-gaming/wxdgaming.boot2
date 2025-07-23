package wxdgaming.boot2.starter.net.httpclient5;

import lombok.Getter;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.core5.http.ClassicHttpResponse;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 请求结果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-23 14:11
 **/
@Getter
public class HttpContent {

    ClassicHttpResponse classicHttpResponse;
    int code;
    byte[] content;
    Throw exception;
    List<Cookie> cookieStore = null;

    public boolean isSuccess() {
        return code == 200;
    }

    private void check() {
        if (!isSuccess()) {
            throw exception;
        }
    }

    public RunResult bodyRunResult() {
        check();
        String string = bodyString(StandardCharsets.UTF_8);
        return RunResult.parse(string);
    }

    public <T> T bodyObject(Class<T> clazz) {
        check();
        String string = bodyString(StandardCharsets.UTF_8);
        return FastJsonUtil.parse(string, clazz);
    }

    public String bodyString() {
        check();
        return bodyString(StandardCharsets.UTF_8);
    }

    public String bodyString(Charset charset) {
        check();
        return new String(getContent(), charset);
    }

    public String bodyUnicodeDecodeString() {
        check();
        return StringUtils.unicodeDecode(bodyString());
    }

    @Override public String toString() {
        return "HttpContent{code=%d, content=%s}".formatted(code, bodyString());
    }
}
