package wxdgaming.boot2.starter.batis;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据批量提交
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-16 20:42
 **/
@Getter
@Setter
public abstract class DataBatch {


    public abstract void insert(Entity entity);

    public abstract void update(Entity entity);

}
