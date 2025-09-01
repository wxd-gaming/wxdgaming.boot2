--- q_item 任务集合
--- src/main/cfg/道具.xlsx q_item

--- @class QItem
---@field id any 主键id
---@field itemType any 道具类型类型
---@field itemSubType any 道具类型类型
---@field name any 名字
---@field description any 说明
---@field lv any 道具的等级
---@field maxCount any 叠加上限，0表示无限制
---@field param1 any 附加参数
---@field param2 any 附加参数
---@field param3 any 附加参数
---@field param4 any 附加参数如果是装备这个位置是穿戴部位
---@field attr any 属性
QItem = {}
QItem.__index = QItem

---@type table<string, QItem>
QItemTable = {
[1] = {id = 1, itemType = 1, itemSubType = 1, name = "钻石", description = "钻石", lv = 0, maxCount = 0, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } ,
[2] = {id = 2, itemType = 1, itemSubType = 2, name = "绑定钻石", description = "绑定钻石", lv = 0, maxCount = 0, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } ,
[3] = {id = 3, itemType = 1, itemSubType = 3, name = "金币", description = "金币", lv = 0, maxCount = 0, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } ,
[4] = {id = 4, itemType = 1, itemSubType = 4, name = "绑定金币", description = "绑定金币", lv = 0, maxCount = 0, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } ,
[5] = {id = 5, itemType = 1, itemSubType = 5, name = "经验值", description = "经验值", lv = 0, maxCount = 0, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } ,
[10001] = {id = 10001, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 1, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=100, 攻击=100, 体力=100, MAXHP=106}" } ,
[10002] = {id = 10002, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 2, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=101, 攻击=100, 体力=100, MAXHP=106}" } ,
[10003] = {id = 10003, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 3, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=102, 攻击=100, 体力=100, MAXHP=106}" } ,
[10004] = {id = 10004, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 4, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=103, 攻击=100, 体力=100, MAXHP=106}" } ,
[10005] = {id = 10005, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 5, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=104, 攻击=100, 体力=100, MAXHP=106}" } ,
[10006] = {id = 10006, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 6, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=105, 攻击=100, 体力=100, MAXHP=106}" } ,
[10007] = {id = 10007, itemType = 2, itemSubType = 0, name = "武器", description = "武器", lv = 7, maxCount = 1, param1 = 0, param2 = 0, param3 = 0, param4 = 1, attr = "{防御=10666, 攻击=10609, 体力=100, MAXHP=106}" } ,
[100001] = {id = 100001, itemType = 10, itemSubType = 0, name = "强化石", description = "强化石", lv = 0, maxCount = 99, param1 = 0, param2 = 0, param3 = 0, param4 = 0, attr = nil } 
}

---@param id string id
---@return QItem 道具配置
function QItemTable.get(id)
    local cfg = QItemTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QItem)
end

---@param field string 字段名字
---@param value any 字段值
---@return QItem 道具配置
function QItemTable.find(field, value)
    for _, v in pairs(QItemTable) do
        if (v[field] == value) then
            return setmetatable(v, QItem)
        end
    end
    return nil
end

