package wxdgaming.minitieba.service;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksIterator;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.minitieba.bean.Notice;
import wxdgaming.minitieba.bean.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Service
public class NoticeService {

    private final RocksDBHelper db;

    private static final String NOTICE_PREFIX = "notice:";
    private static final String USER_NOTICE_INDEX_PREFIX = "user:notice:";
    private static final String NOTICE_ID_GENERATOR = "notice_id_generator";
    private static final String USER_NOTICE_COUNT_PREFIX = "user:notice:count:";
    private static final String USER_NOTICE_UNREAD_PREFIX = "user:notice:unread:";
    private static final String USER_PREFIX = "user:";

    public NoticeService(RocksDBHelper db) {
        this.db = db;
    }

    /** 生成通知ID */
    private synchronized long nextNoticeId() {
        long current = db.getLongValue(NOTICE_ID_GENERATOR);
        long next = current + 1;
        db.put(NOTICE_ID_GENERATOR, next);
        return next;
    }

    /** 发送通知 */
    public void sendNotice(String targetUser, String fromUser, String fromNickname, String type, long postId, String postContent, String replyContent) {
        // 不能给自己发通知
        if (targetUser.equals(fromUser)) {
            return;
        }
        long noticeId = nextNoticeId();
        Notice notice = new Notice(noticeId, targetUser, fromUser, fromNickname, type, postId);
        notice.setPostContent(postContent);
        notice.setReplyContent(replyContent);
        notice.setCreateTime(System.currentTimeMillis());
        notice.setReaded(false);

        // 获取发送者头像
        User fromUserObj = db.getObject(USER_PREFIX + fromUser, User.class);
        if (fromUserObj != null) {
            notice.setAvatar(fromUserObj.getAvatar());
        }

        // 存储通知
        db.put(NOTICE_PREFIX + noticeId, notice);

        // 用户通知索引（按时间倒序）
        String userNoticeKey = USER_NOTICE_INDEX_PREFIX + targetUser + ":" + (Long.MAX_VALUE - notice.getCreateTime()) + ":" + noticeId;
        db.put(userNoticeKey, noticeId);

        // 更新用户未读通知数
        long unreadCount = db.getLongValue(USER_NOTICE_UNREAD_PREFIX + targetUser) + 1;
        db.put(USER_NOTICE_UNREAD_PREFIX + targetUser, unreadCount);

        log.info("发送通知: noticeId={}, targetUser={}, fromUser={}, type={}, postId={}", noticeId, targetUser, fromUser, type, postId);
    }

    /** 更新通知中的用户昵称 */
    private void updateNoticeNicknames(Notice notice) {
        if (notice != null && notice.getFromUser() != null) {
            User user = db.getObject(USER_PREFIX + notice.getFromUser(), User.class);
            if (user != null) {
                if (user.getNickname() != null && !user.getNickname().isBlank()) {
                    notice.setFromNickname(user.getNickname());
                }
                if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
                    notice.setAvatar(user.getAvatar());
                }
            }
        }
    }

    /** 获取用户通知列表 */
    public List<Notice> listNotices(String username, int page, int size) {
        int offset = (page - 1) * size;
        String startKey = USER_NOTICE_INDEX_PREFIX + username + ":";

        List<Notice> notices = new ArrayList<>();
        int skipped = 0;
        int collected = 0;

        try (RocksIterator iterator = db.getDb().newIterator()) {
            iterator.seek(startKey.getBytes(StandardCharsets.UTF_8));
            while (iterator.isValid() && collected < size) {
                String key = new String(iterator.key(), StandardCharsets.UTF_8);
                if (!key.startsWith(startKey)) {
                    break;
                }
                String[] parts = key.substring(startKey.length()).split(":");
                if (parts.length >= 2) {
                    long noticeId = Long.parseLong(parts[parts.length - 1]);
                    if (skipped < offset) {
                        skipped++;
                    } else {
                        Notice notice = db.getObject(NOTICE_PREFIX + noticeId, Notice.class);
                        if (notice != null) {
                            updateNoticeNicknames(notice);
                            notices.add(notice);
                            collected++;
                        }
                    }
                }
                iterator.next();
            }
        } catch (Exception e) {
            log.error("遍历用户通知失败", e);
        }
        return notices;
    }

    /** 获取用户未读通知数 */
    public long getUnreadNoticeCount(String username) {
        return db.getLongValue(USER_NOTICE_UNREAD_PREFIX + username);
    }

    /** 标记通知为已读 */
    public int markNoticesAsRead(String username) {
        List<Notice> notices = listNotices(username, 1, 100);
        int count = 0;
        for (Notice notice : notices) {
            if (!notice.isReaded()) {
                notice.setReaded(true);
                db.put(NOTICE_PREFIX + notice.getId(), notice);
                count++;
            }
        }
        // 重置未读数
        db.put(USER_NOTICE_UNREAD_PREFIX + username, 0L);
        return count;
    }
}
