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
[1] = {id = 1, taskType = "Main", before = 0, after = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":10}]", acceptCost = "", rewards = "1|10,3|10,5|10", submitCost = "" } ,
[2] = {id = 2, taskType = "Main", before = 1, after = 3, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":20}]", acceptCost = "1|1", rewards = "1|10,3|10,5|11", submitCost = "1|1" } ,
[3] = {id = 3, taskType = "Main", before = 2, after = 4, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":30}]", acceptCost = "1|2", rewards = "1|10,3|10,5|12", submitCost = "1|2" } ,
[4] = {id = 4, taskType = "Main", before = 3, after = 5, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":40}]", acceptCost = "1|3", rewards = "1|10,3|10,5|13", submitCost = "1|3" } ,
[5] = {id = 5, taskType = "Main", before = 4, after = 6, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":50}]", acceptCost = "1|4", rewards = "1|10,3|10,5|14", submitCost = "1|4" } ,
[6] = {id = 6, taskType = "Main", before = 5, after = 7, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":60}]", acceptCost = "1|5", rewards = "1|10,3|10,5|15", submitCost = "1|5" } ,
[7] = {id = 7, taskType = "Main", before = 6, after = 8, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":70}]", acceptCost = "1|6", rewards = "1|10,3|10,5|16", submitCost = "1|6" } ,
[8] = {id = 8, taskType = "Main", before = 7, after = 9, name = "等级提升", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"Lv\",\"update\":\"Replace\",\"target\":5}]", acceptCost = "1|7", rewards = "1|10,3|10,5|17", submitCost = "1|7" } ,
[9] = {id = 9, taskType = "Main", before = 8, after = 10, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":100}]", acceptCost = "1|8", rewards = "1|10,3|10,5|18", submitCost = "1|8" } ,
[10] = {id = 10, taskType = "Main", before = 9, after = 11, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":150}]", acceptCost = "1|9", rewards = "1|10,3|10,5|19", submitCost = "1|9" } ,
[11] = {id = 11, taskType = "Main", before = 10, after = 12, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":200}]", acceptCost = "1|10", rewards = "1|10,3|10,5|20", submitCost = "1|10" } ,
[12] = {id = 12, taskType = "Main", before = 11, after = 13, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":250}]", acceptCost = "1|11", rewards = "1|10,3|10,5|21", submitCost = "1|11" } ,
[13] = {id = 13, taskType = "Main", before = 12, after = 14, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":300}]", acceptCost = "1|12", rewards = "1|10,3|10,5|22", submitCost = "1|12" } ,
[14] = {id = 14, taskType = "Main", before = 13, after = 15, name = "等级提升", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"Lv\",\"update\":\"Replace\",\"target\":10}]", acceptCost = "1|13", rewards = "1|10,3|10,5|23", submitCost = "1|13" } ,
[15] = {id = 15, taskType = "Main", before = 14, after = 16, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":500}]", acceptCost = "1|14", rewards = "1|10,3|10,5|24", submitCost = "1|14" } ,
[16] = {id = 16, taskType = "Main", before = 15, after = 17, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":800}]", acceptCost = "1|15", rewards = "1|10,3|10,5|25", submitCost = "1|15" } ,
[17] = {id = 17, taskType = "Main", before = 16, after = 18, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1100}]", acceptCost = "1|16", rewards = "1|10,3|10,5|26", submitCost = "1|16" } ,
[18] = {id = 18, taskType = "Main", before = 17, after = 19, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1400}]", acceptCost = "1|17", rewards = "1|10,3|10,5|27", submitCost = "1|17" } ,
[19] = {id = 19, taskType = "Main", before = 18, after = 0, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, conditionList = "[{\"k1\":\"KillMonster\",\"update\":\"Add\",\"target\":1700}]", acceptCost = "1|18", rewards = "1|10,3|10,5|28", submitCost = "1|18" } 
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

