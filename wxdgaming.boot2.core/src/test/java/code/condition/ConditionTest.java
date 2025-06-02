package code.condition;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.condition.Condition;

@Slf4j
public class ConditionTest {


    @Test
    public void t1() {
        String a = """
                {"k1":1001,"update":"Replace"}""";
        log.info(a);
        Condition parse = FastJsonUtil.parse(a, Condition.class);
        log.info(parse.toString());
    }

}
