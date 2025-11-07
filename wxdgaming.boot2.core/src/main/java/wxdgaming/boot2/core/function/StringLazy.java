package wxdgaming.boot2.core.function;

import java.util.function.Supplier;

/**
 * string 类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-06 20:39
 **/
public final class StringLazy {

    public static StringLazy of(Supplier<String> supplier) {
        return new StringLazy(supplier);
    }

    final Supplier<String> supplier;

    private StringLazy(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override public String toString() {
        return supplier.get();
    }

}
