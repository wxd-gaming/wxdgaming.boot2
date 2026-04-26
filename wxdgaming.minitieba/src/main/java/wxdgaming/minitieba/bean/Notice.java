package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知/消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    /** 通知ID */
    private long id;
    /** 被通知的用户（帖子作者） */
    private String targetUser;
    /** 触发通知的用户（点赞/回复的人） */
    private String fromUser;
    /** 触发通知的用户昵称 */
    private String fromNickname;
    /** 通知类型: like(点赞), dislike(点踩), reply(回复) */
    private String type;
    /** 关联的帖子ID */
    private long postId;
    /** 帖子内容摘要（用于显示） */
    private String postContent;
    /** 回复内容（仅reply类型） */
    private String replyContent;
    /** 是否已读 */
    private boolean readed;
    /** 创建时间 */
    private long createTime;

    public Notice(long id, String targetUser, String fromUser, String fromNickname, String type, long postId) {
        this.id = id;
        this.targetUser = targetUser;
        this.fromUser = fromUser;
        this.fromNickname = fromNickname;
        this.type = type;
        this.postId = postId;
        this.readed = false;
        this.createTime = System.currentTimeMillis();
    }
}
