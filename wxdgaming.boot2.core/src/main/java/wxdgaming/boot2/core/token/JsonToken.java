package wxdgaming.boot2.core.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;

/**
 * json 密钥
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-16 13:55
 **/
@Getter
@Setter
public class JsonToken {

    private long expire;
    private String signature;
    private final JSONObject data = new JSONObject();

    public <T> T getObject(String key, Class<T> clazz) {
        return data.getObject(key, clazz);
    }

    public String getString(String key) {
        return data.getString(key);
    }

    public Integer getInteger(String key) {
        return data.getInteger(key);
    }

    public int getIntValue(String key) {
        return data.getIntValue(key);
    }

    public Long getLong(String key) {
        return data.getLong(key);
    }

    public long getLongValue(String key) {
        return data.getLongValue(key);
    }

    @Override public String toString() {
        return JSON.toJSONString(this, SerializerFeature.MapSortField, SerializerFeature.SortField);
    }
}
