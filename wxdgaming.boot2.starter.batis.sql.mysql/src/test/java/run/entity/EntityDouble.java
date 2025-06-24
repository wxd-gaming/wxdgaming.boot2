package run.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

@Getter
@Setter
@DbTable
public class EntityDouble extends Entity {

    @DbColumn(key = true)
    private long uid;
    private double d1;

}
