package wxdgaming.boot2.core;

/** 传递参数提取 */
public final class HolderArgument {

    private final Object[] arguments;
    private int argumentIndex = 0;

    public HolderArgument(Object[] arguments) {
        this.arguments = arguments;
    }

    @SuppressWarnings("unchecked")
    public <R> R next() {
        return (R) arguments[argumentIndex++];
    }

}
