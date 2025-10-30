package code;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;

public class PostmTest {

    @Test
    public void t1() {
        String string = """
                ,
                    {
                      "fieldName": "h%s",
                      "fieldComment": "%s点",
                      "fieldType": "int"
                    }
                """;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            stringBuilder.append(string.formatted(i, i));
        }
        System.out.println(stringBuilder);

    }

}
