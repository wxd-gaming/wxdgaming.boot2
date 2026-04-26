package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.rocksdb.RocksIterator;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.minitieba.bean.Notice;
import wxdgaming.minitieba.bean.Post;
import wxdgaming.minitieba.bean.Reply;
import wxdgaming.minitieba.bean.User;
import wxdgaming.minitieba.bean.UserLike;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 帖子服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class PostService {

    private final RocksDBHelper db;
    private final NoticeService noticeService;
    private final FriendService friendService;
    private final Object postCountLock = new Object();  // 发帖计数锁

    private static final String POST_PREFIX = "post:";
    private static final String USER_PREFIX = "user:";
    private static final String POST_INDEX_PREFIX = "post:idx:";
    private static final String USER_POST_PREFIX = "user:post:";
    private static final String USER_REPLY_PREFIX = "user:reply:";
    private static final String REPLY_PREFIX = "reply:";
    private static final String REPLY_INDEX_PREFIX = "reply:idx:";  // 回复索引（替代List，按帖子ID+时间倒序）
    private static final String LIKE_PREFIX = "like:";
    private static final String POST_ID_GENERATOR = "post_id_generator";
    private static final String POST_COUNT_KEY = "post_count";
    private static final String USER_POST_COUNT_PREFIX = "user:post:count:";
    private static final String USER_REPLY_COUNT_PREFIX = "user:reply:count:";
    private static final String NOTICE_PREFIX = "notice:";
    private static final String USER_NOTICE_INDEX_PREFIX = "user:notice:";
    private static final String NOTICE_ID_GENERATOR = "notice_id_generator";
    private static final String USER_NOTICE_COUNT_PREFIX = "user:notice:count:";
    private static final String USER_NOTICE_UNREAD_PREFIX = "user:notice:unread:";

    public PostService(RocksDBHelper db, NoticeService noticeService, FriendService friendService) {
        this.db = db;
        this.noticeService = noticeService;
        this.friendService = friendService;
        // 启动时修复旧数据（添加缺失的用户索引）
        fixOldData();
    }

    /** 修复旧帖子数据的用户索引 */
    private void fixOldData() {
        try {
            int total = 0;
            int fixed = 0;
            int cleaned = 0;
            int usernameFixed = 0;
            try (RocksIterator iterator = db.getDb().newIterator()) {
                iterator.seek(POST_PREFIX.getBytes(StandardCharsets.UTF_8));
                while (iterator.isValid()) {
                    String key = new String(iterator.key(), StandardCharsets.UTF_8);
                    if (!key.startsWith(POST_PREFIX)) {
                        break;
                    }
                    String idStr = key.substring(POST_PREFIX.length());
                    if (idStr.contains(":")) {
                        // 跳过复合key
                        iterator.next();
                        continue;
                    }
                    total++;
                    try {
                        long postId = Long.parseLong(idStr);
                        Post post = db.getObject(POST_PREFIX + postId, Post.class);
                        log.info("检查帖子 postId={}, post={}, username={}", postId, post, post != null ? post.getUsername() : "null");
                        if (post != null) {
                            // 修复username为空的帖子（使用author作为username）
                            if (post.getUsername() == null || post.getUsername().isEmpty()) {
                                if (post.getAuthor() != null && !post.getAuthor().isEmpty()) {
                                    post.setUsername(post.getAuthor());
                                    db.put(POST_PREFIX + postId, post);
                                    usernameFixed++;
                                    log.info("修复帖子username: postId={}, author={}", postId, post.getAuthor());
                                }
                            }
                            // 检查是否已有用户索引
                            if (post.getUsername() != null && !post.getUsername().isEmpty()) {
                                long timestamp = post.getCreateTime();
                                if (timestamp <= 0) {
                                    timestamp = System.currentTimeMillis();
                                    post.setCreateTime(timestamp);
                                    db.put(POST_PREFIX + postId, post);
                                }
                                // 键格式必须与 createPost 一致: user:post:{username}:{倒序时间戳}:{postId}
                                String userPostKey = USER_POST_PREFIX + post.getUsername() + ":" + (Long.MAX_VALUE - timestamp) + ":" + postId;
                                if (!db.exits(userPostKey)) {
                                    db.put(userPostKey, postId);
                                    fixed++;
                                    log.info("添加用户索引: username={}, postId={}", post.getUsername(), postId);
                                }
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                    iterator.next();
                }
            }

            // 清理旧格式的索引（包含 post:idx: 的错误格式）
            try (RocksIterator iterator = db.getDb().newIterator()) {
                iterator.seek(USER_POST_PREFIX.getBytes(StandardCharsets.UTF_8));
                while (iterator.isValid()) {
                    String key = new String(iterator.key(), StandardCharsets.UTF_8);
                    if (!key.startsWith(USER_POST_PREFIX)) {
                        break;
                    }
                    // 检查是否是旧格式（包含 post:idx:）
                    if (key.contains(":post:idx:")) {
                        log.info("清理旧格式索引: {}", key);
                        db.getDb().delete(key.getBytes(StandardCharsets.UTF_8));
                        cleaned++;
                    }
                    iterator.next();
                }
            }

            log.info("fixOldData 完成: 共扫描 {} 条帖子, 修复了 {} 条username, 添加了 {} 条索引, 清理了 {} 条旧索引", total, usernameFixed, fixed, cleaned);
        } catch (Exception e) {
            log.error("修复旧数据失败", e);
        }
    }

    /** 生成全局自增ID（synchronized 保证并发安全） */
    private synchronized long nextId() {
        long current = db.getLongValue(POST_ID_GENERATOR);
        long next = current + 1;
        db.put(POST_ID_GENERATOR, next);
        return next;
    }

    /** 生成用户发帖序号 */
    private synchronized long nextUserPostId(String username) {
        String key = USER_POST_COUNT_PREFIX + username;
        long current = db.getLongValue(key);
        long next = current + 1;
        db.put(key, next);
        return next;
    }

    /** 生成用户跟帖序号 */
    private synchronized long nextUserReplyId(String username) {
        String key = USER_REPLY_COUNT_PREFIX + username;
        long current = db.getLongValue(key);
        long next = current + 1;
        db.put(key, next);
        return next;
    }

    /**
     * 构建时间索引key
     * 格式: post:idx:{timestamp}:{postId}
     * 使用时间戳倒序（新的在前），同一毫秒内用postId保证唯一
     */
    private String buildTimeIndexKey(long timestamp, long postId) {
        // 用 Long.MAX_VALUE - timestamp 实现倒序，新帖子排前面
        return String.format("%s%d:%d", POST_INDEX_PREFIX, Long.MAX_VALUE - timestamp, postId);
    }

    /** 发帖
     * @param visibility 可见性: 0-无限制(公开), 1-仅好友可见, 2-仅自己可见(私密)
     */
    public Post createPost(String author, String username, String content, List<String> images, String video, boolean anonymous, int visibility) {
        long id = nextId();
        long userPostId = nextUserPostId(username);
        Post post = new Post(id, author, username, content);
        post.setUserPostId(userPostId);
        // 获取用户头像
        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user != null) {
            post.setAvatar(user.getAvatar());
        }
        if (images != null) {
            post.setImages(images);
        }
        post.setVideo(video);
        post.setAnonymous(anonymous);
        post.setVisibility(visibility);
        post.setPrivated(visibility == 2); // 私密帖子标记
        long createTime = System.currentTimeMillis();
        post.setCreateTime(createTime);
        db.put(POST_PREFIX + id, post);

        // 时间索引key（用于全局列表）
        String timeIndexKey = buildTimeIndexKey(createTime, id);
        db.put(timeIndexKey, id);

        // 用户发帖索引：user:post:{username}:{倒序时间戳}:{postId}
        String userPostKey = USER_POST_PREFIX + username + ":" + (Long.MAX_VALUE - createTime) + ":" + id;
        db.put(userPostKey, id);

        // 更新总数（加锁保护，防止并发丢失）
        synchronized (postCountLock) {
            long count = db.getLongValue(POST_COUNT_KEY) + 1;
            db.put(POST_COUNT_KEY, count);
        }

        log.info("发帖成功: id={}, userPostId={}, author={}, username={}, anonymous={}, visibility={}",
                id, userPostId, author, username, anonymous, visibility);
        return post;
    }

    /**
     * 获取帖子列表（利用RocksDB范围查询，无需加载全量索引）
     * @param currentUser 当前登录用户，如果为null则只显示公开帖子
     */
    public List<Post> listPosts(int page, int size, String currentUser) {
        int offset = (page - 1) * size;

        String startKey = POST_INDEX_PREFIX;
        byte[] startBytes = startKey.getBytes(StandardCharsets.UTF_8);

        List<Post> posts = new ArrayList<>();
        int skipped = 0;
        int collected = 0;

        try (RocksIterator iterator = db.getDb().newIterator()) {
            iterator.seek(startBytes);
            while (iterator.isValid() && collected < size) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(POST_INDEX_PREFIX)) {
                    break;
                }
                String[] parts = key.substring(POST_INDEX_PREFIX.length()).split(":");
                if (parts.length >= 2) {
                    long postId = Long.parseLong(parts[parts.length - 1]);
                    Post post = db.getObject(POST_PREFIX + postId, Post.class);
                    if (post != null) {
                        String postAuthor = post.getUsername();

                        // 检查黑名单：作者在当前用户黑名单中，则不显示
                        if (currentUser != null && friendService.isBlacklisted(currentUser, postAuthor)) {
                            iterator.next();
                            continue;
                        }

                        // 检查可见性
                        int visibility = post.getVisibility();
                        if (visibility == 2) { // 仅自己可见
                            if (currentUser == null || !currentUser.equals(postAuthor)) {
                                iterator.next();
                                continue;
                            }
                        } else if (visibility == 1) { // 仅好友可见
                            if (currentUser == null) {
                                iterator.next();
                                continue;
                            }
                            if (!currentUser.equals(postAuthor) && !friendService.isFriend(currentUser, postAuthor)) {
                                iterator.next();
                                continue;
                            }
                        }

                        if (skipped < offset) {
                            skipped++;
                        } else {
                            // 匿名帖子隐藏作者信息（创建脱敏副本，不修改原始对象）
                            if (post.isAnonymous() && (currentUser == null || !currentUser.equals(postAuthor))) {
                                Post anonymousPost = new Post();
                                BeanUtils.copyProperties(post, anonymousPost);
                                anonymousPost.setAuthor("匿名用户");
                                anonymousPost.setUsername(null);
                                anonymousPost.setAvatar(null);
                                post = anonymousPost;
                            } else {
                                // 非匿名帖子更新作者昵称为当前昵称和头像
                                updatePostAuthor(post);
                                updatePostAvatar(post);
                            }
                            // 设置当前用户的点赞状态
                            if (currentUser != null) {
                                post.setLikeStatus(getUserLikeStatus(currentUser, postId));
                            }
                            // 截断内容用于列表显示，最多128字
                            truncateContent(post, 128);
                            posts.add(post);
                            collected++;
                        }
                    }
                }
                iterator.next();
            }
        } catch (Exception e) {
            log.error("遍历帖子索引失败", e);
        }

        return posts;
    }

    /** 截断帖子内容用于列表显示 */
    private void truncateContent(Post post, int maxChars) {
        if (post != null && post.getContent() != null && post.getContent().length() > maxChars) {
            post.setContent(post.getContent().substring(0, maxChars) + "...");
        }
    }

    /** 获取帖子总数 */
    public int getPostCount() {
        return (int) db.getLongValue(POST_COUNT_KEY);
    }

    /** 更新帖子作者昵称为当前昵称 */
    private void updatePostAuthor(Post post) {
        if (post != null && post.getUsername() != null && !post.isAnonymous()) {
            User user = db.getObject(USER_PREFIX + post.getUsername(), User.class);
            if (user != null && user.getNickname() != null && !user.getNickname().isBlank()) {
                post.setAuthor(user.getNickname());
            }
        }
    }

    /** 更新帖子作者头像为当前头像 */
    private void updatePostAvatar(Post post) {
        if (post != null && post.getUsername() != null && !post.isAnonymous()) {
            User user = db.getObject(USER_PREFIX + post.getUsername(), User.class);
            if (user != null && user.getAvatar() != null && !user.getAvatar().isBlank()) {
                post.setAvatar(user.getAvatar());
            }
        }
    }

    /** 获取帖子详情 */
    public Post getPost(long postId, String currentUser) {
        Post post = db.getObject(POST_PREFIX + postId, Post.class);
        if (post == null) {
            return null;
        }
        String postAuthor = post.getUsername();

        // 检查黑名单
        if (currentUser != null && friendService.isBlacklisted(currentUser, postAuthor)) {
            return null;
        }

        // 检查可见性
        int visibility = post.getVisibility();
        if (visibility == 2) { // 仅自己可见
            if (currentUser == null || !currentUser.equals(postAuthor)) {
                return null;
            }
        } else if (visibility == 1) { // 仅好友可见
            if (currentUser == null) {
                return null;
            }
            if (!currentUser.equals(postAuthor) && !friendService.isFriend(currentUser, postAuthor)) {
                return null;
            }
        }

        // 匿名帖子返回脱敏副本，不修改原始对象
        if (post.isAnonymous() && (currentUser == null || !currentUser.equals(postAuthor))) {
            Post anonymousPost = new Post();
            BeanUtils.copyProperties(post, anonymousPost);
            anonymousPost.setAuthor("匿名用户");
            anonymousPost.setUsername(null);
            anonymousPost.setAvatar(null);
            return anonymousPost;
        }
        // 更新作者昵称和头像为当前值
        updatePostAuthor(post);
        updatePostAvatar(post);
        return post;
    }

    /** 获取原始帖子（不隐藏匿名信息，用于发送通知） */
    private Post getPostRaw(long postId) {
        return db.getObject(POST_PREFIX + postId, Post.class);
    }

    /** 获取帖子详情（无用户上下文） */
    public Post getPost(long postId) {
        return getPost(postId, null);
    }

    /** 回复帖子 */
    public Reply createReply(long postId, String author, String username, String content, boolean anonymous) {
        return createReply(postId, author, username, content, anonymous, 0);
    }

    public Reply createReply(long postId, String author, String username, String content, boolean anonymous, long replyId) {
        // 获取原始帖子用于发送通知
        Post rawPost = getPostRaw(postId);
        if (rawPost == null) {
            return null;
        }

        Post post = getPost(postId);

        long newReplyId = nextId();
        long userReplyId = nextUserReplyId(username);
        Reply reply = new Reply(newReplyId, postId, author, username, content);
        reply.setUserReplyId(userReplyId);
        reply.setAnonymous(anonymous);
        reply.setReplyId(replyId); // 设置引用回复ID
        // 获取用户头像
        User user = db.getObject(USER_PREFIX + username, User.class);
        if (user != null) {
            reply.setAvatar(user.getAvatar());
        }
        db.put(REPLY_PREFIX + newReplyId, reply);

        // 用户跟帖索引：user:reply:{username}:{倒序时间戳}:{replyId}
        long createTime = System.currentTimeMillis();
        String userReplyKey = USER_REPLY_PREFIX + username + ":" + (Long.MAX_VALUE - createTime) + ":" + newReplyId;
        db.put(userReplyKey, newReplyId);

        // 更新回复计数（用 SingletonLockUtil 按帖子ID加锁，防止并发丢失数据，5分钟无访问自动释放）
        SingletonLockUtil.lockRunning(postId, () -> {
            // 重新读取最新数据
            Post lockPost = getPostRaw(postId);
            if (lockPost == null) {
                return;
            }
            lockPost.setReplyCount(lockPost.getReplyCount() + 1);
            db.put(POST_PREFIX + postId, lockPost);

            // 用 RocksDB 二级索引替代 List（格式: reply:idx:{postId}:{倒序时间戳}:{replyId}）
            String replyIndexKey = REPLY_INDEX_PREFIX + postId + ":" + (Long.MAX_VALUE - createTime) + ":" + newReplyId;
            db.put(replyIndexKey, newReplyId);
        });

        // 发送通知给帖子作者
        String postContent = rawPost.getContent().length() > 50 ? rawPost.getContent().substring(0, 50) : rawPost.getContent();
        noticeService.sendNotice(rawPost.getUsername(), username, author, "reply", postId, postContent, content);

        log.info("回复成功: postId={}, replyId={}, userReplyId={}, author={}, anonymous={}",
                postId, newReplyId, userReplyId, author, anonymous);
        return reply;
    }

    /** 回复帖子（默认不匿名） */
    public Reply createReply(long postId, String author, String username, String content) {
        return createReply(postId, author, username, content, false);
    }

    /** 更新回复作者昵称为当前昵称 */
    private void updateReplyAuthor(Reply reply) {
        if (reply != null && reply.getUsername() != null && !reply.isAnonymous()) {
            User user = db.getObject(USER_PREFIX + reply.getUsername(), User.class);
            if (user != null && user.getNickname() != null && !user.getNickname().isBlank()) {
                reply.setAuthor(user.getNickname());
            }
        }
    }

    /** 更新回复作者头像为当前头像 */
    private void updateReplyAvatar(Reply reply) {
        if (reply != null && reply.getUsername() != null && !reply.isAnonymous()) {
            User user = db.getObject(USER_PREFIX + reply.getUsername(), User.class);
            if (user != null && user.getAvatar() != null && !user.getAvatar().isBlank()) {
                reply.setAvatar(user.getAvatar());
            }
        }
    }

    /** 获取帖子回复列表（使用 RocksDB 范围查询，不再用 List） */
    public List<Reply> listReplies(long postId) {
        List<Reply> replies = new ArrayList<>();

        try (RocksIterator iterator = db.getDb().newIterator()) {
            // 范围查询：reply:idx:{postId}:
            String startKey = REPLY_INDEX_PREFIX + postId + ":";
            iterator.seek(startKey.getBytes(StandardCharsets.UTF_8));

            while (iterator.isValid()) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                // 检查是否还在当前帖子的键范围内
                if (!key.startsWith(startKey)) {
                    break;
                }
                // 解析 replyId（格式: reply:idx:{postId}:{倒序时间戳}:{replyId}）
                String[] parts = key.substring(startKey.length()).split(":");
                if (parts.length >= 1) {
                    long replyId = Long.parseLong(parts[parts.length - 1]);
                    Reply reply = db.getObject(REPLY_PREFIX + replyId, Reply.class);
                    if (reply != null) {
                        // 匿名回复隐藏作者信息
                        if (reply.isAnonymous()) {
                            reply.setAuthor("匿名用户");
                            reply.setUsername(null);
                            reply.setAvatar(null);
                        } else {
                            // 非匿名回复更新作者昵称和头像为当前值
                            updateReplyAuthor(reply);
                            updateReplyAvatar(reply);
                        }
                        replies.add(reply);
                    }
                }
                iterator.next();
            }
        } catch (Exception e) {
            log.error("遍历回复索引失败: postId={}", postId, e);
        }
        return replies;
    }

    /** 点赞/点踩 */
    public RunResult likePost(String username, String nickname, long postId, String type) {
        // 获取原始帖子用于发送通知
        Post rawPost = getPostRaw(postId);
        if (rawPost == null) {
            return RunResult.fail("帖子不存在");
        }

        String likeKey = LIKE_PREFIX + username + ":" + postId;
        UserLike existing = db.getObject(likeKey, UserLike.class);

        String action;
        if (existing != null) {
            if (existing.getType().equals(type)) {
                // 取消
                if ("like".equals(type)) {
                    rawPost.setLikeCount(Math.max(0, rawPost.getLikeCount() - 1));
                } else {
                    rawPost.setDislikeCount(Math.max(0, rawPost.getDislikeCount() - 1));
                }
                db.put(POST_PREFIX + postId, rawPost);
                try {
                    db.getDb().delete(likeKey.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    log.error("删除点赞记录失败", e);
                }
                return RunResult.ok().fluentPut("action", "cancel");
            } else {
                // 切换
                if ("like".equals(existing.getType())) {
                    rawPost.setLikeCount(Math.max(0, rawPost.getLikeCount() - 1));
                } else {
                    rawPost.setDislikeCount(Math.max(0, rawPost.getDislikeCount() - 1));
                }
                if ("like".equals(type)) {
                    rawPost.setLikeCount(rawPost.getLikeCount() + 1);
                } else {
                    rawPost.setDislikeCount(rawPost.getDislikeCount() + 1);
                }
                action = "set";
                // 切换时发送通知
                noticeService.sendNotice(rawPost.getUsername(), username, nickname, type, postId,
                        rawPost.getContent().length() > 50 ? rawPost.getContent().substring(0, 50) : rawPost.getContent(), null);
            }
        } else {
            // 新增
            if ("like".equals(type)) {
                rawPost.setLikeCount(rawPost.getLikeCount() + 1);
            } else {
                rawPost.setDislikeCount(rawPost.getDislikeCount() + 1);
            }
            action = "set";
            // 发送通知
            noticeService.sendNotice(rawPost.getUsername(), username, nickname, type, postId,
                    rawPost.getContent().length() > 50 ? rawPost.getContent().substring(0, 50) : rawPost.getContent(), null);
        }

        db.put(likeKey, new UserLike(username, postId, type));
        db.put(POST_PREFIX + postId, rawPost);
        return RunResult.ok().fluentPut("action", action);
    }

    /** 获取用户对帖子的点赞/点踩状态 */
    public String getUserLikeStatus(String username, long postId) {
        String likeKey = LIKE_PREFIX + username + ":" + postId;
        UserLike existing = db.getObject(likeKey, UserLike.class);
        return existing == null ? "none" : existing.getType();
    }

    /**
     * 获取用户的发帖列表（用户自己的帖子，显示完整信息）
     */
    public List<Post> listUserPosts(String username, int page, int size) {
        int offset = (page - 1) * size;
        String startKey = USER_POST_PREFIX + username + ":";

        List<Post> posts = new ArrayList<>();
        int skipped = 0;
        int collected = 0;

        try (RocksIterator iterator = db.getDb().newIterator()) {
            iterator.seek(startKey.getBytes(StandardCharsets.UTF_8));
            while (iterator.isValid() && collected < size) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                // 检查是否还在当前用户的键范围内
                if (!key.startsWith(startKey)) {
                    break;
                }
                // 解析键格式: user:post:{username}:{倒序时间戳}:{postId}
                String[] parts = key.substring(startKey.length()).split(":");
                if (parts.length >= 2) {
                    long postId = Long.parseLong(parts[parts.length - 1]);
                    if (skipped < offset) {
                        skipped++;
                    } else {
                        Post post = db.getObject(POST_PREFIX + postId, Post.class);
                        if (post != null) {
                            // 更新作者昵称和头像为当前值
                            updatePostAuthor(post);
                            updatePostAvatar(post);
                            truncateContent(post, 128);
                            posts.add(post);
                            collected++;
                        }
                    }
                }
                iterator.next();
            }
        } catch (Exception e) {
            log.error("遍历用户发帖索引失败", e);
        }
        return posts;
    }

    /**
     * 获取用户的跟帖列表（用户自己的回复，显示完整信息）
     */
    public List<Reply> listUserReplies(String username, int page, int size) {
        int offset = (page - 1) * size;
        String startKey = USER_REPLY_PREFIX + username + ":";

        List<Reply> replies = new ArrayList<>();
        int skipped = 0;
        int collected = 0;

        try (RocksIterator iterator = db.getDb().newIterator()) {
            iterator.seek(startKey.getBytes(StandardCharsets.UTF_8));
            while (iterator.isValid() && collected < size) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                // 检查是否还在当前用户的键范围内
                if (!key.startsWith(startKey)) {
                    break;
                }
                // 解析键格式: user:reply:{username}:{倒序时间戳}:{replyId}
                String[] parts = key.substring(startKey.length()).split(":");
                if (parts.length >= 2) {
                    long replyId = Long.parseLong(parts[parts.length - 1]);
                    if (skipped < offset) {
                        skipped++;
                    } else {
                        Reply reply = db.getObject(REPLY_PREFIX + replyId, Reply.class);
                        if (reply != null) {
                            // 更新作者昵称和头像为当前值
                            updateReplyAuthor(reply);
                            updateReplyAvatar(reply);
                            replies.add(reply);
                            collected++;
                        }
                    }
                }
                iterator.next();
            }
        } catch (Exception e) {
            log.error("遍历用户跟帖索引失败", e);
        }
        return replies;
    }

    /** 生成通知ID */
    /** 删除帖子 - 需要验证是否是帖子的作者 */
    public RunResult deletePost(long postId, String username) {
        Post post = getPostRaw(postId);
        if (post == null) {
            return RunResult.fail("帖子不存在");
        }
        if (!username.equals(post.getUsername())) {
            return RunResult.fail("只能删除自己的帖子");
        }

        // 删除帖子
        try {
            db.getDb().delete((POST_PREFIX + postId).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("删除帖子失败: postId={}", postId, e);
            return RunResult.fail("删除失败");
        }

        // 更新帖子总数
        long count = db.getLongValue(POST_COUNT_KEY) - 1;
        db.put(POST_COUNT_KEY, Math.max(0, count));

        log.info("删除帖子成功: postId={}, username={}", postId, username);
        return RunResult.ok();
    }

    /** 删除回复 - 需要验证是否是回复的作者 */
    public RunResult deleteReply(long replyId, String username) {
        Reply reply = db.getObject(REPLY_PREFIX + replyId, Reply.class);
        if (reply == null) {
            return RunResult.fail("回复不存在");
        }
        if (!username.equals(reply.getUsername())) {
            return RunResult.fail("只能删除自己的回复");
        }

        long postId = reply.getPostId();

        // 删除回复
        try {
            db.getDb().delete((REPLY_PREFIX + replyId).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("删除回复失败: replyId={}", replyId, e);
            return RunResult.fail("删除失败");
        }

        // 更新帖子的回复数
        Post rawPost = getPostRaw(postId);
        if (rawPost != null) {
            rawPost.setReplyCount(Math.max(0, rawPost.getReplyCount() - 1));
            db.put(POST_PREFIX + postId, rawPost);
        }

        log.info("删除回复成功: replyId={}, postId={}, username={}", replyId, postId, username);
        return RunResult.ok();
    }

}
