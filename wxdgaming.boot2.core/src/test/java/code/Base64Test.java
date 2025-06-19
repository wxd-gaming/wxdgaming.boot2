package code;

import org.junit.Test;
import wxdgaming.boot2.core.chatset.Base64Util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Test {

    @Test
    public void encoder() {

        String json = """
                {"a":"absdgwet"}""";

        System.out.println(json);
        byte[] encode = Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
        String string = new String(encode, StandardCharsets.UTF_8);
        System.out.println(string);

        byte[] encode1 = Base64.getUrlEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
        String string1 = new String(encode1, StandardCharsets.UTF_8);
        System.out.println(string1);
        String encode2 = URLEncoder.encode(string1, StandardCharsets.UTF_8);
        System.out.println(encode2);

    }

}
