package wxdgaming.game.server.bean.ai;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.Vector3D;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前处理器数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-24 16:59
 **/
@Getter
@Setter
public class MoveAiActionData extends AiActionData implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 需要的目标点位 */
    private Vector3D targetPoint = null;
    private List<Vector3D> targetPathList = null;

    public MoveAiActionData(AiAction aiAction) {
        super(aiAction);
    }

}
