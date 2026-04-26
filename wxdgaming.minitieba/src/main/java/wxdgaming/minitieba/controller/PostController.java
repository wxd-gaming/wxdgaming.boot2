package wxdgaming.minitieba.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.bean.Post;
import wxdgaming.minitieba.bean.Reply;
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

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 发帖 - 需要登录 */
    @RequestMapping("/create")
    public RunResult create(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        String nickname = (String) request.getAttribute("currentNickname");
        String author = nickname != null ? nickname : username;
        String content = (String) params.get("content");
        if (content == null || content.isBlank()) {
            return RunResult.fail("内容不能为空");
        }
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) params.get("images");
        String video = (String) params.get("video");
        boolean anonymous = params.containsKey("anonymous") && Boolean.TRUE.equals(params.get("anonymous"));
        boolean privated = params.containsKey("privated") && Boolean.TRUE.equals(params.get("privated"));
        Post post = postService.createPost(author, username, content, images, video, anonymous, privated);
        return RunResult.ok().data(post);
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
        boolean anonymous = params.containsKey("anonymous") && Boolean.TRUE.equals(params.get("anonymous"));
        Reply reply = postService.createReply(postId, author, username, content, anonymous);
        if (reply == null) {
            return RunResult.fail("帖子不存在");
        }
        return RunResult.ok().data(reply);
    }

    /** 点赞/点踩 - 需要登录 */
    @RequestMapping("/like")
    public RunResult like(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUser");
        long postId = Long.parseLong(params.get("postId").toString());
        String type = (String) params.get("type"); // like or dislike
        if (!"like".equals(type) && !"dislike".equals(type)) {
            return RunResult.fail("类型参数错误");
        }
        return postService.likePost(username, postId, type);
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

}
