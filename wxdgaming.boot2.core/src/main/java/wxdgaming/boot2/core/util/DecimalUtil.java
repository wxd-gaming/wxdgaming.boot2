package wxdgaming.boot2.core.util;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 浮点数运算
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-02 17:44
 **/
public class DecimalUtil {

    public static double add(double value, double scale) {
        return BigDecimal.valueOf(value).add(BigDecimal.valueOf(scale), MathContext.UNLIMITED).doubleValue();
    }

    public static float add(float value, float scale) {
        return BigDecimal.valueOf(value).add(BigDecimal.valueOf(scale), MathContext.UNLIMITED).floatValue();
    }

    public static double sub(double value, double scale) {
        return BigDecimal.valueOf(value).subtract(BigDecimal.valueOf(scale), MathContext.UNLIMITED).doubleValue();
    }

    public static float sub(float value, float scale) {
        return BigDecimal.valueOf(value).subtract(BigDecimal.valueOf(scale), MathContext.UNLIMITED).floatValue();
    }

    public static double multiply(double value, double scale) {
        return BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(scale), MathContext.UNLIMITED).doubleValue();
    }

    public static float multiply(float value, float scale) {
        return BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(scale), MathContext.UNLIMITED).floatValue();
    }

    public static double divide(double value, double scale) {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(scale), MathContext.UNLIMITED).doubleValue();
    }

    public static float divide(float value, float scale) {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(scale), MathContext.UNLIMITED).floatValue();
    }

}
