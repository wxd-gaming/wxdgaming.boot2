package wxdgaming.game.message.role;

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


/** 角色信息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("角色信息")
public class RoleBean extends PojoBase {

    /**  */
    @Tag(1) private long rid;
    /**  */
    @Tag(2) private String name;
    /**  */
    @Tag(3) private int level;
    /**  */
    @Tag(4) private long exp;

}
