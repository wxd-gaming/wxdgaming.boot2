# 实体映射与插入示例

## 1) 实体类示例

```java
@Getter
@Setter
@DbTable
public class EntityTest extends EntityLongUID {

    @DbColumn(key = true)
    @Partition(initRangeArrays = {"1=2", "2=3"})
    private int day;

    @DbColumn(index = true)
    private boolean online;

    @DbColumn(length = 128)
    private String name;
}
```

要点：

- `@DbTable` 标记数据库表。
- `@DbColumn(key = true)` 定义主键列。
- `@Partition` 可用于分区字段（如按天分区）。

## 2) 插入实体数据示例

```java
@Autowired
private PgsqlDataHelper dataHelper;

public void insertDemo() {
    long uid = System.nanoTime();
    int yyyyMMdd = Integer.parseInt(MyClock.formatDate("yyyyMMdd"));

    EntityTest entity = new EntityTest();
    entity.setUid(uid);
    entity.setDay(yyyyMMdd);
    entity.setName("test-user");

    dataHelper.insert(entity); // 插入
    dataHelper.update(entity); // 更新
    dataHelper.save(entity);   // 存在则更新，不存在则插入

    EntityTest fromDb = dataHelper.findByKey(EntityTest.class, uid, yyyyMMdd);
    System.out.println(fromDb);
}
```
