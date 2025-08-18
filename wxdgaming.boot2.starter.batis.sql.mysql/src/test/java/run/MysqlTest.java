package run;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.entity.EntityDouble;
import run.entity.EntityTest;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlDataHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 19:44
 **/
@Slf4j
public class MysqlTest {

    private static MysqlDataHelper dataHelper;

    static {
        SqlConfig sqlConfig = new SqlConfig();
        sqlConfig.setDebug(true);
        sqlConfig.setDriverClassName(Driver.class.getName());
        sqlConfig.setUrl("jdbc:mysql://127.0.0.1:3306/test2");
        sqlConfig.setUsername("root");
        sqlConfig.setPassword("test");
        dataHelper = new MysqlDataHelper(sqlConfig);
        dataHelper.checkTable(EntityTest.class);
        dataHelper.checkTable(EntityDouble.class);
        TableMapping tableMapping = dataHelper.tableMapping(EntityTest.class);
        /*TODO 处理分区表 */
        LocalDateTime localDate = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            /*创建表分区*/
            String form = MyClock.formatDate("yyyyMMdd", localDate);
            localDate = localDate.plusDays(1);
            dataHelper.addPartition(tableMapping.getTableName(), form);
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
        entityTest.setDatas(new byte[100]);
        dataHelper.update(entityTest);
        dataHelper.save(entityTest);
    }

    @Test
    public void selectAll() {
        List<EntityTest> all = dataHelper.findList(EntityTest.class);
        for (EntityTest entityTest : all) {
            System.out.println(entityTest);
        }
    }

    @Test
    public void doubleTest() {
        EntityDouble entityDouble = new EntityDouble();
        entityDouble.setUid(System.currentTimeMillis());
        entityDouble.setD1(1);

        dataHelper.insert(entityDouble);

        List<JSONObject> jsonObjects = dataHelper.queryList("select * from entitydouble");
        for (JSONObject jsonObject : jsonObjects) {
            System.out.println(jsonObject);
        }

    }

}
