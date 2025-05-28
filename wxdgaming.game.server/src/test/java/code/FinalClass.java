package code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public class FinalClass {

    @Getter
    @AllArgsConstructor
    public static class C1 {
        private final int a;
    }

    @Getter
    @Builder(toBuilder = true)
    public static class C2 {
        private int a;
    }

    public static record C3(int a) {
    }


    @Getter
    @SuperBuilder(toBuilder = true)
    public static class C20 {
        private int a;

        public void t0() {}
    }

    @Getter
    @SuperBuilder(toBuilder = true)
    public static class C21 extends C20 {
        private int b;

        @Deprecated
        @Override public void t0() {
            throw new RuntimeException("禁止使用");
        }
    }

    public void t1() {
        C21 build = C21.builder().a(1).b(2).build();
        build.t0();
    }

}
