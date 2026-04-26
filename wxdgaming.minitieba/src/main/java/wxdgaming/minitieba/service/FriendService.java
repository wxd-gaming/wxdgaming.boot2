package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.minitieba.bean.FriendRequest;
import wxdgaming.minitieba.bean.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 好友服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class FriendService {

    private final RocksDBHelper db;
    private final NoticeService noticeService;

    // 好友索引: friend:{user}:{friend} -> 1
    private static final String FRIEND_PREFIX = "friend:";
    // 好友申请索引: friend:request:to:{user}:{requestId} -> request
    private static final String FRIEND_REQUEST_TO_PREFIX = "friend:request:to:";
    // 好友申请发送索引: friend:request:from:{user}:{requestId} -> requestId
    private static final String FRIEND_REQUEST_FROM_PREFIX = "friend:request:from:";
    // 黑名单索引: black:{user}:{blocked} -> 1
    private static final String BLACK_PREFIX = "black:";
    // 好友申请ID生成器
    private static final String FRIEND_REQUEST_ID_GENERATOR = "friend_request_id_generator";

    public FriendService(RocksDBHelper db, NoticeService noticeService) {
        this.db = db;
        this.noticeService = noticeService;
    }

    /** 生成申请ID */
    private synchronized long nextRequestId() {
        long current = db.getLongValue(FRIEND_REQUEST_ID_GENERATOR);
        long next = current + 1;
        db.put(FRIEND_REQUEST_ID_GENERATOR, next);
        return next;
    }

    /** 发送好友申请 */
    public RunResult sendFriendRequest(String fromUser, String toUser) {
        if (fromUser.equals(toUser)) {
            return RunResult.fail("不能添加自己为好友");
        }

        // 检查目标用户是否存在
        User toUserObj = db.getObject("user:" + toUser, User.class);
        if (toUserObj == null) {
            return RunResult.fail("用户不存在");
        }

        // 检查是否已经是好友
        if (isFriend(fromUser, toUser)) {
            return RunResult.fail("你们已经是好友了");
        }

        // 检查是否在黑名单
        if (isBlacklisted(fromUser, toUser) || isBlacklisted(toUser, fromUser)) {
            return RunResult.fail("无法添加该用户为好友");
        }

        // 检查是否已有待处理的申请
        if (hasPendingRequest(fromUser, toUser)) {
            return RunResult.fail("已发送过申请，请等待对方处理");
        }

        // 获取申请人信息
        User fromUserObj = db.getObject("user:" + fromUser, User.class);

        // 创建申请
        long requestId = nextRequestId();
        FriendRequest request = new FriendRequest(requestId, fromUser,
                fromUserObj != null ? fromUserObj.getNickname() : fromUser,
                toUser);
        request.setFromAvatar(fromUserObj != null ? fromUserObj.getAvatar() : null);

        // 存储申请（双向索引）
        db.put(FRIEND_REQUEST_TO_PREFIX + toUser + ":" + requestId, request);
        db.put(FRIEND_REQUEST_FROM_PREFIX + fromUser + ":" + requestId, requestId);

        // 发送通知
        noticeService.sendNotice(toUser, fromUser,
                fromUserObj != null ? fromUserObj.getNickname() : fromUser,
                "friend_request", 0, null, null);

        log.info("发送好友申请: from={}, to={}, requestId={}", fromUser, toUser, requestId);
        return RunResult.ok().fluentPut("requestId", requestId);
    }

    /** 处理好友申请（同意/拒绝） */
    public RunResult handleFriendRequest(String toUser, long requestId, boolean accept) {
        FriendRequest request = db.getObject(FRIEND_REQUEST_TO_PREFIX + toUser + ":" + requestId, FriendRequest.class);
        if (request == null) {
            return RunResult.fail("申请不存在");
        }
        if (!"pending".equals(request.getStatus())) {
            return RunResult.fail("该申请已被处理");
        }
        if (!toUser.equals(request.getToUser())) {
            return RunResult.fail("无权操作此申请");
        }

        String fromUser = request.getFromUser();
        User toUserObj = db.getObject("user:" + toUser, User.class);
        String toUserNickname = toUserObj != null ? toUserObj.getNickname() : toUser;

        if (accept) {
            // 互加好友
            SingletonLockUtil.lockRunning(FRIEND_PREFIX + fromUserToKey(fromUser, toUser), () -> {
                db.put(FRIEND_PREFIX + fromUser + ":" + toUser, 1L);
                db.put(FRIEND_PREFIX + toUser + ":" + fromUser, 1L);
            });

            // 发送同意通知
            noticeService.sendNotice(fromUser, toUser, toUserNickname, "friend_accepted", 0, null, null);

            log.info("好友申请已同意: from={}, to={}", fromUser, toUser);
        } else {
            // 发送拒绝通知
            noticeService.sendNotice(fromUser, toUser, toUserNickname, "friend_rejected", 0, null, null);

            log.info("好友申请已拒绝: from={}, to={}", fromUser, toUser);
        }

        // 更新申请状态
        request.setStatus(accept ? "accepted" : "rejected");
        db.put(FRIEND_REQUEST_TO_PREFIX + toUser + ":" + requestId, request);

        return RunResult.ok();
    }

    /** 辅助方法：生成锁key */
    private String fromUserToKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + ":" + user2 : user2 + ":" + user1;
    }

    /** 删除好友 */
    public RunResult removeFriend(String user, String friend) {
        if (!isFriend(user, friend)) {
            return RunResult.fail("你们不是好友");
        }

        SingletonLockUtil.lockRunning(FRIEND_PREFIX + fromUserToKey(user, friend), () -> {
            db.delete((FRIEND_PREFIX + user + ":" + friend));
            db.delete((FRIEND_PREFIX + friend + ":" + user));
        });

        log.info("删除好友: user={}, friend={}", user, friend);
        return RunResult.ok();
    }

    /** 获取好友列表 */
    public List<String> listFriends(String user) {
        List<String> friends = new ArrayList<>();
        String prefix = FRIEND_PREFIX + user + ":";

        try {
            var it = db.getDb().newIterator();
            it.seek(prefix.getBytes(StandardCharsets.UTF_8));
            while (it.isValid()) {
                String key = new String(it.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(prefix)) break;
                String friend = key.substring(prefix.length());
                friends.add(friend);
                it.next();
            }
        } catch (Exception e) {
            log.error("获取好友列表失败: user={}", user, e);
        }
        return friends;
    }

    /** 检查是否是好友 */
    public boolean isFriend(String user1, String user2) {
        return db.exits(FRIEND_PREFIX + user1 + ":" + user2);
    }

    /** 获取收到的好友申请列表 */
    public List<FriendRequest> listReceivedRequests(String user) {
        List<FriendRequest> requests = new ArrayList<>();
        String prefix = FRIEND_REQUEST_TO_PREFIX + user + ":";

        try {
            var it = db.getDb().newIterator();
            it.seek(prefix.getBytes(StandardCharsets.UTF_8));
            while (it.isValid()) {
                String key = new String(it.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(prefix)) break;
                FriendRequest request = db.getObject(key, FriendRequest.class);
                if (request != null && "pending".equals(request.getStatus())) {
                    requests.add(request);
                }
                it.next();
            }
        } catch (Exception e) {
            log.error("获取好友申请列表失败: user={}", user, e);
        }
        return requests;
    }

    /** 拉黑用户 */
    public RunResult blockUser(String user, String blocked) {
        if (user.equals(blocked)) {
            return RunResult.fail("不能拉黑自己");
        }

        // 双向拉黑
        db.put(BLACK_PREFIX + user + ":" + blocked, 1L);
        db.put(BLACK_PREFIX + blocked + ":" + user, 1L);

        // 如果是好友，先删除好友关系
        if (isFriend(user, blocked)) {
            removeFriend(user, blocked);
        }

        log.info("拉黑用户: user={}, blocked={}", user, blocked);
        return RunResult.ok();
    }

    /** 取消拉黑 */
    public RunResult unblockUser(String user, String blocked) {
        try {
            db.getDb().delete((BLACK_PREFIX + user + ":" + blocked).getBytes(StandardCharsets.UTF_8));
            log.info("取消拉黑: user={}, blocked={}", user, blocked);
        } catch (Exception e) {
            log.error("取消拉黑失败", e);
        }
        return RunResult.ok();
    }

    /** 获取黑名单列表 */
    public List<String> listBlacklist(String user) {
        List<String> blacklist = new ArrayList<>();
        String prefix = BLACK_PREFIX + user + ":";

        try {
            var it = db.getDb().newIterator();
            it.seek(prefix.getBytes(StandardCharsets.UTF_8));
            while (it.isValid()) {
                String key = new String(it.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(prefix)) break;
                String blocked = key.substring(prefix.length());
                blacklist.add(blocked);
                it.next();
            }
        } catch (Exception e) {
            log.error("获取黑名单失败: user={}", user, e);
        }
        return blacklist;
    }

    /** 检查是否在黑名单 */
    public boolean isBlacklisted(String user, String other) {
        return db.exits(BLACK_PREFIX + user + ":" + other);
    }

    /** 检查是否有待处理申请 */
    private boolean hasPendingRequest(String fromUser, String toUser) {
        String prefix = FRIEND_REQUEST_TO_PREFIX + toUser + ":";
        try {
            var it = db.getDb().newIterator();
            it.seek(prefix.getBytes(StandardCharsets.UTF_8));
            while (it.isValid()) {
                String key = new String(it.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(prefix)) break;
                FriendRequest request = db.getObject(key, FriendRequest.class);
                if (request != null && "pending".equals(request.getStatus())
                        && fromUser.equals(request.getFromUser())) {
                    return true;
                }
                it.next();
            }
        } catch (Exception e) {
            log.error("检查待处理申请失败", e);
        }
        return false;
    }

    /** 标记所有收到的好友申请为已读（这里简单实现，实际可以扩展） */
    public void markRequestsAsRead(String username) {
        // 由于当前设计没有已读状态，这个方法保留扩展
        log.info("标记好友申请为已读: user={}", username);
    }
}
