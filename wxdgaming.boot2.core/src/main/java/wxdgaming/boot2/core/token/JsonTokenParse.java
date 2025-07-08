package wxdgaming.boot2.core.token;

import com.alibaba.fastjson.serializer.SerializerFeature;
import wxdgaming.boot2.core.chatset.Base64Util;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.util.AesUtil;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.Md5Util;

import java.util.Objects;

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
        AssertUtil.assertTrue(jsonToken.getExpire() > System.currentTimeMillis(), "token已过期");
        String string = jsonToken.getData().toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        AssertUtil.assertTrue(Objects.equals(Md5Util.md5DigestEncode0("#", string, key), jsonToken.getSignature()), "token错误");
        return jsonToken;
    }

}
