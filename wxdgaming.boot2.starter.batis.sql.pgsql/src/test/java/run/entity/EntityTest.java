package run.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@DbTable
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
    @DbColumn(length = 12800)
    private String remark;
    @DbColumn(length = 32800)
    private String remark2;
    private final List<String> list = new ArrayList<>();

    private byte[] datas;
    @DbColumn(index = true)
    private int overhead = 2;
    private AtomicReference<String> remark3 = new AtomicReference<>("test");
    private final AtomicReference<Integer> remark4 = new AtomicReference<>(4);
    private Sex sex2 = Sex.MALE;

    public enum Sex {
        MALE,
        FEMALE
    }

}
