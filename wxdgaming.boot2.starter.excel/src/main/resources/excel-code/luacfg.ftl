---
---

---@class ItemCfg
---@field id number id
---@field name number 名字
ItemCfg = {}
ItemCfg.__index = ItemCfg

---@type table<string, ItemCfg>
ItemCfgTable = {
[1] = { id = 1, name = 1 },
[2] = { id = 2, name = 2 }
}

---@param id string id
---@return ItemCfg 道具配置
function ItemCfgTable.get(id)
local cfg = ItemCfgTable[id]
if (cfg == nil) then
return nil
end
return setmetatable(cfg, ItemCfg)
end

---@param field string 字段名字
---@param value any 字段值
---@return ItemCfg 道具配置
function ItemCfgTable.find(field, value)
for _, v in pairs(ItemCfgTable) do
if (v[field] == value) then
return setmetatable(v, ItemCfg)
end
end
return setmetatable(cfg, ItemCfg)
end
