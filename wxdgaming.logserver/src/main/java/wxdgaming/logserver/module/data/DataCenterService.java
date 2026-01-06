package wxdgaming.logserver.module.data;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.bean.LogMappingInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * 数据中心
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:53
 **/
@Slf4j
@Getter
@Service
public class DataCenterService implements InitPrint {

    final PgsqlDataHelper sqlDataHelper;
    Map<String, LogMappingInfo> logMappingInfoMap = Collections.emptyMap();
    List<LogMappingInfo> logMappingInfoList = Collections.emptyList();

    public DataCenterService(PgsqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @EventListener
    public void start(StartEvent event) {
        log.info("DataCenterService start");
        initLogTable();
    }

    @ExecutorWith(useVirtualThread = true)
    @Scheduled(value = "0 0 0 * * ?")
    public void initLogTable() {
        Map<String, String> dbTableMap = sqlDataHelper.findTableMap();
        Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = sqlDataHelper.findTableStructMap();

        Map<String, LogMappingInfo> tmp = new LinkedHashMap<>();
        Stream<Tuple2<Path, byte[]>> tuple2Stream = FileUtil.resourceStreams(this.getClass().getClassLoader(), "log-init", ".json");
        tuple2Stream.forEach(tuple2 -> {
            String json = new String(tuple2.getRight(), StandardCharsets.UTF_8);
            LogMappingInfo logMappingInfo = FastJsonUtil.parse(json, LogMappingInfo.class);
            String tableName = logMappingInfo.getLogName();
            String tableComment = logMappingInfo.getLogComment();
            TableMapping tableMapping = sqlDataHelper.tableMapping(LogEntity.class);
            checkSLogTable(sqlDataHelper, dbTableMap, tableStructMap, tableMapping, logMappingInfo.isPartition(), tableName, tableComment);
            tmp.put(tableName, logMappingInfo);
        });
        logMappingInfoList = tmp.values().stream().sorted(Comparator.comparing(LogMappingInfo::getSort)).toList();
        logMappingInfoMap = tmp;
    }

    public void checkSLogTable(PgsqlDataHelper dataHelper,
                               Map<String, String> dbTableMap,
                               Map<String, LinkedHashMap<String, JSONObject>> tableStructMap,
                               TableMapping tableMapping,
                               boolean checkPartition,
                               String tableName,
                               String tableComment) {

        dataHelper.checkTable(tableStructMap, tableMapping, tableName, tableComment, checkPartition);
        if (checkPartition) {
            /*TODO 处理分区表 */
            StringBuilder sb = new StringBuilder();
            LocalDateTime now = LocalDateTime.now();
            int beforeDay = 121;
            LocalDateTime localDate = now.plusDays(-beforeDay);
            for (int i = 0; i <= beforeDay; i++) {
                /*创建表分区*/
                String from = MyClock.formatDate("yyyyMMdd", localDate.plusDays(i));
                String to = MyClock.formatDate("yyyyMMdd", localDate.plusDays(i + 1));

                String partition_table_name = tableName + "_" + from;
                if (dbTableMap.containsKey(partition_table_name)) {
                    continue;
                }
                sb.append(dataHelper.buildPartition(tableName, from, to)).append("\n");
            }
            if (!sb.isEmpty()) {
                dataHelper.executeUpdate(sb.toString());
            }
        }
    }
}
