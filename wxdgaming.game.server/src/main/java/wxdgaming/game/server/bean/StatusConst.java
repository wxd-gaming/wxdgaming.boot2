package wxdgaming.game.server.bean;

import wxdgaming.boot2.core.lang.bit.BitFlag;
import wxdgaming.boot2.core.lang.bit.BitFlagGroup;

/**
 * 状态
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 19:12
 **/
public interface StatusConst {

    /** 在线 */
    BitFlagGroup Online = new BitFlagGroup(1, 1, 4);
    /** 离线 */
    BitFlagGroup Offline = new BitFlagGroup(2, 1, 4);

    /** 切换地图中 */
    BitFlagGroup ChangeMap = new BitFlagGroup(5, 5, 6);
    /** 已经在地图中 */
    BitFlagGroup JoinMap = new BitFlagGroup(6, 5, 6);
    /** 禁止切换地图 */
    BitFlagGroup BanChangeMap = new BitFlagGroup(20);
    /** 禁止攻击 */
    BitFlagGroup BanAttack = new BitFlagGroup(21);
    /** 禁止被攻击 */
    BitFlagGroup BanBeAttack = new BitFlagGroup(22);
    /** 禁止拾取 */
    BitFlagGroup BanPickUp = new BitFlagGroup(23);
    /** 禁止瞬移，位移 */
    BitFlagGroup BanShift = new BitFlagGroup(24);
    /** 禁止移动 */
    BitFlagGroup BanMove = new BitFlagGroup(25);
    /** 禁止暴击 */
    BitFlagGroup Ban暴击 = new BitFlagGroup(26);
    /** 禁止被暴击 */
    BitFlagGroup Ban被暴击 = new BitFlagGroup(27);

    static void addStatus(BitFlag bitFlag, BitFlagGroup... bitFlagGroups) {
        for (BitFlagGroup bitFlagGroup : bitFlagGroups) {
            bitFlag.addFlagRange(bitFlagGroup.getIndex(), bitFlagGroup.getEnd(), bitFlagGroup.getFlag());
        }
    }

    static void removeStatus(BitFlag bitFlag, BitFlagGroup... bitFlagGroups) {
        for (BitFlagGroup bitFlagGroup : bitFlagGroups) {
            bitFlag.removeFlagRange(bitFlagGroup.getIndex(), bitFlagGroup.getEnd());
        }
    }

}
