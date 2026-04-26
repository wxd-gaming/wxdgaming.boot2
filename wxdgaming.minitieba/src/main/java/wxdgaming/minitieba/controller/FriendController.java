package wxdgaming.minitieba.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.minitieba.bean.FriendRequest;
import wxdgaming.minitieba.service.FriendService;

import java.util.List;

/**
 * 好友管理API
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /** 发送好友申请 */
    @PostMapping("/request")
    public RunResult sendFriendRequest(@RequestBody FriendRequestDTO dto) {
        return friendService.sendFriendRequest(dto.getUsername(), dto.getTo());
    }

    /** 处理好友申请 */
    @PostMapping("/handle")
    public RunResult handleFriendRequest(@RequestBody HandleRequestDTO dto) {
        return friendService.handleFriendRequest(dto.getUsername(), dto.getRequestId(), dto.isAccept());
    }

    /** 删除好友 */
    @PostMapping("/remove")
    public RunResult removeFriend(@RequestBody RemoveFriendDTO dto) {
        return friendService.removeFriend(dto.getUsername(), dto.getFriend());
    }

    /** 获取好友列表 */
    @GetMapping("/list")
    public RunResult listFriends(@RequestParam String username) {
        return RunResult.ok().data(friendService.listFriends(username));
    }

    /** 获取收到的好友申请列表 */
    @GetMapping("/requests")
    public RunResult listFriendRequests(@RequestParam String username) {
        List<FriendRequest> requests = friendService.listReceivedRequests(username);
        return RunResult.ok().data(requests);
    }

    /** 拉黑用户 */
    @PostMapping("/block")
    public RunResult blockUser(@RequestBody BlockUserDTO dto) {
        return friendService.blockUser(dto.getUsername(), dto.getBlocked());
    }

    /** 取消拉黑 */
    @PostMapping("/unblock")
    public RunResult unblockUser(@RequestBody BlockUserDTO dto) {
        return friendService.unblockUser(dto.getUsername(), dto.getBlocked());
    }

    /** 获取黑名单列表 */
    @GetMapping("/blacklist")
    public RunResult listBlacklist(@RequestParam String username) {
        return RunResult.ok().data(friendService.listBlacklist(username));
    }

    /** 检查是否是好友 */
    @GetMapping("/check")
    public RunResult checkFriend(@RequestParam String username, @RequestParam String friend) {
        return RunResult.ok().data(friendService.isFriend(username, friend));
    }

    /** 检查是否在黑名单 */
    @GetMapping("/blacklist/check")
    public RunResult checkBlacklist(@RequestParam String username, @RequestParam String blocked) {
        return RunResult.ok().data(friendService.isBlacklisted(username, blocked));
    }

    /** 标记好友申请为已读 */
    @PostMapping("/requests/read")
    public RunResult markRequestsRead(@RequestBody MarkReadDTO dto) {
        friendService.markRequestsAsRead(dto.getUsername());
        return RunResult.ok();
    }

    // ========== DTO 内部类 ==========
    @Data
    public static class FriendRequestDTO {
        private String username;
        private String to;
    }

    @Data
    public static class HandleRequestDTO {
        private String username;
        private long requestId;
        private boolean accept;
    }

    @Data
    public static class RemoveFriendDTO {
        private String username;
        private String friend;
    }

    @Data
    public static class BlockUserDTO {
        private String username;
        private String blocked;
    }

    @Data
    public static class MarkReadDTO {
        private String username;
    }
}
