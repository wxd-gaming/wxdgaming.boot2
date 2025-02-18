package wxdgaming.boot2.starter.net.module.inner.message;

import io.protostuff.Tag;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.pojo.PojoBase;


/** 执行同步等待消息 */
@Getter
@Setter
@Accessors(chain = true)
public class ResRemote extends PojoBase {

    /**  */
    @Tag(1) private long uid;
    /** 用于验证的消息 */
    @Tag(2) private String token;
    /** 1表示压缩过 */
    @Tag(4) private int gzip;
    /** 用JsonObject来解析 */
    @Tag(5) private String params;

}
