package wxdgaming.game.test.script.global.message;

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


/** 属性 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("属性")
public class AttrBean extends PojoBase {

    /** 属性id */
    @Tag(1) private int attrId;
    /** 属性值 */
    @Tag(2) private long value;

}
