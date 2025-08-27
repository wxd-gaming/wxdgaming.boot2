package update;

import lombok.Getter;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗简报测试，回合制或者休闲游戏服务器一次性计算所有战斗过程
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-26 21:03
 */
@Getter
public class Fight {

    private final List<Role> leftRoleList;
    private final List<Role> rightRoleList;

    private FightResult fightResult = new FightResult();

    public Fight(List<Role> leftRoleList, List<Role> rightRoleList) {
        this.leftRoleList = leftRoleList;
        this.rightRoleList = rightRoleList;
    }

    public void execute() {
        long time = 0;
        List<Role> attackRoleList = new ArrayList<>(leftRoleList);
        List<Role> targetRoleList = new ArrayList<>(rightRoleList);
        int interval = 100;
        while (true) {
            {

                for (Role attackRole : attackRoleList) {
                    attackRole.getSkillList().stream()
                            .filter(v -> v.getCd() > 0)
                            .forEach(v -> v.setCd(v.getCd() - interval));
                    Skill skill = attackRole.randomSkill();
                    if (skill != null) {
                        Role targetRole = RandomUtils.randomItem(targetRoleList);
                        int costHp = RandomUtils.random(skill.getMinHp(), skill.getMaxHp());
                        targetRole.setHp(targetRole.getHp() - costHp);
                        FightEvent fightEvent = new FightEvent()
                                .setTimes(time)
                                .setAttackRoleId(attackRole.getId())
                                .setTargetRoleId(targetRole.getId())
                                .setSkillId(skill.getId())
                                .setSkillLV(skill.getLv())
                                .setCostHp(costHp)
                                .setTargetHp(targetRole.getHp());
                        for (Skill s : attackRole.getSkillList()) {
                            s.setCd(skill.getExecInterval());
                        }
                        if (targetRole.getHp() <= 0) {
                            fightEvent.setTargetDie(true);
                            targetRoleList.remove(targetRole);
                        }
                        fightResult.getFightEventList().add(fightEvent);
                    }
                }

            }

            if (targetRoleList.isEmpty() || attackRoleList.isEmpty()) {
                break;
            }

            /*攻守双方交换位置*/
            List<Role> tmp = attackRoleList;
            attackRoleList = new ArrayList<>(targetRoleList);
            targetRoleList = new ArrayList<>(tmp);

            /*事件递推*/
            time += interval;
        }
        fightResult.setTimes(time);
    }

}
