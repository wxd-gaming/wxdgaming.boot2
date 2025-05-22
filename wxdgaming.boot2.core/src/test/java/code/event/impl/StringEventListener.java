package code.event.impl;

import code.event.CustomEventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 字符串监听
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-22 17:30
 **/
@Slf4j
public class StringEventListener extends CustomEventListener<StringEvent> {


    @Override public void onCustomEvent(StringEvent event) {
        log.info("{}", event.getMessage());
    }

}
