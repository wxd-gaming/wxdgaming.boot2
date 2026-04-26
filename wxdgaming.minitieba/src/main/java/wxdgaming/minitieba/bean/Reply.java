package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回复
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    /** 回复ID */
    private long id;
    /** 帖子ID */
    private long postId;
    /** 回复者昵称 */
    private String author;
    /** 回复者用户名 */
    private String username;
    /** 回复者头像 */
    private String avatar;
    /** 用户跟帖序号 */
    private long userReplyId;
    /** 回复内容 */
    private String content;
    /** 回复时间 */
    private long createTime;
    /** 是否匿名 */
    private boolean anonymous;
    /** 引用回复ID（0表示直接回复帖子） */
    private long replyId;

    public Reply(long id, long postId, String author, String username, String content) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.username = username;
        this.content = content;
        this.createTime = System.currentTimeMillis();
    }

}
