--- q_buff buff
--- src/main/cfg/buff.xlsx q_buff

--- @class QBuff
---@field id any 主键id 唯一id
---@field buffGroup any buff分组
---@field buffId any buff的id
---@field lv any 等级
---@field type any 类型
---@field addType any 添加的操作，0是覆盖，1是如果已经有忽略，2叠加层级
---@field addExecutor any 是否在添加buff立即执行一次
---@field addStatusList any 获得buff添加的状态
---@field clearType any buff的清理机制，死亡删除，切换地图删除，下线删除
---@field checkGroup any 添加buff的时候检查是部分保护其他buff
---@field clearBuffIdList any 添加buff 的时候删除的其他的buff
---@field clearGroupList any 添加buff 的时候删除的其他的buff
---@field duration any 持续时间
---@field interval any 间隔时间
---@field paramInt1 any 参数1
---@field paramInt2 any 参数2
---@field paramInt3 any 参数3
---@field paramString1 any 特殊配置1
---@field paramString2 any 特殊配置2
---@field paramString3 any 特殊配置3
QBuff = {}
QBuff.__index = QBuff

---@type table<string, QBuff>
QBuffTable = {
["1-1"] = {id = "1-1", buffGroup = 1, buffId = 1, lv = 1, type = 1, addType = 0, addExecutor = false, addStatusList = "[]", clearType = 0, checkGroup = "[]", clearBuffIdList = "[]", clearGroupList = "[]", duration = 30000, interval = 100, paramInt1 = 0, paramInt2 = 0, paramInt3 = 0, paramString1 = "{\"MP\":10,\"HP\":10}", paramString2 = nil, paramString3 = nil } ,
["2-1"] = {id = "2-1", buffGroup = 1, buffId = 2, lv = 1, type = 1, addType = 0, addExecutor = false, addStatusList = "[]", clearType = 0, checkGroup = "[]", clearBuffIdList = "[]", clearGroupList = "[]", duration = 30000, interval = 1000, paramInt1 = 1, paramInt2 = 0, paramInt3 = 0, paramString1 = "{\"MP\":1000,\"HP\":1000}", paramString2 = nil, paramString3 = nil } ,
["3-1"] = {id = "3-1", buffGroup = 1, buffId = 3, lv = 1, type = 1, addType = 0, addExecutor = false, addStatusList = "[]", clearType = 0, checkGroup = "[]", clearBuffIdList = "[]", clearGroupList = "[]", duration = 30000, interval = 1000, paramInt1 = 0, paramInt2 = 0, paramInt3 = 0, paramString1 = "{\"MP\":-40,\"HP\":-40}", paramString2 = nil, paramString3 = nil } ,
["4-1"] = {id = "4-1", buffGroup = 1, buffId = 4, lv = 1, type = 2, addType = 0, addExecutor = false, addStatusList = nil, clearType = 0, checkGroup = nil, clearBuffIdList = nil, clearGroupList = nil, duration = 30000, interval = 1000, paramInt1 = 0, paramInt2 = 0, paramInt3 = 0, paramString1 = "{\"体力\":100,\"MAXHP\":1000,\"MAXMP\":1000,\"攻击\":100,\"防御\":100}", paramString2 = nil, paramString3 = nil } 
}

---@param id string id
---@return QBuff 道具配置
function QBuffTable.get(id)
    local cfg = QBuffTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QBuff)
end

---@param field string 字段名字
---@param value any 字段值
---@return QBuff 道具配置
function QBuffTable.find(field, value)
    for _, v in pairs(QBuffTable) do
        if (v[field] == value) then
            return setmetatable(v, QBuff)
        end
    end
    return nil
end

