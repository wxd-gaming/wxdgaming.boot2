package code.hold;

/**
 * 懒汉模式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-16 19:37
 **/
public class HoldManager {

    private static final HoldManager ins = new HoldManager();

    public static HoldManager getInstance() {
        return ins;
    }

    public static void init(){}

    private HoldManager() {
        System.out.println("HoldManager init");
    }

}
