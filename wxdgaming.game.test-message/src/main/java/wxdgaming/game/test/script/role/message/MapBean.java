package wxdgaming.game.test.script.role.message;

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


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class MapBean extends PojoBase {

    /**  */
    @Tag(1) private String key;
    /**  */
    @Tag(2) private String value;

}
