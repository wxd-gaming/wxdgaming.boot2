package code;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.groovy.GroovyScan;
import wxdgaming.boot2.groovy.GroovyService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-03 16:53
 **/
@SpringBootTest(classes = {GroovyScan.class})
public class GroovyServiceTest {

    @Autowired
    private GroovyService groovyService;

    @Test
    public void v1() throws IOException {
        String string = IOUtils.toString(this.getClass().getResourceAsStream("/a.groovy"), StandardCharsets.UTF_8);
        Object evaluate = groovyService.evaluate(string);
        System.out.println(evaluate);
    }

}
