package code.string;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExceptionTest {

    @Test
    public void t1() {
        try {
            r1();
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            System.out.println("111");
            System.out.println(stackTrace);
        }

        try {
            io1();
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            System.out.println("222");
            System.out.println(stackTrace);
        }
    }

    public void r1() {
        try {
            r2();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void r2() throws Exception {
        throw new Exception("r2");
    }

    public void io1() {
        try {
            io2();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public void io2() throws IOException {
        throw new IOException("io");
    }

}
