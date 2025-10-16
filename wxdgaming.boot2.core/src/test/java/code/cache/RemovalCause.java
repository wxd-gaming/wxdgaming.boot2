package code.cache;

public enum RemovalCause {
    /** 替换 */
    REPLACED,
    /** 过期删除 */
    EXPIRE,
    /** 手动删除 */
    EXPLICIT,
    ;
}
