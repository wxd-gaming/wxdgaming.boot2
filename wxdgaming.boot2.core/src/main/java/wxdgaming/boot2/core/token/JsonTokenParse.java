package wxdgaming.boot2.core.token;

import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.util.AesUtil;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.Base64Util;
import wxdgaming.boot2.core.util.Md5Util;

/**
 * 构建器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-16 13:59
 **/
@Slf4j
public class JsonTokenParse {

    public static JsonToken parse(String key, String token) throws IllegalArgumentException {
        token = AesUtil.convert_ASE(token, JsonTokenBuilder.DECODE_KK);
        token = Base64Util.decode(token);
        JsonToken jsonToken = FastJsonUtil.parse(token, JsonToken.class);
        AssertUtil.isTrue(jsonToken.getExpire() > System.currentTimeMillis(), "token已过期");
        String string = jsonToken.getData().toString(JSONWriter.Feature.SortMapEntriesByKeys);
        String join = String.join("#", string, key);
        String selfSign = Md5Util.md5(join);
        String targetSign = jsonToken.getSignature();
        log.debug("md5join: {}, selfSign={}, targetSign={}", join, selfSign, targetSign);
        AssertUtil.isEquals(selfSign, targetSign, "token 错误");
        return jsonToken;
    }

}
