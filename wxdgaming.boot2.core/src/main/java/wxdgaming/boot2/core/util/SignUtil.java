package wxdgaming.boot2.core.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.json.FastJsonUtil;

/**
 * 签名
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-09 14:02
 **/
@Slf4j
public class SignUtil {

    /**
     * 根据json数据生成签名
     * {@code MD5(data.toJson() + key)}
     *
     * @param data json数据
     * @param key  密钥
     * @return 签名
     */
    public static String signByJsonKey(Object data, String key) {
        String dataString;
        if (data instanceof String s) {
            dataString = s;
        } else {
            dataString = FastJsonUtil.toJSONString(data, SerializerFeature.MapSortField, SerializerFeature.SortField);
        }
        String string = dataString + key;
        String sign = Md5Util.md5(string);
        if (log.isDebugEnabled()) {
            log.debug("source: {}, sign: {}", string, sign);
        }
        return sign;
    }
}
