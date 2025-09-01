--- q_achieve 成就集合
--- src/main/cfg/任务成就.xlsx q_achieve

--- @class QAchieve
---@field id any 主键id
---@field type any 成就类型
---@field name any 成就名称
---@field description any 成就说明
---@field min_lv any 等级
---@field max_lv any 等级
---@field condition any 任务条件
---@field rewards any 任务奖励
QAchieve = {}
QAchieve.__index = QAchieve

---@type table<string, QAchieve>
QAchieveTable = {
[1] = {id = 1, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":1,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":10,\"cfgId\":5}]" } ,
[2] = {id = 2, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":2,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":11,\"cfgId\":5}]" } ,
[3] = {id = 3, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":3,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":12,\"cfgId\":5}]" } ,
[4] = {id = 4, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":4,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":13,\"cfgId\":5}]" } ,
[5] = {id = 5, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":5,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":14,\"cfgId\":5}]" } ,
[6] = {id = 6, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":6,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":15,\"cfgId\":5}]" } ,
[7] = {id = 7, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":7,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":16,\"cfgId\":5}]" } ,
[8] = {id = 8, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":8,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":17,\"cfgId\":5}]" } ,
[9] = {id = 9, type = 1, name = "升级", description = "等级提升至 {} 级", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"Lv\",\"k2\":\"0\",\"k3\":\"0\",\"target\":9,\"update\":\"Max\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":18,\"cfgId\":5}]" } ,
[10] = {id = 10, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":200,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":19,\"cfgId\":5}]" } ,
[11] = {id = 11, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":500,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":20,\"cfgId\":5}]" } ,
[12] = {id = 12, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":800,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":21,\"cfgId\":5}]" } ,
[13] = {id = 13, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":1100,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":22,\"cfgId\":5}]" } ,
[14] = {id = 14, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":1400,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":23,\"cfgId\":5}]" } ,
[15] = {id = 15, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":1700,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":24,\"cfgId\":5}]" } ,
[16] = {id = 16, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":2000,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":25,\"cfgId\":5}]" } ,
[17] = {id = 17, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":2300,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":26,\"cfgId\":5}]" } ,
[18] = {id = 18, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":2600,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":27,\"cfgId\":5}]" } ,
[19] = {id = 19, type = 2, name = "杀怪", description = "击杀 {} 只怪物", min_lv = 1, max_lv = 100, condition = "{\"k1\":\"KillMonster\",\"k2\":\"0\",\"k3\":\"0\",\"target\":2900,\"update\":\"Add\"}", rewards = "[{\"num\":10,\"cfgId\":1}, {\"num\":10,\"cfgId\":3}, {\"num\":28,\"cfgId\":5}]" } 
}

---@param id string id
---@return QAchieve 道具配置
function QAchieveTable.get(id)
    local cfg = QAchieveTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QAchieve)
end

---@param field string 字段名字
---@param value any 字段值
---@return QAchieve 道具配置
function QAchieveTable.find(field, value)
    for _, v in pairs(QAchieveTable) do
        if (v[field] == value) then
            return setmetatable(v, QAchieve)
        end
    end
    return nil
end

