package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户点赞/点踩记录
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLike {

    /** 用户名 */
    private String username;
    /** 帖子ID */
    private long postId;
    /** 类型: like=点赞, dislike=点踩 */
    private String type;

}
