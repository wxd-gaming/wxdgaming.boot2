package code.event.impl;

import code.event.EventObject;
import lombok.Builder;
import lombok.Getter;

/**
 * 多参数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-23 15:08
 **/
@Getter
@Builder
public class MultipartEvent extends EventObject {

    private final String message;
    private final int number;

}
