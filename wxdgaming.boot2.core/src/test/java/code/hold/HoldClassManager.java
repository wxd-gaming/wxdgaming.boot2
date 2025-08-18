package code.hold;

import java.util.function.Supplier;

/**
 * 内部类模式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-16 19:37
 **/
public class HoldClassManager {

    private static final class Holder {
        private static final HoldClassManager INSTANCE = new HoldClassManager();
    }

    public static void init() {}

    public static HoldClassManager getInstance() {
        return Holder.INSTANCE;
    }

    private HoldClassManager() {

        System.out.println("HoldManager init");
    }

}
