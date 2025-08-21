package wxdgaming.game.server.script.gm.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.script.gm.ann.GM;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.task.ITaskScript;
import wxdgaming.game.server.script.task.TaskService;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色等级相关
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:50
 **/
@Slf4j
@Component
public class PlayerTaskGmScript {

    final PlayerService playerService;
    final TaskService taskService;

    public PlayerTaskGmScript(PlayerService playerService, TaskService taskService) {
        this.playerService = playerService;
        this.taskService = taskService;
    }

    @GM
    public void acceptTask(Player player, JSONArray args) {
        ITaskScript taskScript = taskService.getTaskScript(TaskType.Main);
        TaskPack taskPack = player.getTaskPack();
        HashMap<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().get(TaskType.Main);
        Map.Entry<Integer, TaskInfo> next = integerTaskInfoMap.entrySet().iterator().next();
        taskScript.acceptTask(player, taskPack, next.getKey());
    }

    @GM
    public void completeTask(Player player, JSONArray args) {
        int taskId = args.getIntValue(1);
        TaskPack taskPack = player.getTaskPack();
        /*通知客户端*/
        ResUpdateTaskList resUpdateTaskList = new ResUpdateTaskList();
        taskPack.getTasks().forEach(task -> {
            if (task.getCfgId() != taskId) {
                return;
            }
            if (task.getAcceptTime() <= 0) {
                return;
            }
            if (task.isComplete()) {
                return;
            }
            task.setComplete(true);
            log.info("{} gm 命令强制完成任务 {}", player, task);
            resUpdateTaskList.getTasks().add(task.buildTaskBean());
        });
        player.write(resUpdateTaskList);
    }

    @GM
    public void submitTask(Player player, JSONArray args) {
        ITaskScript taskScript = taskService.getTaskScript(TaskType.Main);
        TaskPack taskPack = player.getTaskPack();
        HashMap<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().get(TaskType.Main);
        Map.Entry<Integer, TaskInfo> next = integerTaskInfoMap.entrySet().iterator().next();
        taskScript.submitTask(player, taskPack, next.getKey());
    }

}
