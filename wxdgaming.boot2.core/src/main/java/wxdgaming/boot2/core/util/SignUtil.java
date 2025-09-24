package wxdgaming.boot2.core.util;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.Map;
import java.util.TreeMap;

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
            dataString = FastJsonUtil.toJSONString(data, FastJsonUtil.Writer_Features);
        }
        String string = dataString + key;
        String sign = Md5Util.md5(string);
        if (log.isDebugEnabled()) {
            log.debug("source: {}, sign: {}", string, sign);
        }
        return sign;
    }

    public static String signByFormData(Map<String, ?> data, String key) {
        TreeMap<String, ?> tmap;
        if (data instanceof TreeMap<String, ?>) {
            tmap = (TreeMap<String, ?>) data;
        } else {
            tmap = new TreeMap<>(data);
        }
        String dataString = tmap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        String string = dataString + key;
        String sign = Md5Util.md5(string);
        if (log.isDebugEnabled()) {
            log.debug("source: {}, sign: {}", string, sign);
        }
        return sign;
    }

}
