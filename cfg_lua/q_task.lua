--- q_task 任务集合
--- src/main/cfg/任务成就.xlsx q_task

--- @class QTask
---@field id any 主键id
---@field taskType any 任务类型
---@field before any 上一个任务ID
---@field after any 下一个任务ID
---@field name any 任务名称
---@field description any 任务说明
---@field min_lv any 等级
---@field max_lv any 等级
---@field conditionList any 任务条件
---@field acceptCost any 任务接取的时候需要扣除的道具
---@field rewards any 任务奖励
---@field submitCost any 任务提交的时候需要扣除的道具
QTask = {}
QTask.__index = QTask

---@type table<string, QTask>
QTaskTable = {
    [1]  = { id = 1, taskType = "Main", before = 0, after = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":10}]", acceptCost = "[{\"num\":1,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":10,\"cfgId\":5}]", submitCost = "[{\"num\":1,\"cfgId\":1}]" },
    [2]  = { id = 2, taskType = "Main", before = 1, after = 3, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":20}]", acceptCost = "[{\"num\":2,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":11,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":2,\"cfgId\":1}]" },
    [3]  = { id = 3, taskType = "Main", before = 2, after = 4, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":30}]", acceptCost = "[{\"num\":3,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":12,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":3,\"cfgId\":1}]" },
    [4]  = { id = 4, taskType = "Main", before = 3, after = 5, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":40}]", acceptCost = "[{\"num\":4,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":13,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":4,\"cfgId\":1}]" },
    [5]  = { id = 5, taskType = "Main", before = 4, after = 6, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":50}]", acceptCost = "[{\"num\":5,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":14,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":5,\"cfgId\":1}]" },
    [6]  = { id = 6, taskType = "Main", before = 5, after = 7, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":60}]", acceptCost = "[{\"num\":6,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":15,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":6,\"cfgId\":1}]" },
    [7]  = { id = 7, taskType = "Main", before = 6, after = 8, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":70}]", acceptCost = "[{\"num\":7,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":16,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":7,\"cfgId\":1}]" },
    [8]  = { id = 8, taskType = "Main", before = 7, after = 9, name = "等级提升", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"Lv\",\"update\":\"Replace\",\"target\":5}]", acceptCost = "[{\"num\":8,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":17,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":8,\"cfgId\":1}]" },
    [9]  = { id = 9, taskType = "Main", before = 8, after = 10, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":100}]", acceptCost = "[{\"num\":9,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":18,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":9,\"cfgId\":1}]" },
    [10] = { id = 10, taskType = "Main", before = 9, after = 11, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":150}]", acceptCost = "[{\"num\":10,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":19,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}]" },
    [11] = { id = 11, taskType = "Main", before = 10, after = 12, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":200}]", acceptCost = "[{\"num\":11,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":20,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":11,\"cfgId\":1}]" },
    [12] = { id = 12, taskType = "Main", before = 11, after = 13, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":250}]", acceptCost = "[{\"num\":12,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":21,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":12,\"cfgId\":1}]" },
    [13] = { id = 13, taskType = "Main", before = 12, after = 14, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":300}]", acceptCost = "[{\"num\":13,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":22,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":13,\"cfgId\":1}]" },
    [14] = { id = 14, taskType = "Main", before = 13, after = 15, name = "等级提升", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"Lv\",\"update\":\"Replace\",\"target\":10}]", acceptCost = "[{\"num\":14,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":23,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":14,\"cfgId\":1}]" },
    [15] = { id = 15, taskType = "Main", before = 14, after = 16, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":500}]", acceptCost = "[{\"num\":15,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":24,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":15,\"cfgId\":1}]" },
    [16] = { id = 16, taskType = "Main", before = 15, after = 17, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":800}]", acceptCost = "[{\"num\":16,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":25,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":16,\"cfgId\":1}]" },
    [17] = { id = 17, taskType = "Main", before = 16, after = 18, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1100}]", acceptCost = "[{\"num\":17,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":26,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":17,\"cfgId\":1}]" },
    [18] = { id = 18, taskType = "Main", before = 17, after = 19, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1400}]", acceptCost = "[{\"num\":18,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":27,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":18,\"cfgId\":1}]" },
    [19] = { id = 19, taskType = "Main", before = 18, after = 0, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1700}]", acceptCost = "[{\"num\":19,\"cfgId\":1}]", rewards = "[{\"num\":10,\"cfgId\":10003}, {\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":50,\"cfgId\":100001}, {\"num\":28,\"cfgId\":5}]", submitCost = "[{\"num\":2,\"cfgId\":100001}, {\"num\":2,\"cfgId\":10003}, {\"num\":19,\"cfgId\":1}]" }
}

---@param id string id
---@return QTask 道具配置
function QTaskTable.get(id)
    local cfg = QTaskTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QTask)
end

---@param field string 字段名字
---@param value any 字段值
---@return QTask 道具配置
function QTaskTable.find(field, value)
    for _, v in pairs(QTaskTable) do
        if (v[field] == value) then
            return setmetatable(v, QTask)
        end
    end
    return nil
end

