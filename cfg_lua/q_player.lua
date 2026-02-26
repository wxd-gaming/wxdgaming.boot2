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
[1] = {id = 1, exp = 10, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=100}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=100}" } ,
[2] = {id = 2, exp = 10, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=101}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=101}" } ,
[3] = {id = 3, exp = 10, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=102}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=102}" } ,
[4] = {id = 4, exp = 30, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=103}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=103}" } ,
[5] = {id = 5, exp = 50, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=104}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=104}" } ,
[6] = {id = 6, exp = 70, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=105}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=105}" } ,
[7] = {id = 7, exp = 90, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=106}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=106}" } ,
[8] = {id = 8, exp = 110, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=107}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=107}" } ,
[9] = {id = 9, exp = 130, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=108}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=108}" } ,
[10] = {id = 10, exp = 150, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=109}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=109}" } ,
[11] = {id = 11, exp = 170, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=110}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=110}" } ,
[12] = {id = 12, exp = 190, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=111}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=111}" } ,
[13] = {id = 13, exp = 210, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=112}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=112}" } ,
[14] = {id = 14, exp = 230, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=113}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=113}" } ,
[15] = {id = 15, exp = 250, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=114}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=114}" } ,
[16] = {id = 16, exp = 270, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=115}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=115}" } ,
[17] = {id = 17, exp = 290, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=116}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=116}" } ,
[18] = {id = 18, exp = 310, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=117}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=117}" } ,
[19] = {id = 19, exp = 330, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=118}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=118}" } ,
[20] = {id = 20, exp = 350, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=119}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=119}" } ,
[21] = {id = 21, exp = 370, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=120}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=120}" } ,
[22] = {id = 22, exp = 390, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=121}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=121}" } ,
[23] = {id = 23, exp = 410, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=122}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=122}" } ,
[24] = {id = 24, exp = 430, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=123}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=123}" } ,
[25] = {id = 25, exp = 450, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=124}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=124}" } ,
[26] = {id = 26, exp = 470, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=125}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=125}" } ,
[27] = {id = 27, exp = 490, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=126}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=126}" } ,
[28] = {id = 28, exp = 510, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=127}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=127}" } ,
[29] = {id = 29, exp = 530, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=128}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=128}" } ,
[30] = {id = 30, exp = 550, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=129}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=129}" } ,
[31] = {id = 31, exp = 570, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=130}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=130}" } ,
[32] = {id = 32, exp = 590, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=131}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=131}" } ,
[33] = {id = 33, exp = 610, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=132}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=132}" } ,
[34] = {id = 34, exp = 630, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=133}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=133}" } ,
[35] = {id = 35, exp = 650, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=134}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=134}" } ,
[36] = {id = 36, exp = 670, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=135}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=135}" } ,
[37] = {id = 37, exp = 690, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=136}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=136}" } ,
[38] = {id = 38, exp = 710, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=137}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=137}" } ,
[39] = {id = 39, exp = 730, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=138}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=138}" } ,
[40] = {id = 40, exp = 750, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=139}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=139}" } ,
[41] = {id = 41, exp = 770, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=140}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=140}" } ,
[42] = {id = 42, exp = 790, attr = "{体力=100, MAXHP=10000, MAXMP=10000, 攻击=100, 防御=141}", attrPro = "{体力=100, MAXHP=106, 攻击=100, 防御=141}" } 
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

