package code.hold;

/**
 * 单例模式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-16 19:37
 **/
public class HoldTest {

    public static void main(String[] args) {
        Class<HoldManager> holdManagerClass = HoldManager.class;
        Class<HoldClassManager> holdClassManagerClass = HoldClassManager.class;
        System.out.println(holdManagerClass);
        System.out.println(holdClassManagerClass);

        HoldManager.init();
        HoldClassManager.init();
        HoldClassManager.getInstance();

    }

    HoldSupplier proxy;

    public void init(HoldSupplier proxy) {
        this.proxy = proxy;
    }

    public <T> T get(String key) {
        return proxy.get(key);
    }

}
