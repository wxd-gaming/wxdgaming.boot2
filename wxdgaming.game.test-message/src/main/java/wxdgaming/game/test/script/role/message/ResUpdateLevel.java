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


/** 更新等级 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新等级")
public class ResUpdateLevel extends PojoBase {

    /** 当前等级 */
    @Tag(1) private int level;

}
