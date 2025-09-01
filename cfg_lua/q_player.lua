--- q_player 怪物表
--- src/main/cfg/玩家信息.xlsx q_player

--- @class QPlayer
---@field id any 主键id/等级
---@field exp any 升级所需要的经验值
---@field attr any 属性
---@field attrPro any 属性
QPlayer = {}
QPlayer.__index = QPlayer

---@type table<string, QPlayer>
QPlayerTable = {
[1] = {id = 1, exp = 10, attr = "{MAXMP=10000, 防御=100, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=100, 攻击=100, 体力=100, MAXHP=106}" } ,
[2] = {id = 2, exp = 10, attr = "{MAXMP=10000, 防御=101, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=101, 攻击=100, 体力=100, MAXHP=106}" } ,
[3] = {id = 3, exp = 10, attr = "{MAXMP=10000, 防御=102, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=102, 攻击=100, 体力=100, MAXHP=106}" } ,
[4] = {id = 4, exp = 30, attr = "{MAXMP=10000, 防御=103, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=103, 攻击=100, 体力=100, MAXHP=106}" } ,
[5] = {id = 5, exp = 50, attr = "{MAXMP=10000, 防御=104, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=104, 攻击=100, 体力=100, MAXHP=106}" } ,
[6] = {id = 6, exp = 70, attr = "{MAXMP=10000, 防御=105, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=105, 攻击=100, 体力=100, MAXHP=106}" } ,
[7] = {id = 7, exp = 90, attr = "{MAXMP=10000, 防御=106, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=106, 攻击=100, 体力=100, MAXHP=106}" } ,
[8] = {id = 8, exp = 110, attr = "{MAXMP=10000, 防御=107, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=107, 攻击=100, 体力=100, MAXHP=106}" } ,
[9] = {id = 9, exp = 130, attr = "{MAXMP=10000, 防御=108, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=108, 攻击=100, 体力=100, MAXHP=106}" } ,
[10] = {id = 10, exp = 150, attr = "{MAXMP=10000, 防御=109, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=109, 攻击=100, 体力=100, MAXHP=106}" } ,
[11] = {id = 11, exp = 170, attr = "{MAXMP=10000, 防御=110, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=110, 攻击=100, 体力=100, MAXHP=106}" } ,
[12] = {id = 12, exp = 190, attr = "{MAXMP=10000, 防御=111, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=111, 攻击=100, 体力=100, MAXHP=106}" } ,
[13] = {id = 13, exp = 210, attr = "{MAXMP=10000, 防御=112, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=112, 攻击=100, 体力=100, MAXHP=106}" } ,
[14] = {id = 14, exp = 230, attr = "{MAXMP=10000, 防御=113, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=113, 攻击=100, 体力=100, MAXHP=106}" } ,
[15] = {id = 15, exp = 250, attr = "{MAXMP=10000, 防御=114, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=114, 攻击=100, 体力=100, MAXHP=106}" } ,
[16] = {id = 16, exp = 270, attr = "{MAXMP=10000, 防御=115, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=115, 攻击=100, 体力=100, MAXHP=106}" } ,
[17] = {id = 17, exp = 290, attr = "{MAXMP=10000, 防御=116, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=116, 攻击=100, 体力=100, MAXHP=106}" } ,
[18] = {id = 18, exp = 310, attr = "{MAXMP=10000, 防御=117, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=117, 攻击=100, 体力=100, MAXHP=106}" } ,
[19] = {id = 19, exp = 330, attr = "{MAXMP=10000, 防御=118, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=118, 攻击=100, 体力=100, MAXHP=106}" } ,
[20] = {id = 20, exp = 350, attr = "{MAXMP=10000, 防御=119, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=119, 攻击=100, 体力=100, MAXHP=106}" } ,
[21] = {id = 21, exp = 370, attr = "{MAXMP=10000, 防御=120, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=120, 攻击=100, 体力=100, MAXHP=106}" } ,
[22] = {id = 22, exp = 390, attr = "{MAXMP=10000, 防御=121, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=121, 攻击=100, 体力=100, MAXHP=106}" } ,
[23] = {id = 23, exp = 410, attr = "{MAXMP=10000, 防御=122, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=122, 攻击=100, 体力=100, MAXHP=106}" } ,
[24] = {id = 24, exp = 430, attr = "{MAXMP=10000, 防御=123, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=123, 攻击=100, 体力=100, MAXHP=106}" } ,
[25] = {id = 25, exp = 450, attr = "{MAXMP=10000, 防御=124, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=124, 攻击=100, 体力=100, MAXHP=106}" } ,
[26] = {id = 26, exp = 470, attr = "{MAXMP=10000, 防御=125, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=125, 攻击=100, 体力=100, MAXHP=106}" } ,
[27] = {id = 27, exp = 490, attr = "{MAXMP=10000, 防御=126, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=126, 攻击=100, 体力=100, MAXHP=106}" } ,
[28] = {id = 28, exp = 510, attr = "{MAXMP=10000, 防御=127, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=127, 攻击=100, 体力=100, MAXHP=106}" } ,
[29] = {id = 29, exp = 530, attr = "{MAXMP=10000, 防御=128, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=128, 攻击=100, 体力=100, MAXHP=106}" } ,
[30] = {id = 30, exp = 550, attr = "{MAXMP=10000, 防御=129, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=129, 攻击=100, 体力=100, MAXHP=106}" } ,
[31] = {id = 31, exp = 570, attr = "{MAXMP=10000, 防御=130, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=130, 攻击=100, 体力=100, MAXHP=106}" } ,
[32] = {id = 32, exp = 590, attr = "{MAXMP=10000, 防御=131, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=131, 攻击=100, 体力=100, MAXHP=106}" } ,
[33] = {id = 33, exp = 610, attr = "{MAXMP=10000, 防御=132, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=132, 攻击=100, 体力=100, MAXHP=106}" } ,
[34] = {id = 34, exp = 630, attr = "{MAXMP=10000, 防御=133, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=133, 攻击=100, 体力=100, MAXHP=106}" } ,
[35] = {id = 35, exp = 650, attr = "{MAXMP=10000, 防御=134, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=134, 攻击=100, 体力=100, MAXHP=106}" } ,
[36] = {id = 36, exp = 670, attr = "{MAXMP=10000, 防御=135, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=135, 攻击=100, 体力=100, MAXHP=106}" } ,
[37] = {id = 37, exp = 690, attr = "{MAXMP=10000, 防御=136, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=136, 攻击=100, 体力=100, MAXHP=106}" } ,
[38] = {id = 38, exp = 710, attr = "{MAXMP=10000, 防御=137, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=137, 攻击=100, 体力=100, MAXHP=106}" } ,
[39] = {id = 39, exp = 730, attr = "{MAXMP=10000, 防御=138, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=138, 攻击=100, 体力=100, MAXHP=106}" } ,
[40] = {id = 40, exp = 750, attr = "{MAXMP=10000, 防御=139, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=139, 攻击=100, 体力=100, MAXHP=106}" } ,
[41] = {id = 41, exp = 770, attr = "{MAXMP=10000, 防御=140, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=140, 攻击=100, 体力=100, MAXHP=106}" } ,
[42] = {id = 42, exp = 790, attr = "{MAXMP=10000, 防御=141, 攻击=100, 体力=100, MAXHP=10000}", attrPro = "{防御=141, 攻击=100, 体力=100, MAXHP=106}" } 
}

---@param id string id
---@return QPlayer 道具配置
function QPlayerTable.get(id)
    local cfg = QPlayerTable[id]
    if (cfg == nil) then
        return nil
    end
    return setmetatable(cfg, QPlayer)
end

---@param field string 字段名字
---@param value any 字段值
---@return QPlayer 道具配置
function QPlayerTable.find(field, value)
    for _, v in pairs(QPlayerTable) do
        if (v[field] == value) then
            return setmetatable(v, QPlayer)
        end
    end
    return nil
end

