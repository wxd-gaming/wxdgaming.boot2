package wxdgaming.game.message.cdkey;

import io.protostuff.Tag;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.ann.Comment;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.net.pojo.PojoBase;


/** 响应使用cdkey */
@Getter
@Setter
@Accessors(chain = true)
@Comment("响应使用cdkey")
public class ResUseCdKey extends PojoBase {

    /**  */
    @Tag(1) private String cdKey;

}
