package wxdgaming.boot2.starter.batis.build;

public interface IColumnFactory {
    void register(Class<?> type, IBuildColumn buildColumn);
}
