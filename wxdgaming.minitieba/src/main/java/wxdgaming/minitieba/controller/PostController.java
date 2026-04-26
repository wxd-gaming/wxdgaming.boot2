package wxdgaming.minitieba.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.bean.Notice;
import wxdgaming.minitieba.bean.Post;
import wxdgaming.minitieba.bean.Reply;
import wxdgaming.minitieba.service.NoticeService;
import wxdgaming.minitieba.service.PostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子API
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final NoticeService noticeService;

    public PostController(PostService postService, NoticeService noticeService) {
        this.postService = postService;
        this.noticeService = noticeService;
    }

    /** 发帖 - 需要登录
     * visibility: 0-无限制(公开), 1-仅好友可见, 2-仅自己可见(私密)
     */
    @RequestMapping("/create")
    public RunResult create(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        String nickname = (String) request.getAttribute("currentNickname");
        String author = nickname != null ? nickname : username;
        String content = (String) params.get("content");
        if (content == null || content.isBlank()) {
            return RunResult.fail("内容不能为空");
        }
        if (content.length() > 300) {
            return RunResult.fail("内容不能超过300个字");
        }
        // 过滤HTML和JS标签，防止注入攻击
        content = filterHtml(content);
        if (content.isBlank()) {
            return RunResult.fail("内容不能为空");
        }
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) params.get("images");
        String video = (String) params.get("video");
        boolean anonymous = params.containsKey("anonymous") && Boolean.TRUE.equals(params.get("anonymous"));
        int visibility = 0; // 默认公开
        if (params.containsKey("visibility")) {
            visibility = Integer.parseInt(params.get("visibility").toString());
        }
        Post post = postService.createPost(author, username, content, images, video, anonymous, visibility);
        return RunResult.ok().data(post);
    }

    /** 过滤HTML/JS标签，防止XSS注入 */
    private String filterHtml(String content) {
        if (content == null) return null;
        // 移除script标签及其内容
        content = content.replaceAll("(?i)<script[^>]*>[\\s\\S]*?</script>", "");
        // 移除javascript:协议
        content = content.replaceAll("(?i)javascript:", "");
        // 移除onload/onerror等事件属性
        content = content.replaceAll("(?i)\\s+on\\w+\\s*=", " ");
        // 移除HTML标签
        content = content.replaceAll("<[^>]+>", "");
        // 移除多余的空白字符，保留换行
        content = content.replaceAll("[ \\t]+", " ");
        return content.trim();
    }

    /** 帖子列表 - 无需登录（登录后可见自己的私密帖子） */
    @RequestMapping("/list")
    public RunResult list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        List<Post> posts = postService.listPosts(page, size, username);
        int total = postService.getPostCount();
        Map<String, Object> data = new HashMap<>();
        data.put("posts", posts);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", (total + size - 1) / size);
        return RunResult.ok().data(data);
    }

    /** 帖子详情 - 无需登录，但登录用户可看到点赞状态 */
    @RequestMapping("/detail")
    public RunResult detail(@RequestParam long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        Post post = postService.getPost(id, username);
        if (post == null) {
            return RunResult.fail("帖子不存在或无权查看");
        }
        List<Reply> replies = postService.listReplies(id);
        String likeStatus = "none";
        if (username != null) {
            likeStatus = postService.getUserLikeStatus(username, id);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("post", post);
        data.put("replies", replies);
        data.put("likeStatus", likeStatus);
        return RunResult.ok().data(data);
    }

    /** 回复帖子 - 需要登录 */
    @RequestMapping("/reply")
    public RunResult reply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        String nickname = (String) request.getAttribute("currentNickname");
        String author = nickname != null ? nickname : username;
        long postId = Long.parseLong(params.get("postId").toString());
        String content = (String) params.get("content");
        if (content == null || content.isBlank()) {
            return RunResult.fail("回复内容不能为空");
        }
        // 过滤HTML和JS标签，防止注入攻击
        content = filterHtml(content);
        if (content.isBlank()) {
            return RunResult.fail("回复内容不能为空");
        }
        boolean anonymous = params.containsKey("anonymous") && Boolean.TRUE.equals(params.get("anonymous"));
        long replyId = 0;
        if (params.containsKey("replyId")) {
            try {
                replyId = Long.parseLong(params.get("replyId").toString());
            } catch (Exception e) {
                replyId = 0;
            }
        }
        Reply reply = postService.createReply(postId, author, username, content, anonymous, replyId);
        if (reply == null) {
            return RunResult.fail("帖子不存在");
        }
        return RunResult.ok().data(reply);
    }

    /** 点赞/点踩 - 需要登录 */
    @RequestMapping("/like")
    public RunResult like(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        String nickname = (String) request.getAttribute("currentNickname");
        long postId = Long.parseLong(params.get("postId").toString());
        String type = (String) params.get("type"); // like or dislike
        if (!"like".equals(type) && !"dislike".equals(type)) {
            return RunResult.fail("类型参数错误");
        }
        return postService.likePost(username, nickname, postId, type);
    }

    /** 用户帖子列表 - 无需登录 */
    @RequestMapping("/user")
    public RunResult userPosts(@RequestParam String username,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.listUserPosts(username, page, size);
        List<Reply> replies = postService.listUserReplies(username, page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("posts", posts);
        data.put("replies", replies);
        data.put("username", username);
        return RunResult.ok().data(data);
    }

    /** 获取通知列表 - 需要登录 */
    @RequestMapping("/notices")
    public RunResult notices(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "20") int size,
                             HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            return RunResult.fail("请先登录");
        }
        List<Notice> notices = noticeService.listNotices(username, page, size);
        long unreadCount = noticeService.getUnreadNoticeCount(username);
        Map<String, Object> data = new HashMap<>();
        data.put("notices", notices);
        data.put("unreadCount", unreadCount);
        return RunResult.ok().data(data);
    }

    /** 标记通知为已读 - 需要登录 */
    @RequestMapping("/notices/read")
    public RunResult markNoticesRead(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            return RunResult.fail("请先登录");
        }
        int count = noticeService.markNoticesAsRead(username);
        return RunResult.ok().fluentPut("count", count);
    }

    /** 删除帖子 - 需要登录，只能删除自己的帖子 */
    @RequestMapping("/delete")
    public RunResult deletePost(@RequestParam long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            return RunResult.fail("请先登录");
        }
        return postService.deletePost(id, username);
    }

    /** 删除回复 - 需要登录，只能删除自己的回复 */
    @RequestMapping("/reply/delete")
    public RunResult deleteReply(@RequestParam long id, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            return RunResult.fail("请先登录");
        }
        return postService.deleteReply(id, username);
    }

}
