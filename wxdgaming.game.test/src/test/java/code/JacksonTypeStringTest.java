package code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.chatset.json.JacksonUtil;
import wxdgaming.game.test.bean.global.GlobalDataEntity;
import wxdgaming.game.test.bean.global.impl.YunyingData;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;

import java.util.HashMap;

public class JacksonTypeStringTest {


    @Test
    public void t1() throws Exception {
        GlobalDataEntity globalDataEntity = new GlobalDataEntity();
        globalDataEntity.setSid(1);
        globalDataEntity.setId(1);
        globalDataEntity.setData(new YunyingData());
        String jsonString = JacksonUtil.toJSONString(globalDataEntity);
        System.out.println(jsonString);
        JsonNode jsonNode = JacksonUtil.parse(jsonString);
        GlobalDataEntity parse = JacksonUtil.parse(jsonString, GlobalDataEntity.class);
        System.out.println(FastJsonUtil.toJsonFmtWriteType(parse));
    }


    @Test
    public void t2() throws Exception {
        HashMap<String, Object> taskPack = new HashMap<>();
        taskPack.put("1", 1);
        taskPack.put("2", 1L);
        String jsonString = JacksonUtil.toJSONString(taskPack);
        System.out.println(jsonString);
        HashMap<String, Object> parse = JacksonUtil.parse(jsonString, new TypeReference<HashMap<String, Object>>() {});
        System.out.println(FastJsonUtil.toJsonFmtWriteType(parse));
    }

    @Test
    public void t4() throws Exception {
        TaskPack taskPack = new TaskPack();
        taskPack.getTaskFinishList().put(1, Lists.newArrayList(1, 2, 3));
        taskPack.getTasks().put(1, 1, new TaskInfo().setCfgId(1));
        String jsonString = JacksonUtil.toJSONString(taskPack);
        System.out.println(jsonString);
        TaskPack parse = JacksonUtil.parse(jsonString, TaskPack.class);
        System.out.println(FastJsonUtil.toJsonFmtWriteType(parse));
    }

}
