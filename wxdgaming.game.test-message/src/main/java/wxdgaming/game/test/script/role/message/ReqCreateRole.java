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


/** 创建角色 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("创建角色")
public class ReqCreateRole extends PojoBase {

    /** 角色名 */
    @Tag(1) private String name;
    /** 性别 */
    @Tag(2) private int sex;
    /** 职业 */
    @Tag(3) private int job;

}
