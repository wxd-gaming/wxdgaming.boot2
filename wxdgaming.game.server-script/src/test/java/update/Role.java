package update;

import lombok.Getter;
import lombok.Setter;
import org.junit.platform.commons.util.RuntimeUtils;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Role {

    private long id;
    private String name;
    private long hp;

    private List<Skill> skillList = new ArrayList<>();
    private List<Buff> buffList = new ArrayList<>();

    public Skill randomSkill() {
        List<Skill> list = skillList.stream().filter(v -> v.getCd() <= 0).toList();
        return RandomUtils.randomItem(list);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;
        return getId() == role.getId();
    }

    @Override public int hashCode() {
        return Long.hashCode(getId());
    }
}
