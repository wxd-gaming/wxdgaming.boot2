package wxdgaming.boot2.core.lang;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.format.string.*;

import java.util.List;

/**
 * 配置字符串
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-08 20:44
 **/
public class ConfigString {

    @Getter @JSONField(ordinal = 1)
    String value;
    private transient Integer integer = null;
    private transient Long aLong = null;
    private transient Float aFloat = null;
    private transient int[] intArray = null;
    private transient long[] longArray = null;
    private transient float[] floatArray = null;
    private transient String[] stringArray = null;

    private transient int[][] intArray2 = null;
    private transient long[][] longArray2 = null;
    private transient float[][] floatArray2 = null;
    private transient String[][] stringArray2 = null;

    private transient List<Integer> intList = null;
    private transient List<Long> longList = null;
    private transient List<Float> floatList = null;
    private transient List<String> stringList = null;

    private transient List<int[]> intArrayList = null;
    private transient List<long[]> longArrayList = null;
    private transient List<float[]> floatArrayList = null;
    private transient List<String[]> stringArrayList = null;

    public ConfigString(String value) {
        this.value = value;
    }

    public int intVal() {
        if (StringUtils.isBlank(value)) return 0;
        if (integer == null) {
            integer = Integer.valueOf(value);
        }
        return integer;
    }

    public long longVal() {
        if (StringUtils.isBlank(value)) return 0;
        if (aLong == null) {
            aLong = Long.valueOf(value);
        }
        return aLong;
    }

    public float floatVal() {
        if (StringUtils.isBlank(value)) return 0f;
        if (aFloat == null) {
            aFloat = Float.valueOf(value);
        }
        return aFloat;
    }

    public int[] intArray() {
        if (StringUtils.isBlank(value)) return null;
        if (intArray == null) {
            intArray = String2IntArray.parse(value);
        }
        return intArray;
    }

    public long[] longArray() {
        if (StringUtils.isBlank(value)) return null;
        if (longArray == null) {
            longArray = String2LongArray.parse(value);
        }
        return longArray;
    }

    public float[] floatArray() {
        if (StringUtils.isBlank(value)) return null;
        if (floatArray == null) {
            floatArray = String2FloatArray.parse(value);
        }
        return floatArray;
    }

    public String[] stringArray() {
        if (StringUtils.isBlank(value)) return null;
        if (stringArray == null) {
            stringArray = String2StringArray.parse(value);
        }
        return stringArray;
    }


    public int[][] intArray2() {
        if (StringUtils.isBlank(value)) return null;
        if (intArray2 == null) {
            intArray2 = String2IntArray2.parse(value);
        }
        return intArray2;
    }

    public long[][] longArray2() {
        if (StringUtils.isBlank(value)) return null;
        if (longArray2 == null) {
            longArray2 = String2LongArray2.parse(value);
        }
        return longArray2;
    }

    public float[][] floatArray2() {
        if (StringUtils.isBlank(value)) return null;
        if (floatArray2 == null) {
            floatArray2 = String2FloatArray2.parse(value);
        }
        return floatArray2;
    }

    public String[][] stringArray2() {
        if (StringUtils.isBlank(value)) return null;
        if (stringArray2 == null) {
            stringArray2 = String2StringArray2.parse(value);
        }
        return stringArray2;
    }

    public List<Integer> intList() {
        if (StringUtils.isBlank(value)) return null;
        if (intList == null) {
            intList = String2IntList.parse(value);
        }
        return intList;
    }

    public List<Long> longList() {
        if (StringUtils.isBlank(value)) return null;
        if (longList == null) {
            longList = String2LongList.parse(value);
        }
        return longList;
    }

    public List<Float> floatList() {
        if (StringUtils.isBlank(value)) return null;
        if (floatList == null) {
            floatList = String2FloatList.parse(value);
        }
        return floatList;
    }

    public List<String> stringList() {
        if (StringUtils.isBlank(value)) return null;
        if (stringList == null) {
            stringList = String2StringList.parse(value);
        }
        return stringList;
    }

    public List<int[]> intArrayList() {
        if (StringUtils.isBlank(value)) return null;
        if (intArrayList == null) {
            intArrayList = String2IntArrayList.parse(value);
        }
        return intArrayList;
    }

    public List<long[]> longArrayList() {
        if (StringUtils.isBlank(value)) return null;
        if (longArrayList == null) {
            longArrayList = String2LongArrayList.parse(value);
        }
        return longArrayList;
    }

    public List<float[]> floatArrayList() {
        if (StringUtils.isBlank(value)) return null;
        if (floatArrayList == null) {
            floatArrayList = String2FloatArrayList.parse(value);
        }
        return floatArrayList;
    }

    public List<String[]> stringArrayList() {
        if (StringUtils.isBlank(value)) return null;
        if (stringArrayList == null) {
            stringArrayList = String2StringArrayList.parse(value);
        }
        return stringArrayList;
    }

    @Override public String toString() {
        return value;
    }
}
