--- q_activity 活动
--- src/main/cfg/活动.xlsx q_activity

--- @class QActivity
---@field id any 活动流水号
---@field type any 活动类型id
---@field name any 名称
---@field poll any 是否循环
---@field validation any 限制条件
---@field openTime any 开启时间
QActivity = {}
QActivity.__index = QActivity

---@type table<string, QActivity>
QActivityTable = {
[1001] = {id = 1001, type = 1, name = "活动1", poll = 1, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "CurrentDayMin#0&CurrentDayMax#0" } ,
[1002] = {id = 1002, type = 2, name = "活动2", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } ,
[1003] = {id = 1003, type = 2, name = "活动3", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } ,
[1004] = {id = 1004, type = 2, name = "活动4", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } ,
[1005] = {id = 1005, type = 2, name = "活动5", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } ,
[1006] = {id = 1006, type = 2, name = "活动6", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } ,
[1007] = {id = 1007, type = 2, name = "活动7", poll = 0, validation = "OpenDay|gte|1;OpenDay|lte|999", openTime = "HHmmss#200000&minute#30" } 
}

---@param id string id
---@return QActivity 道具配置
function QActivityTable.get(id)
    local cfg = QActivityTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QActivity)
end

---@param field string 字段名字
---@param value any 字段值
---@return QActivity 道具配置
function QActivityTable.find(field, value)
    for _, v in pairs(QActivityTable) do
        if (v[field] == value) then
            return setmetatable(v, QActivity)
        end
    end
    return nil
end

