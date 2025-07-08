package code.local;

public class ThreadLocalTest {

    public static void main(String[] args) {

        try {
            ThreadLocalFactory.push(new StringBuilder());
            t1();
            StringBuilder pop = ThreadLocalFactory.pop(StringBuilder.class);
            System.out.println(pop);
        } finally {
            ThreadLocalFactory.release();
        }
    }

    public static void t1() {
        StringBuilder peek = ThreadLocalFactory.peek(StringBuilder.class);
        peek.append("1");
        try {
            ThreadLocalFactory.push(new StringBuilder());
            ThreadLocalFactory.peekOptional(StringBuilder.class).ifPresent(stringBuilder -> stringBuilder.append("2"));
        } finally {
            StringBuilder stringBuilder = ThreadLocalFactory.pop(StringBuilder.class);
            peek.append("ss").append(stringBuilder);
            System.out.println(stringBuilder);
        }
    }

}
