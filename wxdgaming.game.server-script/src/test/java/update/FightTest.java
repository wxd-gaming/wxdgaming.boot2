package update;

import org.junit.jupiter.api.RepeatedTest;
import wxdgaming.boot2.core.format.TimeFormat;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.List;

/**
 * 战斗测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-25 16:51
 **/
public class FightTest {

    @RepeatedTest(5)
    public void f1() {

        Role roleLeft = createRole(1, "left");
        Role roleRight = createRole(2, "right");
        Fight fight = new Fight(List.of(roleLeft), List.of(roleRight));
        DiffTime diffTime = new DiffTime();
        fight.execute();
        float v = diffTime.diffMs5();
        for (FightEvent fightEvent : fight.getFightResult().getFightEventList()) {
            System.out.println(fightEvent);
        }

        System.out.printf(
                "战斗持续时间：%s - 回合：%d - 战报耗时：%s ms%n",
                TimeFormat.of(fight.getFightResult().getTimes() * 100), fight.getFightResult().getFightEventList().size(), v
        );
    }

    public Role createRole(long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setHp(500000);
        for (int i = 0; i < 10; i++) {

            Skill skill = new Skill()
                    .setId(i + 1)
                    .setLv(1)
                    .setExecInterval(RandomUtils.random(1500, 5000))
                    .setMinHp(RandomUtils.random(1000, 3000))
                    .setMinHp(RandomUtils.random(10000, 30000));
            skill.setCd(skill.getExecInterval());
            role.getSkillList().add(skill);

        }
        return role;
    }

}
