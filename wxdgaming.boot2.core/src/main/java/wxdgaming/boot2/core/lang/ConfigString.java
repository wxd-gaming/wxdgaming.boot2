package wxdgaming.boot2.core.lang;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.format.string.*;

import java.util.List;
import java.util.function.Function;

/**
 * 配置字符串
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-08 20:44
 **/
public class ConfigString {

    @Getter @JSONField(ordinal = 1) final String value;
    private transient Object object = null;

    @JSONCreator()
    public ConfigString(@JSONField(name = "value") String value) {
        this.value = value;
    }

    public boolean bool() {
        return getObjectByFunction(StringParseUtil.parseBoolean);
    }

    public int intVal() {
        return getObjectByFunction(StringParseUtil.parseInteger);
    }

    public long longVal() {
        return getObjectByFunction(StringParseUtil.parseLong);
    }

    public float floatVal() {
        return getObjectByFunction(StringParseUtil.parseFloat);
    }

    public double doubleVal() {
        return getObjectByFunction(StringParseUtil.parseDouble);
    }

    public int[] intArray() {
        return getObjectByFunction(String2IntArray.parse);
    }

    public long[] longArray() {
        return getObjectByFunction(String2LongArray.parse);
    }

    public float[] floatArray() {
        return getObjectByFunction(String2FloatArray.parse);
    }

    public String[] stringArray() {
        return getObjectByFunction(String2StringArray.parse);
    }


    public int[][] intArray2() {
        return getObjectByFunction(String2IntArray2.parse);
    }

    public long[][] longArray2() {
        return getObjectByFunction(String2LongArray2.parse);
    }

    public float[][] floatArray2() {
        return getObjectByFunction(String2FloatArray2.parse);
    }

    public String[][] stringArray2() {
        return getObjectByFunction(String2StringArray2.parse);
    }

    public List<Integer> intList() {
        return getObjectByFunction(String2IntList.parse);
    }

    public List<Long> longList() {
        return getObjectByFunction(String2LongList.parse);
    }

    public List<Float> floatList() {
        return getObjectByFunction(String2FloatList.parse);
    }

    public List<String> stringList() {
        return getObjectByFunction(String2StringList.parse);
    }

    public List<int[]> intArrayList() {
        return getObjectByFunction(String2IntArrayList.parse);
    }

    public List<long[]> longArrayList() {
        return getObjectByFunction(String2LongArrayList.parse);
    }

    public List<float[]> floatArrayList() {
        return getObjectByFunction(String2FloatArrayList.parse);
    }

    public List<String[]> stringArrayList() {
        return getObjectByFunction(String2StringArrayList.parse);
    }

    public <T> void initObjectByFunction(Function<String, T> function) {
        object = function.apply(value);
    }

    public ConfigString setObject(Object object) {
        this.object = object;
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getObject() {
        return (T) object;
    }

    /** 自定义转化，避免每次都转化 */
    public <T> T getObjectByFunction(Function<String, T> function) {
        if (StringUtils.isBlank(value)) return null;
        if (object == null) {
            object = function.apply(value);
        }
        return getObject();
    }

    @Override public String toString() {
        return value;
    }
}
