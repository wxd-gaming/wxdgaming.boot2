package wxdgaming.game.common.bean.global;

public interface IGlobalDataConst {
    int getCode();

    Class<? extends AbstractGlobalData> getCls();

    String getComment();

    java.util.function.Supplier<AbstractGlobalData> getFactory();
}
