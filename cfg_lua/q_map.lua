--- q_map 怪物表
--- src/main/cfg/地图信息.xlsx q_map

--- @class QMap
---@field id any 主键id
---@field crossType any 1是本服，2是跨服
---@field type any 地图类型1常规，2是副本
---@field name any 怪物名称
---@field min_lv any 等级
---@field max_lv any 等级
---@field buffIds any buff配置
QMap = {}
QMap.__index = QMap

---@type table<string, QMap>
QMapTable = {
[1001] = {id = 1001, crossType = 1, type = 1, name = "新手村", min_lv = 1, max_lv = 10, buffIds = "[1, 2]" } ,
[1002] = {id = 1002, crossType = 1, type = 1, name = "城镇中心", min_lv = 11, max_lv = 20, buffIds = "[1, 2]" } ,
[1003] = {id = 1003, crossType = 1, type = 1, name = "魔幻森林", min_lv = 21, max_lv = 30, buffIds = "[1, 2]" } ,
[1004] = {id = 1004, crossType = 1, type = 1, name = "宠物天堂", min_lv = 31, max_lv = 40, buffIds = "[1, 2]" } ,
[1005] = {id = 1005, crossType = 1, type = 1, name = "永恒大陆", min_lv = 41, max_lv = 50, buffIds = "[1, 2]" } ,
[1006] = {id = 1006, crossType = 1, type = 1, name = "暗之神殿", min_lv = 51, max_lv = 60, buffIds = "[1, 2]" } ,
[1007] = {id = 1007, crossType = 1, type = 1, name = "神魔大殿", min_lv = 61, max_lv = 70, buffIds = "[1, 2]" } 
}

---@param id string id
---@return QMap 道具配置
function QMapTable.get(id)
    local cfg = QMapTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QMap)
end

---@param field string 字段名字
---@param value any 字段值
---@return QMap 道具配置
function QMapTable.find(field, value)
    for _, v in pairs(QMapTable) do
        if (v[field] == value) then
            return setmetatable(v, QMap)
        end
    end
    return nil
end

