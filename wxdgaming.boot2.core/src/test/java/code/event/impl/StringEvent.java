package code.event.impl;

import lombok.Getter;

import java.util.EventObject;

/**
 * 字符串事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-22 17:29
 **/
@Getter
public class StringEvent extends EventObject {

    private final String message;

    public StringEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

}
