package wxdgaming.boot2.starter.excel.store;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * excel to java code
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-08 19:49
 **/
@Slf4j
public class CreateJavaCode extends ICreateCode {

    @Getter private static final CreateJavaCode ins = new CreateJavaCode();

    CreateJavaCode() {}

    @Override public TypeString typeString() {
        return TypeString.JAVA;
    }

}
