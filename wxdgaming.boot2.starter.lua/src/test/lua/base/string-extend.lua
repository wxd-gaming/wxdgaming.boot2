string.empty = ''
function string.contains(str, subStr)
    return string.find(str, subStr) ~= nil
end

function string.endsWith(str, subStr)
    local len1 = #str
    local len2 = #subStr
    if len1 < len2 then
        return false
    end

    return string.find(str, subStr, len1 - len2 + 1) ~= nil
end

function string.isNullOrEmpty(str)
    return str == nil or str == ""
end

function string.replace(str, oldStr, newStr)
    local i, j = string.find(str, oldStr, 1, true)
    if i and j then
        ---@class ret
        local ret = {}
        local start = 1
        while i and j do
            table.insert(ret, string.sub(str, start, i - 1))
            table.insert(ret, newStr)
            start = j + 1
            i, j = string.find(str, oldStr, start, true)
        end
        table.insert(ret, string.sub(str, start))
        return table.concat(ret)
    end
    return str
end

function string.split(str, sep)
    sep = sep or " "
    local items = {}
    local pattern = string.format("([^%s]+)", sep)
    string.gsub(str, pattern, function(c)
        table.insert(items, c)
    end)
    return items
end

---@param pattern string @例如 '<.->',里面的减号表示尽可能少,例如 abc<fdsafdsa>df<dddd> 分割后返回 abc <fdsafdsa> df <dddd>
---但是 abc<fdd<d>d> 会匹配成 abc <fdd<d> d>
function string.splitPair(str, pattern)
    local items = {}
    --或者 %b<>
    local temp = {}
    for v in string.gmatch(str, pattern) do
        table.insert(temp, v)
    end
    local index = 1
    local tempIndex = 1
    local length = string.len(str)
    while index <= length and tempIndex <= #temp and not string.isNullOrEmpty(str) do
        local i = string.indexOf(str, temp[tempIndex])
        local pre = string.sub(str, 1, i - 1)
        if not string.isNullOrEmpty(pre) then
            table.insert(items, pre)
        end
        table.insert(items, temp[tempIndex])
        index = i + string.len(temp[tempIndex])
        str = string.sub(str, index)
        tempIndex = tempIndex + 1
        index = i + 1
    end
    if not string.isNullOrEmpty(str) then
        table.insert(items, str)
    end
    return items
end
---@return table @abc<fdd<d>d> 会匹配成 abc <fdd<d>d> 完全匹配 left:'<' right:'>'
function string.splitCompletePairMatch(str, left, right)
    local items = {}
    local index = 1
    local length = string.len(str)
    local leftCount = 0
    local preIndex = 1
    while index <= length do
        local c = string.sub(str, index, index)
        if c == left then
            if leftCount == 0 then
                if preIndex <= index - 1 then
                    local pre = string.sub(str, preIndex, index - 1)
                    preIndex = index
                    if not string.isNullOrEmpty(pre) then
                        table.insert(items, pre)
                    end
                end
            end
            leftCount = leftCount + 1
        elseif c == right then
            if leftCount ~= 0 then
                leftCount = leftCount - 1
                if leftCount == 0 then
                    local p = string.sub(str, preIndex, index)
                    preIndex = index + 1
                    table.insert(items, p)
                end
            else
                local p = string.sub(str, preIndex, index)
                preIndex = index + 1
                table.insert(items, p)
            end
        end
        index = index + 1
    end
    if preIndex <= length then
        local p = string.sub(str, preIndex, length)
        table.insert(items, p)
    end
    return items
end

---@return string[] @例如 分隔符/，111/aaa 分割之后是 111 /aaa
function string.splitToArray(str, sep)
    local items = string.splitPair(str, sep)
    local t = {}
    local s = string.empty
    for k, v in pairs(items) do
        if v == sep then
            s = v
        elseif not string.isNullOrEmpty(s) then
            table.insert(t, s .. v)
            s = string.empty
        else
            table.insert(t, v)
        end
    end
    return t
end

