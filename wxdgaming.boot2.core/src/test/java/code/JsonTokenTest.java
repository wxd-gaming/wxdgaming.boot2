package code;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.Base64Util;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.core.token.JsonTokenParse;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-16 14:20
 **/
public class JsonTokenTest {

    @Test
    public void encode() {
        JsonTokenBuilder jsonTokenBuilder = JsonTokenBuilder
                .of("key")
                .put("account", "t1")
                .put("name", "无心道")
                .put("age", 18);
        String compact = jsonTokenBuilder.compact();
        System.out.println(jsonTokenBuilder.viewData());
        System.out.println(compact);
        JsonToken jsonToken = JsonTokenParse.parse("key", compact);
        System.out.println(jsonToken);
        String decode = Base64Util.decode(compact);
        System.out.println(decode);

    }

}
