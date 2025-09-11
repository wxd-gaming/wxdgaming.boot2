--- q_monster 怪物表
--- src/main/cfg/怪物信息.xlsx q_monster

--- @class QMonster
---@field id any 主键id
---@field name any 怪物名称
---@field mapId any 关联的地图
---@field lv any 等级
---@field exp any 获得经验
---@field attr any 属性
---@field attrPro any 属性
---@field rewards any 击杀奖励
QMonster = {}
QMonster.__index = QMonster

---@type table<string, QMonster>
QMonsterTable = {
[1] = {id = 1, name = "蚂蚁", mapId = 1001, lv = 1, exp = 2, attr = "{MAXHP=10000, 攻击=100, 防御=100, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=100, 体力=100}", rewards = "1|10,3|10,5|10" } ,
[2] = {id = 2, name = "蜜蜂", mapId = 1001, lv = 2, exp = 3, attr = "{MAXHP=10000, 攻击=100, 防御=101, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=101, 体力=100}", rewards = "1|10,3|10,5|11" } ,
[3] = {id = 3, name = "蟑螂", mapId = 1001, lv = 3, exp = 4, attr = "{MAXHP=10000, 攻击=100, 防御=102, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=102, 体力=100}", rewards = "1|10,3|10,5|12" } ,
[4] = {id = 4, name = "黄雀", mapId = 1001, lv = 4, exp = 5, attr = "{MAXHP=10000, 攻击=100, 防御=103, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=103, 体力=100}", rewards = "1|10,3|10,5|13" } ,
[5] = {id = 5, name = "兔子", mapId = 1002, lv = 5, exp = 6, attr = "{MAXHP=10000, 攻击=100, 防御=104, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=104, 体力=100}", rewards = "1|10,3|10,5|14" } ,
[6] = {id = 6, name = "公鸡", mapId = 1002, lv = 6, exp = 7, attr = "{MAXHP=10000, 攻击=100, 防御=105, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=105, 体力=100}", rewards = "1|10,3|10,5|15" } ,
[7] = {id = 7, name = "野狗", mapId = 1002, lv = 7, exp = 8, attr = "{MAXHP=10000, 攻击=100, 防御=106, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=106, 体力=100}", rewards = "1|10,3|10,5|16" } ,
[8] = {id = 8, name = "小软", mapId = 1003, lv = 8, exp = 9, attr = "{MAXHP=10000, 攻击=100, 防御=107, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=107, 体力=100}", rewards = "1|10,3|10,5|17" } ,
[9] = {id = 9, name = "猎豹", mapId = 1003, lv = 9, exp = 10, attr = "{MAXHP=10000, 攻击=100, 防御=108, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=108, 体力=100}", rewards = "1|10,3|10,5|18" } ,
[10] = {id = 10, name = "老虎", mapId = 1003, lv = 10, exp = 11, attr = "{MAXHP=10000, 攻击=100, 防御=109, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=109, 体力=100}", rewards = "1|10,3|10,5|19" } ,
[11] = {id = 11, name = "蟒蛇", mapId = 1004, lv = 11, exp = 12, attr = "{MAXHP=10000, 攻击=100, 防御=110, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=110, 体力=100}", rewards = "1|10,3|10,5|20" } ,
[12] = {id = 12, name = "河马", mapId = 1004, lv = 12, exp = 13, attr = "{MAXHP=10000, 攻击=100, 防御=111, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=111, 体力=100}", rewards = "1|10,3|10,5|21" } ,
[13] = {id = 13, name = "鳄鱼", mapId = 1004, lv = 13, exp = 14, attr = "{MAXHP=10000, 攻击=100, 防御=112, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=112, 体力=100}", rewards = "1|10,3|10,5|22" } ,
[14] = {id = 14, name = "猛犸象", mapId = 1005, lv = 14, exp = 15, attr = "{MAXHP=10000, 攻击=100, 防御=113, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=113, 体力=100}", rewards = "1|10,3|10,5|23" } ,
[15] = {id = 15, name = "骷髅头", mapId = 1005, lv = 15, exp = 16, attr = "{MAXHP=10000, 攻击=100, 防御=114, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=114, 体力=100}", rewards = "1|10,3|10,5|24" } ,
[16] = {id = 16, name = "术士", mapId = 1006, lv = 16, exp = 17, attr = "{MAXHP=10000, 攻击=100, 防御=115, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=115, 体力=100}", rewards = "1|10,3|10,5|25" } ,
[17] = {id = 17, name = "猎人", mapId = 1006, lv = 17, exp = 18, attr = "{MAXHP=10000, 攻击=100, 防御=116, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=116, 体力=100}", rewards = "1|10,3|10,5|26" } ,
[18] = {id = 18, name = "刺客", mapId = 1007, lv = 18, exp = 19, attr = "{MAXHP=10000, 攻击=100, 防御=117, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=117, 体力=100}", rewards = "1|10,3|10,5|27" } ,
[19] = {id = 19, name = "呆呆贼", mapId = 1007, lv = 19, exp = 20, attr = "{MAXHP=10000, 攻击=100, 防御=118, 体力=100}", attrPro = "{MAXHP=100, 攻击=100, 防御=118, 体力=100}", rewards = "1|10,3|10,5|28" } 
}

---@param id string id
---@return QMonster 道具配置
function QMonsterTable.get(id)
    local cfg = QMonsterTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QMonster)
end

---@param field string 字段名字
---@param value any 字段值
---@return QMonster 道具配置
function QMonsterTable.find(field, value)
    for _, v in pairs(QMonsterTable) do
        if (v[field] == value) then
            return setmetatable(v, QMonster)
        end
    end
    return nil
end

