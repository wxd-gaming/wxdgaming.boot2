package wxdgaming.boot2.starter.batis;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 13:01
 **/
@Getter
public abstract class DDLBuilder {

    protected final Map<Class<? extends Entity>, TableMapping> tableMappings = new ConcurrentHashMap<>();

    public TableMapping tableMapping(Class<? extends Entity> cls) {
        if (!Entity.class.isAssignableFrom(cls)) throw new IllegalArgumentException("cls must be Entity");
        return tableMappings.computeIfAbsent(cls, l -> new TableMapping(cls));
    }

    public abstract Object[] buildKeyParams(TableMapping tableMapping, Entity bean);

    public abstract Object[] buildInsertParams(TableMapping tableMapping, Entity bean);

    public abstract Object[] builderUpdateParams(TableMapping tableMapping, Entity bean);

}
