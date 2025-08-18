package wxdgaming.game.server.bean.equip;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 装备常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-02 11:22
 **/
public interface EquipConst {

    /** 装备面板 , 比如普通装备，时装，神装， */
    @Getter
    enum EquipPanel {
        /** 普通装备 */
        Normal(1),
        ;

        private static final Map<Integer, EquipPanel> codeMap = MapOf.ofMap(EquipPanel::getCode, EquipPanel.values());

        public static EquipPanel ofCode(int code) {
            return codeMap.get(code);
        }

        private final int code;

        EquipPanel(int code) {
            this.code = code;
        }

    }

    @Getter
    enum EquipPost {
        /** 头部 */
        HEAD(1),
        /** 肩膀 */
        SHOULDER(2),
        /** 手部 */
        HAND(3),
        /** 胸部 */
        WAIST(4),
        /** 腿部 */
        LEG(5),
        /** 脚部 */
        FEET(6),
        /** 戒指 */
        RING(7),
        ;

        private static final Map<Integer, EquipPost> codeMap = MapOf.ofMap(EquipPost::getCode, EquipPost.values());

        public static EquipPost ofCode(int code) {
            return codeMap.get(code);
        }

        private final int code;

        EquipPost(int code) {
            this.code = code;
        }

    }

}
