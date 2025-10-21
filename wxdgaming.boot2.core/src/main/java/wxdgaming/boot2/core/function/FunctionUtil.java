package wxdgaming.boot2.core.function;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FunctionUtil {

    /** 当参数空 返回 默认值 */
    public static <R, T1> R nullDefaultValue(T1 t1, Function<T1, R> function, R defaultValue) {
        if (t1 == null) return defaultValue;
        return function.apply(t1);
    }

    public static <R> List<R> split2List(String source, String separator1, Function1<String, R> function) {
        String[] split = source.split(separator1);
        List<R> list = new ArrayList<>(split.length);
        for (String s : split) {
            list.add(function.apply(s));
        }
        return list;
    }

    public static <R> List<List<R>> split2ListList(String source, String separator1, String separator2, Function1<String, R> function) {
        String[] split = source.split(separator1);
        List<List<R>> list = new ArrayList<>(split.length);
        for (String s : split) {
            List<R> line = new ArrayList<>();
            String[] split1 = s.split(separator2);
            for (String string : split1) {
                R apply = function.apply(string);
                line.add(apply);
            }
            list.add(line);
        }
        return list;
    }

    public static JSONArray split2JSONArray(String source, String separator1) {
        String[] split = source.split(separator1);
        JSONArray list = new JSONArray(split.length);
        list.addAll(Arrays.asList(split));
        return list;
    }


    public static List<JSONArray> split2ListJSONArray(String source, String separator1, String separator2) {
        String[] split = source.split(separator1);
        List<JSONArray> list = new ArrayList<>(split.length);
        for (String s : split) {
            JSONArray line = new JSONArray();
            String[] split1 = s.split(separator2);
            line.addAll(Arrays.asList(split1));
            list.add(line);
        }
        return list;
    }

    public static List<int[]> split2ListIntArray(String source, String separator1, String separator2) {
        List<JSONArray> jsonArrays = FunctionUtil.split2ListJSONArray(source, separator1, separator2);
        ArrayList<int[]> arrays = new ArrayList<>(jsonArrays.size());
        for (JSONArray jsonArray : jsonArrays) {
            int[] apply = FunctionUtil.jsonArray2IntArray.apply(jsonArray);
            arrays.add(apply);
        }
        return arrays;
    }

    public static List<long[]> split2ListLongArray(String source, String separator1, String separator2) {
        List<JSONArray> jsonArrays = FunctionUtil.split2ListJSONArray(source, separator1, separator2);
        ArrayList<long[]> arrays = new ArrayList<>(jsonArrays.size());
        for (JSONArray jsonArray : jsonArrays) {
            long[] apply = FunctionUtil.jsonArray2LongArray.apply(jsonArray);
            arrays.add(apply);
        }
        return arrays;
    }

    public static Function<JSONArray, int[]> jsonArray2IntArray = new Function<JSONArray, int[]>() {
        @Override public int[] apply(JSONArray jsonArray) {
            int[] vs1 = new int[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                vs1[i] = jsonArray.getIntValue(i);
            }
            return vs1;
        }
    };

    public static Function<JSONArray, long[]> jsonArray2LongArray = new Function<JSONArray, long[]>() {
        @Override public long[] apply(JSONArray jsonArray) {
            long[] vs1 = new long[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                vs1[i] = jsonArray.getLongValue(i);
            }
            return vs1;
        }
    };

}
