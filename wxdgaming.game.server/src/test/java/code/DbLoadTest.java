package code;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlScan;
import wxdgaming.game.server.bean.role.RoleEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {CoreScan.class, PgsqlScan.class})
public class DbLoadTest {

    @Autowired
    PgsqlDataHelper pgsqlDataHelper;
    static List<Long> ridList = new ArrayList<>(List.of(617281845461013L, 618004336345105L, 618097744543756L, 617281845461010L, 616765064216594L));

    @RepeatedTest(5)
    public void load() {
        Long aLong = ridList.removeFirst();
        RoleEntity byKey = pgsqlDataHelper.getCacheService().cacheIfPresent(RoleEntity.class, aLong);
        System.out.println(byKey);
    }

    @Test
    public void t1() {
        System.out.println(TimeUnit.MICROSECONDS.toMillis(65648));
    }

}
