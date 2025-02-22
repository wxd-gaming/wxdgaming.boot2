package run.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EntityTest extends EntityLongUID {

    @DbColumn(key = true)
    @Partition()
    private int day;
    @DbColumn(index = true)
    private boolean online;
    private short sex;
    private int age;
    @DbColumn(length = 128)
    private String name;
    private final List<String> list = new ArrayList<>();
    private byte[] datas;
}
