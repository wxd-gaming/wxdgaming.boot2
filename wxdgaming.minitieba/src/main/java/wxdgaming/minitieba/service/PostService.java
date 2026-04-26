package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksIterator;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.minitieba.bean.Post;
import wxdgaming.minitieba.bean.Reply;
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

    private static final String POST_PREFIX = "post:";
    private static final String POST_INDEX_PREFIX = "post:idx:";
    private static final String USER_POST_PREFIX = "user:post:";
    private static final String USER_REPLY_PREFIX = "user:reply:";
    private static final String REPLY_PREFIX = "reply:";
    private static final String REPLY_INDEX = "reply_index:";
    private static final String LIKE_PREFIX = "like:";
    private static final String POST_ID_GENERATOR = "post_id_generator";
    private static final String POST_COUNT_KEY = "post_count";
    private static final String USER_POST_COUNT_PREFIX = "user:post:count:";
    private static final String USER_REPLY_COUNT_PREFIX = "user:reply:count:";

    public PostService(RocksDBHelper db) {
        this.db = db;
        // 启动时修复旧数据（添加缺失的用户索引）
        fixOldData();
    }

    /** 修复旧帖子数据的用户索引 */
    private void fixOldData() {
        try {
            int total = 0;
            int fixed = 0;
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
                        if (post != null && post.getUsername() != null && !post.getUsername().isEmpty()) {
                            // 检查是否已有用户索引
                            long timestamp = post.getCreateTime();
                            if (timestamp <= 0) {
                                timestamp = System.currentTimeMillis();
                                post.setCreateTime(timestamp);
                                db.put(POST_PREFIX + postId, post);
                            }
                            String userPostKey = USER_POST_PREFIX + post.getUsername() + ":" + buildTimeIndexKey(timestamp, postId);
                            if (!db.exits(userPostKey)) {
                                db.put(userPostKey, postId);
                                fixed++;
                                log.info("添加用户索引: username={}, postId={}", post.getUsername(), postId);
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                    iterator.next();
                }
            }
            log.info("fixOldData 完成: 共扫描 {} 条帖子, 修复了 {} 条", total, fixed);
            if (fixed > 0) {
                log.info("已修复 {} 条旧帖子的用户索引", fixed);
            }
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

    /** 发帖 */
    public Post createPost(String author, String username, String content, List<String> images, String video, boolean anonymous, boolean privated) {
        long id = nextId();
        long userPostId = nextUserPostId(username);
        Post post = new Post(id, author, username, content);
        post.setUserPostId(userPostId);
        if (images != null) {
            post.setImages(images);
        }
        post.setVideo(video);
        post.setAnonymous(anonymous);
        post.setPrivated(privated);
        long createTime = System.currentTimeMillis();
        post.setCreateTime(createTime);
        db.put(POST_PREFIX + id, post);

        // 时间索引key（用于全局列表）
        String timeIndexKey = buildTimeIndexKey(createTime, id);
        db.put(timeIndexKey, id);

        // 用户发帖索引：user:post:{username}:{倒序时间戳}:{postId}
        String userPostKey = USER_POST_PREFIX + username + ":" + (Long.MAX_VALUE - createTime) + ":" + id;
        db.put(userPostKey, id);

        // 更新总数
        long count = db.getLongValue(POST_COUNT_KEY) + 1;
        db.put(POST_COUNT_KEY, count);

        log.info("发帖成功: id={}, userPostId={}, author={}, username={}, anonymous={}, privated={}", 
                id, userPostId, author, username, anonymous, privated);
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
                        // 私密帖子只显示给作者本人
                        if (post.isPrivated() && (currentUser == null || !currentUser.equals(post.getUsername()))) {
                            iterator.next();
                            continue;
                        }
                        if (skipped < offset) {
                            skipped++;
                        } else {
                            // 匿名帖子隐藏作者信息
                            if (post.isAnonymous()) {
                                post.setAuthor("匿名用户");
                                post.setUsername(null);
                            }
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

    /** 获取帖子总数 */
    public int getPostCount() {
        return (int) db.getLongValue(POST_COUNT_KEY);
    }

    /** 获取帖子详情 */
    public Post getPost(long postId, String currentUser) {
        Post post = db.getObject(POST_PREFIX + postId, Post.class);
        if (post == null) {
            return null;
        }
        // 私密帖子只显示给作者本人
        if (post.isPrivated() && (currentUser == null || !currentUser.equals(post.getUsername()))) {
            return null;
        }
        // 匿名帖子隐藏作者信息
        if (post.isAnonymous()) {
            post.setAuthor("匿名用户");
            post.setUsername(null);
        }
        return post;
    }

    /** 获取帖子详情（无用户上下文） */
    public Post getPost(long postId) {
        return getPost(postId, null);
    }

    /** 回复帖子 */
    public Reply createReply(long postId, String author, String username, String content, boolean anonymous) {
        Post post = getPost(postId);
        if (post == null) {
            return null;
        }

        long replyId = nextId();
        long userReplyId = nextUserReplyId(username);
        Reply reply = new Reply(replyId, postId, author, username, content);
        reply.setUserReplyId(userReplyId);
        reply.setAnonymous(anonymous);
        db.put(REPLY_PREFIX + replyId, reply);

        // 用户跟帖索引：user:reply:{username}:{倒序时间戳}:{replyId}
        long createTime = System.currentTimeMillis();
        String userReplyKey = USER_REPLY_PREFIX + username + ":" + (Long.MAX_VALUE - createTime) + ":" + replyId;
        db.put(userReplyKey, replyId);

        post.setReplyCount(post.getReplyCount() + 1);
        db.put(POST_PREFIX + postId, post);

        List<Long> replyIndex = db.getList(REPLY_INDEX + postId);
        if (replyIndex == null) {
            replyIndex = new ArrayList<>();
        }
        replyIndex.add(replyId);
        db.put(REPLY_INDEX + postId, replyIndex);

        log.info("回复成功: postId={}, replyId={}, userReplyId={}, author={}, anonymous={}", 
                postId, replyId, userReplyId, author, anonymous);
        return reply;
    }

    /** 回复帖子（默认不匿名） */
    public Reply createReply(long postId, String author, String username, String content) {
        return createReply(postId, author, username, content, false);
    }

    /** 获取帖子回复列表 */
    public List<Reply> listReplies(long postId) {
        List<Long> replyIndex = db.getList(REPLY_INDEX + postId);
        if (replyIndex == null || replyIndex.isEmpty()) {
            return Collections.emptyList();
        }

        List<Reply> replies = new ArrayList<>();
        for (Long replyId : replyIndex) {
            Reply reply = db.getObject(REPLY_PREFIX + replyId, Reply.class);
            if (reply != null) {
                // 匿名回复隐藏作者信息
                if (reply.isAnonymous()) {
                    reply.setAuthor("匿名用户");
                    reply.setUsername(null);
                }
                replies.add(reply);
            }
        }
        return replies;
    }

    /** 点赞/点踩 */
    public RunResult likePost(String username, long postId, String type) {
        Post post = getPost(postId);
        if (post == null) {
            return RunResult.fail("帖子不存在");
        }

        String likeKey = LIKE_PREFIX + username + ":" + postId;
        UserLike existing = db.getObject(likeKey, UserLike.class);

        String action;
        if (existing != null) {
            if (existing.getType().equals(type)) {
                // 取消
                if ("like".equals(type)) {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                } else {
                    post.setDislikeCount(Math.max(0, post.getDislikeCount() - 1));
                }
                db.put(POST_PREFIX + postId, post);
                try {
                    db.getDb().delete(likeKey.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    log.error("删除点赞记录失败", e);
                }
                return RunResult.ok().fluentPut("action", "cancel");
            } else {
                // 切换
                if ("like".equals(existing.getType())) {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                } else {
                    post.setDislikeCount(Math.max(0, post.getDislikeCount() - 1));
                }
                if ("like".equals(type)) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setDislikeCount(post.getDislikeCount() + 1);
                }
                action = "set";
            }
        } else {
            // 新增
            if ("like".equals(type)) {
                post.setLikeCount(post.getLikeCount() + 1);
            } else {
                post.setDislikeCount(post.getDislikeCount() + 1);
            }
            action = "set";
        }

        db.put(likeKey, new UserLike(username, postId, type));
        db.put(POST_PREFIX + postId, post);
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

}
