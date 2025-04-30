package code;

import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.game.test.bean.global.GlobalDataEntity;
import wxdgaming.game.test.bean.global.impl.YunyingData;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.role.RoleEntity;

public class JsonTypeStringTest {

    @Test
    public void t1() {
        Player player = new Player();
        player.setUid(1);
        player.setName("test");
        player.setLevel(1);
        player.setExp(100);
        RoleEntity roleEntity = new RoleEntity().setUid(1).setPlayer(player);
        roleEntity.saveRefresh();
        String jsonStringAllAsString = FastJsonUtil.toJSONStringWriteTypeAllAsString(roleEntity);
        System.out.println(jsonStringAllAsString);

        RoleEntity parse = FastJsonUtil.parse(jsonStringAllAsString, RoleEntity.class);
        System.out.println(parse);
    }

    @Test
    public void t2() {
        GlobalDataEntity globalDataEntity=new GlobalDataEntity();
        globalDataEntity.setSid(1);
        globalDataEntity.setId(1);
        globalDataEntity.setData(new YunyingData());

        System.out.println(FastJsonUtil.toJSONString(globalDataEntity,FastJsonUtil.Writer_Features_Type_Name));
        System.out.println(FastJsonUtil.toJSONString(globalDataEntity,FastJsonUtil.Writer_Features_Type_Name_NOT_ROOT));
        System.out.println(FastJsonUtil.toJSONString(globalDataEntity,FastJsonUtil.Writer_Type_Name_Features_K_V_String));
    }

}
