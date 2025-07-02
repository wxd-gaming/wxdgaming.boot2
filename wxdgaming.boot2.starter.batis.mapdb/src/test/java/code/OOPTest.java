package code;

public class OOPTest {


    private void a1(int a, int b) {
        a1(a, b, 0);
    }

    private void a1(int a, int b, int c) {}

    private void a1(int a, int b, int c, int d) {}

    private void a2(AArgs aArgs) {

    }

    public static class AArgs {
        private int a;
        private int b;
        private int c;
        private int d;
    }

}
