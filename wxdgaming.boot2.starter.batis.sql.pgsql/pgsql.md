# pgsql 数据库使用说明

该 ORM 框架 采用的代码优先模式，通过代码结构定义自动生成数据库表结构

## springboot 配置
```angular2html
db:
  pgsql:
    debug: false                                                 #调试模式将会显示sql语句
    driverClassName: "org.postgresql.Driver"
    url: "jdbc:postgresql://@db-host@:5432/test_login"
    username: "@db-user@"
    password: "@db-pwd@"
    scanPackage: ["wxdgaming.game.login.entity"]                   #扫描路径
    connectionTimeoutMs: 2000                                 #链接超时单位毫秒
    idleTimeoutM: 10                                          #空闲超时单位分钟
    minPoolSize: 5                                            #最小连接数
    maxPoolSize: 20                                           #最大连接数
    batchSubmitSize: 500                                      #批量提交数量
    batchThreadSize: 1                                        #批量提交线程数
    cacheArea: 32                                             #缓存分区

```

## 实体类编写

```code
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
    @DbColumn(length = 32800, columnType = ColumnType.String)
    @Convert(Object2JsonStringConverter.class)
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

```
通过对实体类添加注解 @DbTable 来定义数据库表结构，

实体类需要继承 Entity  类 因为泛型传递原因；

添加 @DbColumn 来定义数据库字段结构，

如果类型对象比较特殊无法自动识别可以自定义转换器只需要添加注解
@Convert(Object2JsonStringConverter.class)

添加 @Partition() 来定义分区字段，
@Partition(initRangeArrays = {"1=2", "2=3"})
