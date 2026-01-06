package code;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.game.common.entity.global.GlobalDataEntity;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.global.impl.YunyingData;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;

import java.util.ArrayList;
import java.util.HashMap;

public class FastJsonTypeStringTest {

    @Test
    public void e1(){
        System.out.println(FastJsonUtil.toJSONString(TaskType.Main));
        System.out.println(TaskType.valueOf(TaskType.Main.name()));
        TaskType taskType = FastJsonUtil.parse("\"Main\"", TaskType.class, JSONReader.Feature.ErrorOnEnumNotMatch);
        System.out.println(taskType);
    }

    @Test
    public void t1() {
        Player player = new Player();
        player.setUid(1);
        player.setName("test");
        player.setLevel(1);
        player.setExp(100);
        RoleEntity roleEntity = new RoleEntity().setUid(1).setPlayer(player);
        roleEntity.saveRefresh();
        String jsonStringAllAsString = FastJsonUtil.toJSONStringAsWriteType(roleEntity);
        System.out.println(jsonStringAllAsString);

        RoleEntity parse = FastJsonUtil.parse(jsonStringAllAsString, RoleEntity.class);
        System.out.println(parse.toString());
        System.out.println(FastJsonUtil.toJSONStringAsWriteType(parse));
    }

    @Test
    public void t2() {
        GlobalDataEntity globalDataEntity = new GlobalDataEntity();
        globalDataEntity.setUid(1);
        globalDataEntity.setSid(1);
        globalDataEntity.setData(new YunyingData());

        System.out.println(FastJsonUtil.toJSONStringAsWriteType(globalDataEntity));
        System.out.println(FastJsonUtil.toJSONStringWriteTypeAllAsString(globalDataEntity));
    }

    @Test
    public void t3() {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        map.put(1, Lists.newArrayList(1));
        System.out.println(FastJsonUtil.toJSONStringWriteTypeAllAsString(map));
    }

    @Test
    public void t4() {
        TaskPack taskPack = new TaskPack();
        taskPack.getTaskFinishList().put(TaskType.Main, Lists.newArrayList(1, 2, 3));
        taskPack.getTasks().put(TaskType.Main, 1, new TaskInfo().setCfgId(1));
        System.out.println(JSON.toJSONString(taskPack));
        System.out.println(FastJsonUtil.toJSONString(taskPack));
        System.out.println(taskPack);
        String jsonString = FastJsonUtil.toJsonFmtWriteType(taskPack);
        System.out.println(jsonString);
        TaskPack parse = FastJsonUtil.parse(jsonString, TaskPack.class);
        System.out.println(FastJsonUtil.toJsonFmtWriteType(parse));
    }

    @Test
    public void t5() {
        Pack pack = new Pack();
        HashMap<Long, String> value = Maps.newHashMap();
        value.put(1L, "1");
        pack.intIntObjectTable.put(1, 1, 1);
        pack.table.put(1, value);
        System.out.println(JSON.toJSONString(pack));
        System.out.println(FastJsonUtil.toJSONString(pack));
        System.out.println(pack);
        String jsonString = FastJsonUtil.toJSONStringAsWriteType(pack);
        System.out.println(jsonString);
        Pack parse = FastJsonUtil.parseSupportAutoType(jsonString, Pack.class);
        System.out.println(parse.toJSONStringAsWriteType());
    }

    @Getter
    @Setter
    public static class Pack extends ObjectBase {
        HashMap<Integer, HashMap<Long, String>> table = new HashMap<>();
        IntIntIntTable intIntObjectTable = new IntIntIntTable();
    }

    @Getter
    @Setter
    public static class IntIntIntTable {

        private final HashMap<Integer, HashMap<Integer, Integer>> nodes = new HashMap<>();

        public HashMap<Integer, Integer> row(Integer r) {
            return nodes.computeIfAbsent(r, k -> new HashMap<>());
        }

        public Integer put(Integer r, Integer c, Integer v) {
            return row(r).put(c, v);
        }

    }

    @Getter
    @Setter
    public static class IntIntObjectTable<V> {

        private final HashMap<Integer, HashMap<Integer, V>> nodes = new HashMap<>();

        public HashMap<Integer, V> row(Integer r) {
            return nodes.computeIfAbsent(r, k -> new HashMap<>());
        }

        public V put(Integer r, Integer c, V v) {
            return row(r).put(c, v);
        }

    }

}
