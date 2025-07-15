package wxdgaming.boot2.starter.net.httpclient;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.zip.GzipUtil;
import wxdgaming.boot2.starter.net.http.HttpHeadNameType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 基于 java 原生的http 信息请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-11-15 12:34
 **/
@Slf4j
@Getter
public final class Response<H extends HttpBase> {

    final H httpBase;
    final String uriPath;
    String postText = null;
    ClassicHttpResponse httpResponse;
    int statusCode = 0;
    Exception exception;
    byte[] bodys = null;
    List<Cookie> cookieStore = null;

    Response(H httpBase, String uriPath) {
        this.httpBase = httpBase;
        this.uriPath = uriPath;
    }

    public String getHeader(String header) {
        return Optional.ofNullable(this.httpResponse.getFirstHeader(header))
                .map(NameValuePair::getValue)
                .orElse(null);
    }

    public String cookie(String name) {
        if (cookieStore == null) return null;
        return cookieStore.stream()
                .filter(v -> v.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public boolean isSuccess() {
        return statusCode == 200;
    }

    public void setBodys(byte[] data) {
        String header = getHeader(HttpHeadNameType.Content_Encoding.getValue());
        if (header != null && !header.isBlank() && header.toLowerCase().contains("gzip")) {
            data = GzipUtil.unGZip(data);
        }
        bodys = data;
    }

    public byte[] body() {
        return bodys;
    }

    public RunResult bodyRunResult() {
        String string = bodyString(StandardCharsets.UTF_8);
        return RunResult.parse(string);
    }

    public <T> T bodyObject(Class<T> clazz) {
        String string = bodyString(StandardCharsets.UTF_8);
        return FastJsonUtil.parse(string, clazz);
    }

    public String bodyString() {
        return bodyString(StandardCharsets.UTF_8);
    }

    public String bodyString(Charset charset) {
        return new String(body(), charset);
    }

    public String bodyUnicodeDecodeString() {
        return StringUtils.unicodeDecode(bodyString());
    }

    public Response<H> logDebug() {
        log.debug("res: {} {}", bodyString(), this.toString());
        return this;
    }

    public Response<H> logInfo() {
        log.info("res: {} {}", bodyString(), this.toString());
        return this;
    }

    public Response<H> systemOut() {
        System.out.println("res: " + bodyString() + " " + this.toString());
        return this;
    }

    @Override public String toString() {
        return httpBase.getClass().getSimpleName() + " url: " + uriPath + Optional.ofNullable(postText).map(v -> ", postText: " + postText).orElse("");
    }
}
