package code.event;

public enum EventTypeConst implements EventType {
    /**
     * 字符串事件
     */
    ServerInit("字符串事件"),
    MultipartInit("多参数事件"),
    ;

    private final String comment;

    EventTypeConst(String comment) {
        this.comment = comment;
    }


    @Override public String comment() {
        return comment;
    }

}
