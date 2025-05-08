package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.game.test.bean.global.GlobalDataEntity;
import wxdgaming.game.test.bean.global.impl.YunyingData;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.role.RoleEntity;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;

import java.util.ArrayList;
import java.util.HashMap;

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
        GlobalDataEntity globalDataEntity = new GlobalDataEntity();
        globalDataEntity.setSid(1);
        globalDataEntity.setId(1);
        globalDataEntity.setData(new YunyingData());

        System.out.println(FastJsonUtil.toJSONString(globalDataEntity, FastJsonUtil.Writer_Features_Type_Name));
        System.out.println(FastJsonUtil.toJSONString(globalDataEntity, FastJsonUtil.Writer_Features_Type_Name_NOT_ROOT));
        System.out.println(FastJsonUtil.toJSONString(globalDataEntity, FastJsonUtil.Writer_Type_Name_Features_K_V_String));
    }

    @Test
    public void t3() {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        map.put(1, Lists.newArrayList(1));
        System.out.println(FastJsonUtil.toJSONString(map, FastJsonUtil.Writer_Type_Name_Features_K_V_String));
    }

    @Test
    public void t4() {
        TaskPack taskPack = new TaskPack();
        taskPack.getTaskFinishList().put(1, Lists.newArrayList(1, 2, 3));
        taskPack.getTasks().put(1, 1, new TaskInfo().setCfgId(1));
        System.out.println(JSON.toJSONString(taskPack));
        System.out.println(FastJsonUtil.toJSONString(taskPack, new SerializerFeature[0]));
        System.out.println(taskPack.toString());
        String jsonString = FastJsonUtil.toJSONString(taskPack, FastJsonUtil.Writer_Features_Type_Name);
        System.out.println(jsonString);
        TaskPack parse = FastJsonUtil.parse(jsonString, TaskPack.class);
        System.out.println(parse);
    }


}