---@return string[] @例如 helloWorld 分隔符llo 分割之后是he World
function string.splitByAll(str, sep)
    if type(sep) ~= "string" or #sep <= 0 then
        return
    end
    local index = 1
    local strTb = {}
    while true do
        local pos = string.find(str, sep, index, true)
        if not pos then
            break
        end
        table.insert(strTb, string.sub(str, index, pos - 1))
        index = pos + string.len(sep)
    end
    table.insert(strTb, string.sub(str, index))
    return strTb
end

function string.getChar(str, index)
    return string.sub(str, index, index)
end

function string.startsWith(str, subStr)
    return string.find(str, subStr) == 1
end

local EMPTY_CHARS = " \t\n\r"

function string.trim(str, chars)
    chars = chars or EMPTY_CHARS
    return str:match(string.format("^[%s]*(.-)[%s]*$", chars, chars))
end

function string.trimLeft(str, chars)
    chars = chars or EMPTY_CHARS
    return str:match(string.format("^[%s]*(.*)", chars))
end

function string.trimRight(str, chars)
    chars = chars or EMPTY_CHARS
    return str:match(string.format("(.-)[%s]*$", chars))
end

function string.indexOf(s, pattern, init)
    init = init or 0
    local index = string.find(s, pattern, init, true)
    return index or -1;
end

function string.lastIndexOf(s, pattern)
    local i = s:match(".*" .. pattern .. "()")
    if i == nil then
        return nil
    else
        return i - 1
    end
end

---@return string @顺序匹配是否包含
function string.containsByOrder(str, subStr)
    local len = string.len(str)
    local subLen = string.len(subStr)
    local lenIndex = 1
    local subLenIndex = 1
    while subLenIndex <= subLen and lenIndex <= len do
        local subChar = string.getChar(subStr, subLenIndex)
        local char = string.getChar(str, lenIndex)
        if subChar == char then
            subLenIndex = subLenIndex + 1
            lenIndex = 1
        else
            lenIndex = lenIndex + 1
        end
    end
    if subLenIndex >= subLen then
        return true
    end
    return false
end

function string.insert(str, index, insertStr)
    local pre = string.sub(str, 1, index - 1)
    local tail = string.sub(str, index, -1)
    local createStr = string.format("%s%s%s", pre, insertStr, tail)
    return createStr
end

--获取文件名
function string.getFileName(filename)
    return string.match(filename, ".+/([^/]*%.%w+)$")
end

function string.getFileNameWithoutExtension(filename)
    return string.match(filename, ".+/([^/]*)%.%w+$")
end

--获取扩展名
function string.getExtension(str)
    return str:match(".+%.(%w+)$")
end

---@return string @减少无效匹配的gc分配
function string.gsub_opti(s, pattern, repl, n)
    if string.contains(s, pattern) then
        return string.gsub(s, pattern, repl, n)
    else
        return s
    end

end
---@return string @减少无效匹配的gc分配
function string.gmatch_opti(s, pattern)
    if string.contains(s, pattern) then
        return string.gmatch(s, pattern)
    else
        return nil
    end
end

---@return string @ 将数值转化成百分比的字符
function string.numToPercent(value)
    -- 首先，将value乘以100
    local percentValue = value * 100
    -- 然后，使用string.format保留两位小数，并添加%
    -- %.2f表示保留两位小数的浮点数
    local formattedValue = string.format("%.2f%%", percentValue)
    -- 返回格式化后的字符串
    return formattedValue
end

