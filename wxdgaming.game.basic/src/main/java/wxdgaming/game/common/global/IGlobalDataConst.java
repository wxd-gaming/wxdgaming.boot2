package wxdgaming.game.common.global;

public interface IGlobalDataConst {
    int getCode();

    Class<? extends wxdgaming.game.bean.global.AbstractGlobalData> getCls();

    String getComment();

    java.util.function.Supplier<wxdgaming.game.bean.global.AbstractGlobalData> getFactory();
}
