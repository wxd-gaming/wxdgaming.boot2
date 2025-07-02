package wxdgaming.game.core;

/**
 * 原因
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-03 20:28
 **/
public enum Reason {
    /** 未知 */
    UNKNOWN,
    SYSTEM,
    CreateRole,
    GM,
    Heart,
    Buff,
    Login,
    Level,
    /** 使用道具 */
    USE_ITEM,
    /** 使用激活码 */
    USE_CDKEY,
    /** 任务接受 */
    TASK_ACCEPT,
    /** 任务完成 */
    TASK_SUBMIT,
    /** 任务完成 */
    TASK_COMPLETE,
    /** 任务取消 */
    TASK_CANCEL,
    /** 任务失败 */
    TASK_FAIL,
    /** 任务超时 */
    TASK_TIMEOUT,
    /** 任务条件不满足 */
    TASK_CONDITION_NOT_MET,
}
