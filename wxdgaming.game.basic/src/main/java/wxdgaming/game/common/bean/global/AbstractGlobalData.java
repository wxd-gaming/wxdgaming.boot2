package wxdgaming.game.common.bean.global;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.game.common.entity.global.GlobalDataEntity;

/**
 * d
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-08 14:20
 **/
@Getter
@Setter
public abstract class AbstractGlobalData extends ObjectBase {

    private int sid;
    private int type;
    private String comment;
    private boolean merger;

    public GlobalDataEntity globalEntity() {
        GlobalDataEntity globalDataEntity = new GlobalDataEntity();
        globalDataEntity.setUid(sid * 10000 + type);
        globalDataEntity.setSid(sid);
        globalDataEntity.setType(type);
        globalDataEntity.setComment(comment);
        globalDataEntity.setMerger(merger);
        globalDataEntity.setData(this);
        return globalDataEntity;
    }

}
