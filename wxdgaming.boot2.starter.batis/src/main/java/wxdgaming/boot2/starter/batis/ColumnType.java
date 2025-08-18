package wxdgaming.boot2.starter.batis;


import lombok.Getter;

/**
 * 数据类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:50
 **/
@Getter
public enum ColumnType {
    None("默认值"),
    Bool("布尔值"),
    Byte("字节"),
    Short("短整数"),
    Int("整数"),
    Long("长整数"),
    Float("浮点数"),
    Double("双精度浮点数"),
    String("字符串"),
    Blob("二进制"),
    Json("JSON"),
    Jsonb("JSONB"),
    ;

    private final String comment;

    ColumnType(String comment) {
        this.comment = comment;
    }

}