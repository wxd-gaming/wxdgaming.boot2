package code;

public class LzyTest {

    private static final class Lazy {
        public static LzyTest INSTANCE = new LzyTest();
    }

    public static LzyTest getInstance() {
        return Lazy.INSTANCE;
    }

    public LzyTest(String a) {
        Lazy.INSTANCE = this;
        System.out.println(2);
    }

    public LzyTest() {
        System.out.println(1);
    }

    public void print() {
        System.out.println("LzyTest " + this.hashCode());
    }

    public static void main(String[] args) {

        LzyTest.getInstance().print();
        new LzyTest("a");
        LzyTest.getInstance().print();
    }

}
