package wxdgaming.boot2.starter.net.httpclient;

import org.apache.hc.core5.http.ContentType;

import java.nio.charset.StandardCharsets;

/**
 * 静态
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-28 16:06
 **/
public interface HttpConst {

    ContentType APPLICATION_FORM_URLENCODED = ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8);

    ContentType TEXT_HTML = ContentType.create("text/html", StandardCharsets.UTF_8);

    ContentType TEXT_MARKDOWN = ContentType.create("text/markdown", StandardCharsets.UTF_8);

    ContentType TEXT_PLAIN = ContentType.create("text/plain", StandardCharsets.UTF_8);

    ContentType APPLICATION_JSON = ContentType.create("application/json", StandardCharsets.UTF_8);
}
