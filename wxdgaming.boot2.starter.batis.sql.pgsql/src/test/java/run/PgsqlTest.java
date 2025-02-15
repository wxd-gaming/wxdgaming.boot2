package run;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.entity.EntityTest;
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
        SqlConfig sqlConfig = new SqlConfig();
        sqlConfig.setDebug(true);
        sqlConfig.setDriverClassName("org.postgresql.Driver");
        sqlConfig.setUrl("jdbc:postgresql://192.168.137.10:5432/test2");
        sqlConfig.setUsername("postgres");
        sqlConfig.setPassword("test");
        dataHelper = new PgsqlDataHelper(sqlConfig);
        dataHelper.checkTable(EntityTest.class);
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
        EntityTest entityTest = new EntityTest();
        entityTest.setUid(System.currentTimeMillis());
        entityTest.setDay(Integer.parseInt(MyClock.formatDate("yyyyMMdd")));
        entityTest.setName("测试");
        dataHelper.insert(entityTest);
        entityTest.getList().add("测试1");
        dataHelper.update(entityTest);
    }

    @Test
    public void selectAll() {
        List<EntityTest> all = dataHelper.findAll(EntityTest.class);
        for (EntityTest entityTest : all) {
            System.out.println(entityTest);
        }
    }

}
