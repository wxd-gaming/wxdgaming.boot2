package code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.starter.condition.ConditionService;

/**
 * 比较器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 16:04
 **/
@SpringBootTest(classes = {ConditionTest.class})
@ComponentScan(basePackages = {"code", "wxdgaming"})
public class ConditionTest {

    @Autowired
    MainApplicationContextProvider provider;
    @Autowired
    ConditionService conditionService;

    @BeforeEach
    public void before() {
        provider.postInitEvent();
    }

    @Test
    public void t1() {
        SelfBean jsonObject = new SelfBean();
        TestBean value = new TestBean();
        value.setLevel(2);
        jsonObject.setPlayer(value);

        conditionService.testAll(jsonObject, conditionService.parse("level#>#10"), System.out::println);
        conditionService.testAll(jsonObject, conditionService.parse("vip#>#10"), System.out::println);
    }

}
