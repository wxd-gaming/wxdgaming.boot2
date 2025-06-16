package wxdgaming.boot2.core.token;

import com.alibaba.fastjson.serializer.SerializerFeature;
import wxdgaming.boot2.core.chatset.Base64Util;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.util.AesUtil;
import wxdgaming.boot2.core.util.Md5Util;

/**
 * 构建器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-16 13:59
 **/
public class JsonTokenParse {

    public static JsonToken parse(String key, String token) throws AssertException {
        token = AesUtil.convert_ASE(token, JsonTokenBuilder.DECODE_KK);
        token = Base64Util.decode(token);
        JsonToken jsonToken = FastJsonUtil.parse(token, JsonToken.class);
        String string = jsonToken.getData().toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        if (!Md5Util.md5DigestEncode0("#", string, key).equals(jsonToken.getSignature())) {
            throw new AssertException("token错误");
        }
        if (jsonToken.getExpire() < System.currentTimeMillis()) {
            throw new AssertException("token已过期");
        }
        return jsonToken;
    }

}
