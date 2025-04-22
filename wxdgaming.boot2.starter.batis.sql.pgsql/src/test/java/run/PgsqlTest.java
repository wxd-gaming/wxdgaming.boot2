package run;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.entity.EntityTest;
import run.entity.Table2;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 19:44
 **/
@Slf4j
public class PgsqlTest {

    private static PgsqlDataHelper dataHelper;


    static {
        ExecutorUtilImpl.impl();
        SqlConfig sqlConfig = new SqlConfig();
        sqlConfig.setDebug(true);
        sqlConfig.setDriverClassName("org.postgresql.Driver");
        sqlConfig.setUrl("jdbc:postgresql://192.168.137.10:5432/test2");
        sqlConfig.setUsername("postgres");
        sqlConfig.setPassword("test");
        sqlConfig.setScanPackage(EntityTest.class.getPackageName());
        dataHelper = new PgsqlDataHelper(sqlConfig);
        TableMapping tableMapping = dataHelper.tableMapping(EntityTest.class);
        /*TODO 处理分区表 */
        LocalDateTime localDate = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            /*创建表分区*/
            String form = MyClock.formatDate("yyyyMMdd", localDate);
            localDate = localDate.plusDays(1);
            String to = MyClock.formatDate("yyyyMMdd", localDate);
            dataHelper.addPartition(tableMapping.getTableName(), form, to);
        }
    }

    @Test
    public void t1() {
        long uid = System.currentTimeMillis();
        int yyyyMMdd = Integer.parseInt(MyClock.formatDate("yyyyMMdd"));
        EntityTest entityTest = new EntityTest();
        entityTest.setUid(uid);
        entityTest.setDay(yyyyMMdd);
        entityTest.setName("测试");
        entityTest.getRemark4().set(400);
        dataHelper.insert(entityTest);
        entityTest.getList().add("测试1");
        dataHelper.update(entityTest);
        dataHelper.save(entityTest);
        EntityTest byKey = dataHelper.findByKey(EntityTest.class, uid, yyyyMMdd);
        System.out.println(byKey);
    }

    @Test
    public void t2() {
        long uid = System.currentTimeMillis();
        Table2 table2 = new Table2();
        table2.setUid(uid);
        table2.setName("测试" + uid);
        dataHelper.insert(table2);
        dataHelper.update(table2);
        dataHelper.save(table2);
        Table2 byKey = dataHelper.getCacheService().cache(Table2.class, uid);
        System.out.println(byKey);
    }

    @Test
    public void selectAll() {
        List<EntityTest> all = dataHelper.findList(EntityTest.class);
        for (EntityTest entityTest : all) {
            System.out.println(entityTest);
        }
    }

}
