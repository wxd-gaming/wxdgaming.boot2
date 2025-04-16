package wxdgaming.boot2.starter.excel.store;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.reflect.ReflectContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据仓库
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-09 11:35
 **/
@Slf4j
@Getter
@Accessors(chain = true)
@Singleton
public class DataRepository {
    private String jsonPath;
    private String scanPackageName;
    @Setter private ClassLoader classLoader;
    /** 存储数据表 */
    private Map<Class<?>, DataTable<?>> dataTableMap = new ConcurrentHashMap<>();

    public <D extends DataKey, T extends DataTable<D>> T dataTable(Class<T> dataTableClass) {
        return (T) dataTableMap.computeIfAbsent(dataTableClass, k -> buildDataTable(k));
    }

    public <T extends DataTable, E> E dataTable(Class<T> dataTableClass, Object key) {
        return (E) (dataTableMap.computeIfAbsent(dataTableClass, k -> buildDataTable(k)).get(key));
    }

    @Start
    @Order(1)
    public void load(@Value(path = "data.json.path", nestedPath = true, required = false) String jsonPath,
                     @Value(path = "data.json.scan", nestedPath = true, required = false) String scanPackageName) {
        this.jsonPath = jsonPath;
        this.scanPackageName = scanPackageName;

        if (StringUtils.isBlank(jsonPath) || StringUtils.isBlank(scanPackageName)) {
            log.warn("扫描器异常：{}, {}", jsonPath, scanPackageName);
            return;
        }
        Map<Class<?>, DataTable<?>> tmpDataTableMap = new ConcurrentHashMap<>();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        ReflectContext reflectContext = ReflectContext.Builder.of(classLoader, scanPackageName).build();
        reflectContext.classWithSuper(DataTable.class, null)
                .forEach(dataTableClass -> {
                    try {
                        DataTable<?> dataTable = buildDataTable(dataTableClass);
                        tmpDataTableMap.put(dataTableClass, dataTable);
                    } catch (Exception e) {
                        log.error("load data table error", e);
                    }
                });
        for (DataTable<?> dataTable : tmpDataTableMap.values()) {
            dataTable.checkData(tmpDataTableMap);
        }
        dataTableMap = tmpDataTableMap;
    }

    public void reload(Class<?> dataTableClass) {
        DataTable<?> dataTable = buildDataTable(dataTableClass);
        dataTableMap.put(dataTableClass, dataTable);
    }

    DataTable<?> buildDataTable(Class<?> dataTableClass) {
        DataTable<?> dataTable = null;
        try {
            dataTable = (DataTable<?>) dataTableClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dataTable.loadJson(jsonPath);
        log.info("load data table 文件：{}, 数据：{}, 行数：{}", dataTable.getDataMapping().excelPath(), dataTable.getDataMapping().name(), dataTable.dbSize());
        return dataTable;
    }

}
