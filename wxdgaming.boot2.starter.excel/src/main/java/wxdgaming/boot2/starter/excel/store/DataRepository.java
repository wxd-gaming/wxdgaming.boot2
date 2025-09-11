package wxdgaming.boot2.starter.excel.store;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据仓库
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-09 11:35
 **/
@Slf4j
@Getter
@Accessors(chain = true)
@Service
public class DataRepository {

    private static final class Lazy {
        private static DataRepository ins = null;
    }

    public static DataRepository getIns() {
        return DataRepository.Lazy.ins;
    }

    @Setter private ClassLoader classLoader;
    /** 存储数据表 */
    private Map<Class<?>, DataTable<?>> dataTableMap = new ConcurrentHashMap<>();
    private final DataRepositoryProperties dataRepositoryProperties;

    public DataRepository(DataRepositoryProperties dataRepositoryProperties) {
        this.dataRepositoryProperties = dataRepositoryProperties;
        loadAll();
        Lazy.ins = this;
    }

    @SuppressWarnings("unchecked")
    public <D extends DataKey, T extends DataTable<D>> T dataTable(Class<T> dataTableClass) {
        return (T) dataTableMap.get(dataTableClass);
    }

    public <E extends DataKey, T extends DataTable<E>> E dataTable(Class<T> dataTableClass, Object key) {
        return dataTable(dataTableClass).get(key);
    }

    public void loadAll() {
        if (StringUtils.isBlank(dataRepositoryProperties.getPath()) || StringUtils.isBlank(dataRepositoryProperties.getScan())) {
            log.debug("扫描器异常：{}, {}", dataRepositoryProperties.getPath(), dataRepositoryProperties.getScan());
            return;
        }
        Map<Class<?>, DataTable<?>> tmpDataTableMap = new ConcurrentHashMap<>();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        ReflectProvider reflectProvider = ReflectProvider.Builder.of(classLoader, dataRepositoryProperties.getScan()).build();
        reflectProvider.classWithSuper(DataTable.class)
                .forEach(dataTableClass -> {
                    DataTable<?> dataTable = buildDataTable(dataTableClass);
                    tmpDataTableMap.put(dataTableClass, dataTable);
                });
        for (DataTable<?> dataTable : tmpDataTableMap.values()) {
            for (DataKey row : dataTable.getDataList()) {
                if (row instanceof DataChecked dataChecked) {
                    try {
                        dataChecked.initAndCheck(tmpDataTableMap);
                    } catch (Exception e) {
                        throw Throw.of(row.toString(), e);
                    }
                }
            }
        }
        for (DataTable<?> dataTable : tmpDataTableMap.values()) {
            dataTable.checkData(tmpDataTableMap);
        }
        if (tmpDataTableMap.isEmpty()) {
            log.error("扫描器异常：{}, {}, 没有任何类", dataRepositoryProperties.getPath(), dataRepositoryProperties.getScan());
        }
        dataTableMap = tmpDataTableMap;
    }

    private DataTable<?> buildDataTable(Class<?> dataTableClass) {
        DataTable<?> dataTable = null;
        try {
            dataTable = (DataTable<?>) dataTableClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dataTable.loadJson(dataRepositoryProperties.getPath());
        log.info("load data table 文件：{}, 数据：{}, 行数：{}", dataTable.getDataMapping().excelPath(), dataTable.getDataMapping().name(), dataTable.dbSize());
        return dataTable;
    }

}
