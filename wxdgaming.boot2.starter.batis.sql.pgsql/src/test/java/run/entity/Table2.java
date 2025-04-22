package run.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbTable;

@Getter
@Setter
@DbTable
public class Table2 extends EntityLongUID {
    private String name;
}
