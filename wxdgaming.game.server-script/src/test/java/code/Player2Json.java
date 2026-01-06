package code;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.annotation.JSONType;
import com.google.common.collect.HashBasedTable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.format.data.Data2Json;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.bean.task.TaskInfo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * json
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-03 11:16
 **/
@Slf4j
@Getter
@Setter
public class Player2Json {

    @Test
    public void player2Json() {

        Player player = new Player();
        player.setUid(1);
        player.setAccount("test");
        player.setName("test");
        player.getTaskPack().getTasks().put(TaskType.Main, 1, new TaskInfo().setCfgId(1));
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setUid(1);
        roleEntity.setPlayer(player);
        roleEntity.saveRefresh();
        String jsonStringAsWriteType = FastJsonUtil.toJSONStringAsWriteType(roleEntity);
        RoleEntity parse = FastJsonUtil.parse(jsonStringAsWriteType, RoleEntity.class);
        System.out.println(jsonStringAsWriteType);
        System.out.println(FastJsonUtil.toJSONString(roleEntity, JSONWriter.Feature.SortMapEntriesByKeys, JSONWriter.Feature.WriteClassName));
        boolean b = roleEntity.checkHashCode();
    }

    @Test
    public void hashBasedTable() {
        HashBasedTable<TaskType, Integer, Integer> objectObjectObjectHashBasedTable = HashBasedTable.create();
        objectObjectObjectHashBasedTable.put(TaskType.Main, 1, 1);
        System.out.println(FastJsonUtil.toJSONStringAsWriteType(objectObjectObjectHashBasedTable));
    }

    @Test
    public void table() {
        table.put(TaskType.Main, 1, 1);
        testTable.put(TaskType.Main, 1, 1);

        String jsonString = FastJsonUtil.toJSONString(table);
        System.out.println(jsonString);
        Table parseTable = FastJsonUtil.parse(jsonString, table.getClass());
        System.out.println(parseTable);

        String jsonStringAsWriteType = FastJsonUtil.toJSONStringAsWriteType(this);
        System.out.println(jsonStringAsWriteType);
        Player2Json parse = FastJsonUtil.parseSupportAutoType(jsonStringAsWriteType, Player2Json.class);
        Player2Json parse2 = FastJsonUtil.parseSupportAutoType(jsonStringAsWriteType, new TypeReference<Player2Json>() {});
        System.out.println(parse);
    }

    Table<TaskType, Integer, Integer> table = new Table<>();
    TestTable<TaskType, Integer, Integer> testTable = new TestTable<>();

    @Getter
    @Setter
    @JSONType(seeAlso = {HashMap.class})
    public static class TestTable<R, C, V> implements Serializable, Data2Json {
        HashMap<R, HashMap<C, V>> nodes = new HashMap<>();

        public void put(R r, C c, V v) {
            nodes.computeIfAbsent(r, l -> new HashMap<>()).put(c, v);
        }

    }

}
