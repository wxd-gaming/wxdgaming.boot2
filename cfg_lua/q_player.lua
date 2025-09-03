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
[1] = {id = 1, exp = 10, attr = "{防御=100, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=100, 体力=100, MAXHP=106, 攻击=100}" } ,
[2] = {id = 2, exp = 10, attr = "{防御=101, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=101, 体力=100, MAXHP=106, 攻击=100}" } ,
[3] = {id = 3, exp = 10, attr = "{防御=102, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=102, 体力=100, MAXHP=106, 攻击=100}" } ,
[4] = {id = 4, exp = 30, attr = "{防御=103, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=103, 体力=100, MAXHP=106, 攻击=100}" } ,
[5] = {id = 5, exp = 50, attr = "{防御=104, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=104, 体力=100, MAXHP=106, 攻击=100}" } ,
[6] = {id = 6, exp = 70, attr = "{防御=105, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=105, 体力=100, MAXHP=106, 攻击=100}" } ,
[7] = {id = 7, exp = 90, attr = "{防御=106, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=106, 体力=100, MAXHP=106, 攻击=100}" } ,
[8] = {id = 8, exp = 110, attr = "{防御=107, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=107, 体力=100, MAXHP=106, 攻击=100}" } ,
[9] = {id = 9, exp = 130, attr = "{防御=108, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=108, 体力=100, MAXHP=106, 攻击=100}" } ,
[10] = {id = 10, exp = 150, attr = "{防御=109, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=109, 体力=100, MAXHP=106, 攻击=100}" } ,
[11] = {id = 11, exp = 170, attr = "{防御=110, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=110, 体力=100, MAXHP=106, 攻击=100}" } ,
[12] = {id = 12, exp = 190, attr = "{防御=111, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=111, 体力=100, MAXHP=106, 攻击=100}" } ,
[13] = {id = 13, exp = 210, attr = "{防御=112, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=112, 体力=100, MAXHP=106, 攻击=100}" } ,
[14] = {id = 14, exp = 230, attr = "{防御=113, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=113, 体力=100, MAXHP=106, 攻击=100}" } ,
[15] = {id = 15, exp = 250, attr = "{防御=114, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=114, 体力=100, MAXHP=106, 攻击=100}" } ,
[16] = {id = 16, exp = 270, attr = "{防御=115, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=115, 体力=100, MAXHP=106, 攻击=100}" } ,
[17] = {id = 17, exp = 290, attr = "{防御=116, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=116, 体力=100, MAXHP=106, 攻击=100}" } ,
[18] = {id = 18, exp = 310, attr = "{防御=117, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=117, 体力=100, MAXHP=106, 攻击=100}" } ,
[19] = {id = 19, exp = 330, attr = "{防御=118, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=118, 体力=100, MAXHP=106, 攻击=100}" } ,
[20] = {id = 20, exp = 350, attr = "{防御=119, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=119, 体力=100, MAXHP=106, 攻击=100}" } ,
[21] = {id = 21, exp = 370, attr = "{防御=120, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=120, 体力=100, MAXHP=106, 攻击=100}" } ,
[22] = {id = 22, exp = 390, attr = "{防御=121, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=121, 体力=100, MAXHP=106, 攻击=100}" } ,
[23] = {id = 23, exp = 410, attr = "{防御=122, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=122, 体力=100, MAXHP=106, 攻击=100}" } ,
[24] = {id = 24, exp = 430, attr = "{防御=123, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=123, 体力=100, MAXHP=106, 攻击=100}" } ,
[25] = {id = 25, exp = 450, attr = "{防御=124, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=124, 体力=100, MAXHP=106, 攻击=100}" } ,
[26] = {id = 26, exp = 470, attr = "{防御=125, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=125, 体力=100, MAXHP=106, 攻击=100}" } ,
[27] = {id = 27, exp = 490, attr = "{防御=126, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=126, 体力=100, MAXHP=106, 攻击=100}" } ,
[28] = {id = 28, exp = 510, attr = "{防御=127, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=127, 体力=100, MAXHP=106, 攻击=100}" } ,
[29] = {id = 29, exp = 530, attr = "{防御=128, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=128, 体力=100, MAXHP=106, 攻击=100}" } ,
[30] = {id = 30, exp = 550, attr = "{防御=129, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=129, 体力=100, MAXHP=106, 攻击=100}" } ,
[31] = {id = 31, exp = 570, attr = "{防御=130, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=130, 体力=100, MAXHP=106, 攻击=100}" } ,
[32] = {id = 32, exp = 590, attr = "{防御=131, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=131, 体力=100, MAXHP=106, 攻击=100}" } ,
[33] = {id = 33, exp = 610, attr = "{防御=132, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=132, 体力=100, MAXHP=106, 攻击=100}" } ,
[34] = {id = 34, exp = 630, attr = "{防御=133, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=133, 体力=100, MAXHP=106, 攻击=100}" } ,
[35] = {id = 35, exp = 650, attr = "{防御=134, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=134, 体力=100, MAXHP=106, 攻击=100}" } ,
[36] = {id = 36, exp = 670, attr = "{防御=135, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=135, 体力=100, MAXHP=106, 攻击=100}" } ,
[37] = {id = 37, exp = 690, attr = "{防御=136, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=136, 体力=100, MAXHP=106, 攻击=100}" } ,
[38] = {id = 38, exp = 710, attr = "{防御=137, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=137, 体力=100, MAXHP=106, 攻击=100}" } ,
[39] = {id = 39, exp = 730, attr = "{防御=138, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=138, 体力=100, MAXHP=106, 攻击=100}" } ,
[40] = {id = 40, exp = 750, attr = "{防御=139, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=139, 体力=100, MAXHP=106, 攻击=100}" } ,
[41] = {id = 41, exp = 770, attr = "{防御=140, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=140, 体力=100, MAXHP=106, 攻击=100}" } ,
[42] = {id = 42, exp = 790, attr = "{防御=141, 体力=100, MAXHP=10000, MAXMP=10000, 攻击=100}", attrPro = "{防御=141, 体力=100, MAXHP=106, 攻击=100}" } 
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

