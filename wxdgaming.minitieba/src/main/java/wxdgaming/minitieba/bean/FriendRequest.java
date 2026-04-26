package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 好友申请
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {

    /** 申请ID */
    private long id;
    /** 申请人 */
    private String fromUser;
    /** 申请人昵称 */
    private String fromNickname;
    /** 申请人头像 */
    private String fromAvatar;
    /** 被申请人 */
    private String toUser;
    /** 申请时间 */
    private long createTime;
    /** 状态: pending(待处理), accepted(已同意), rejected(已拒绝) */
    private String status;

    public FriendRequest(long id, String fromUser, String fromNickname, String toUser) {
        this.id = id;
        this.fromUser = fromUser;
        this.fromNickname = fromNickname;
        this.toUser = toUser;
        this.status = "pending";
        this.createTime = System.currentTimeMillis();
    }
}
