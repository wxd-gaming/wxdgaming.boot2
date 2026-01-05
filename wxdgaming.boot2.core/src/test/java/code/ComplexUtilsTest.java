package code;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ComplexUtilsTest {

    @Test
    public void t1() {
        try {
            throw new IOException("ddd");
        } catch (Throwable e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

}