---@return string @ 判断数值是否大于0 大于0返回key拼接value的字符串加上空格 如果是结尾字符则不加空格
function string.joinVar(str, value, ...)
    local args = table.pack(...)
    if tonumber(value) > 0 then
        if (#args > 0 and args[1] == true) then
            return str .. "：" .. value
        else
            return str .. "：" .. value .. " "
        end
    else
        return ""
    end
end

---@return string @ 一般情况下 通过字符返回属性表
function string.getAttrByStr(str)
    local dt = {}
    local data = string.split(str, "|")
    if data then
        for _, v in ipairs(data) do
            local data2 = string.split(v, "#")
            dt[data2[1]] = data2[2]
        end
    end
    return dt
end

---@return string @ 通过字符返回左右两边数值
function string.LR(str)
    local dt = {}
    local data = string.split(str, "#")
    dt.left = data[1]
    dt.right = data[2]
    return dt
end

---@return number @ 将字符串转化为数字，如果失败返回0
function string.tonumber(str)
    if type(str) == "number" then
        return str
    end
    if string.isNullOrEmpty(str) or not str:match("^%d+$") then
        return 0
    end
    return tonumber(str)
end

--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @ param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {1=2,3=4}
function string.toIntIntMap(str, sep1, sep2)
    local map = {}
    string.putIntIntMap(map, str, sep1, sep2)
    return map
end

--- 把字符串追加到指定的map里面
--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @ param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {1=2,3=4}
function string.putIntIntMap(map, str, sep1, sep2)
    if not string.isNullOrEmpty(str) then
        local data = string.split(str, sep2)
        for _, v in ipairs(data) do
            local data2 = string.split(v, sep1)
            map[tonumber(data2[1])] = tonumber(map[tonumber(data2[1])] or 0) + tonumber(data2[2])
        end
    end
    return map
end

--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @ param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {"1"=2,"3"=4}
function string.toStringIntMap(str, sep1, sep2)
    local map = {}
    string.putStringIntMap(map, str, sep1, sep2)
    return map
end

--- 把字符串追加到指定的map里面
--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @ param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {"1"=2,"3"=4}
function string.putStringIntMap(map, str, sep1, sep2)
    if not string.isNullOrEmpty(str) then
        local data = string.split(str, sep2)
        for _, v in ipairs(data) do
            local data2 = string.split(v, sep1)
            map[data2[1]] = (map[data2[1]] or 0) + tonumber(data2[2])
        end
    end
    return map
end

--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {"1"="2","3"="4"}
function string.toStringStringMap(str, sep1, sep2)
    local map = {}
    string.putStringStringMap(map, str, sep1, sep2)
    return map
end

--- 把字符串追加到指定的map里面
--- @param str string 配置字符串
--- @param sep1 string 内层切割
--- @param sep2 string 外层切割
--- @return table @ 将字符串转化为map，例如："道具id#数量|道具id#数量" 转化为 {"1"="2","3"="4"}
function string.putStringStringMap(map, str, sep1, sep2)
    if not string.isNullOrEmpty(str) then
        local data = string.split(str, sep2)
        for _, v in ipairs(data) do
            local data2 = string.split(v, sep1)
            gameDebug.assertNotNil(map[data2[1]], "重复的key", data2[1])
            map[data2[1]] = data2[2]
        end
    end
    return map
end

---根据职业过滤配置，如果不限制职业配置0
---@param career number 职业，如果不限制职业配置0
---@param sep1 string 内层切割
---@param sep2 string 外层切割 例如："职业#道具id#数量|职业#道具id#数量" 转化为 {1=2,3=4}
function string.toIntIntMap4Career(career, str, sep1, sep2)
    local map = {}
    string.putIntIntMap4Career(map, career, str, sep1, sep2)
    return map
end

---根据职业过滤配置，把字符串追加到指定的map里面 ，如果不限制职业配置0
---@param career number 职业，如果不限制职业配置0
---@param sep1 string 内层切割
---@param sep2 string 外层切割 例如："职业#道具id#数量|职业#道具id#数量" 转化为 {1=2,3=4}
function string.putIntIntMap4Career(map, career, str, sep1, sep2)
    if not string.isNullOrEmpty(str) then
        local data = string.split(str, sep2)
        for _, v in ipairs(data) do
            local data2 = string.split(v, sep1)
            local cfgCareer = tonumber(data2[1])
            if cfgCareer == 0 or cfgCareer == tonumber(career) then
                map[tonumber(data2[2])] = tonumber(map[tonumber(data2[2])] or 0) + tonumber(data2[3])
            end
        end
    end
    return map
end

function string.equalsIgnoreCase(str1, str2)
    return string.lower(str1) == string.lower(str2)
end
