package wxdgaming.minitieba.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    /** 帖子ID */
    private long id;
    /** 作者昵称 */
    private String author;
    /** 作者用户名 */
    private String username;
    /** 用户发帖序号 */
    private long userPostId;
    /** 内容 */
    private String content;
    /** 图片列表 */
    private List<String> images = new ArrayList<>();
    /** 视频链接 */
    private String video;
    /** 点赞数 */
    private int likeCount;
    /** 点踩数 */
    private int dislikeCount;
    /** 回复数 */
    private int replyCount;
    /** 创建时间 */
    private long createTime;
    /** 是否匿名 */
    private boolean anonymous;
    /** 是否私密（仅自己可见） */
    private boolean privated;

    public Post(long id, String author, String username, String content) {
        this.id = id;
        this.author = author;
        this.username = username;
        this.content = content;
        this.createTime = System.currentTimeMillis();
    }

}
