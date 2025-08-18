package code.event;

/**
 * 事件类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-23 11:18
 **/
public interface EventType {

    public static EventType build(String comment) {
        return new EventType() {
            @Override public String comment() {
                return comment;
            }
        };
    }

    String comment();

}


