package wxdgaming.boot2.core.format.data;

import wxdgaming.boot2.core.chatset.json.FastJsonUtil;

/**
 * 序列化处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-06-16 16:55
 **/
public interface Data2Json {

    /** json */
    default String toJson() {
        return FastJsonUtil.toJSONString(this);
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    default String toJsonKeyAsString() {
        return FastJsonUtil.toJSONStringKeyAsString(this);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    default String toJsonAllAsString() {
        return FastJsonUtil.toJSONStringAllAsString(this);
    }

    /** json, 输出类型名称 */
    default String toJsonWriteType() {
        return FastJsonUtil.toJSONStringAsWriteType(this);
    }

    /** 格式化json */
    default String toJsonFmt() {
        return FastJsonUtil.toJSONStringAsFmt(this);
    }

    /** 格式化json,输出类型名称 */
    default String toJsonFmtWriteType() {
        return FastJsonUtil.toJsonFmtWriteType(this);
    }


}
