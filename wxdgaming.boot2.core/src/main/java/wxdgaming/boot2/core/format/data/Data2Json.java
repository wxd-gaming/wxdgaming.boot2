package wxdgaming.boot2.core.format.data;

import com.alibaba.fastjson2.JSONObject;
import wxdgaming.boot2.core.json.FastJsonUtil;

/**
 * 序列化处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-06-16 16:55
 **/
public interface Data2Json {

    default JSONObject toJSONObject() {
        return FastJsonUtil.parseJSONObject(this);
    }

    /** json */
    default String toJSONString() {
        return FastJsonUtil.toJSONString(this);
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    default String toJSONStringKeyAsString() {
        return FastJsonUtil.toJSONStringKeyAsString(this);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    default String toJSONStringAllAsString() {
        return FastJsonUtil.toJSONStringAllAsString(this);
    }

    /** json, 输出类型名称 */
    default String toJSONStringAsWriteType() {
        return FastJsonUtil.toJSONStringAsWriteType(this);
    }

    /** 格式化json */
    default String toJSONStringAsFmt() {
        return FastJsonUtil.toJSONStringAsFmt(this);
    }

    /** 格式化json,输出类型名称 */
    default String toJsonFmtWriteType() {
        return FastJsonUtil.toJsonFmtWriteType(this);
    }


}
