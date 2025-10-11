package wxdgaming.game.login.cdkey;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.CDKeyUtil;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.login.cfg.QCdkeyTable;
import wxdgaming.game.login.cfg.bean.QCdkey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 激活码服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 09:18
 **/
@Slf4j
@Service
public class CDKeyService implements InitPrint {

    final MapDBDataHelper mapDBDataHelper;
    final DataRepository dataRepository;

    public CDKeyService(DataRepository dataRepository, MapDBDataHelper mapDBDataHelper) {
        this.dataRepository = dataRepository;
        this.mapDBDataHelper = mapDBDataHelper;
    }

    public RunResult gain(int cdKeyId, int num) {
        final String lockKey = "cdkey:" + cdKeyId;
        SingletonLockUtil.lock(lockKey);
        try {
            QCdkey cdKeyEntity = dataRepository.dataTable(QCdkeyTable.class, cdKeyId);
            if (cdKeyEntity == null) {
                return RunResult.fail("激活码不存在");
            }
            if (StringUtils.isNotBlank(cdKeyEntity.getCode())) {
                return RunResult.fail("通用激活码无需配置");
            }
            Collection<String> strings = CDKeyUtil.cdKey(cdKeyId, num);
            HoldMap cdkeyMap = this.mapDBDataHelper.bMap("cdkey:" + cdKeyId);
            for (String string : strings) {
                cdkeyMap.put(string, 1);
            }
            return RunResult.ok().data(strings);
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

    public RunResult use(String key, int sid, String account, long roleId, String roleName) {
        key = key.toUpperCase();
        int cdKeyId = CDKeyUtil.getCdKeyId(key);
        final String lockKey = "cdkey:" + cdKeyId;
        SingletonLockUtil.lock(lockKey);
        try {
            QCdkeyTable qCdkeyTable = dataRepository.dataTable(QCdkeyTable.class);
            QCdkey cdKeyEntity = qCdkeyTable.getCodeMap().get(key);
            if (cdKeyEntity == null) {
                /*通用礼包码不存在，查找特定礼包码*/
                cdKeyEntity = dataRepository.dataTable(QCdkeyTable.class, cdKeyId);
            }
            if (cdKeyEntity == null) {
                return RunResult.fail("cdkey 不存在");
            }
            if (StringUtils.isBlank(cdKeyEntity.getCode())) {
                /*特定礼包码*/
                HoldMap cdkeyMap = this.mapDBDataHelper.bMap("cdkey:" + cdKeyId);
                Integer used = cdkeyMap.get(key);
                if (used == null || used < 1)
                    return RunResult.fail("激活码已使用");
                used = used - 1;
                if (used < 1) {
                    cdkeyMap.remove(key);
                } else {
                    cdkeyMap.put(key, used);
                }
            }

            RunResult runResult = RunResult.ok()
                    .fluentPut("cid", cdKeyEntity.getId())
                    .fluentPut("validate", cdKeyEntity.getValidation())
                    .fluentPut("comment", cdKeyEntity.getComment())
                    .fluentPut("rewards", cdKeyEntity.getRewards());
            log.info("使用cdkey {}, sid={}, account={}, roleId={}, roleName={}, info={}", key, sid, account, roleId, roleName, runResult.toJSONString());
            return runResult;
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

    public RunResult queryByUid(int cdKeyId) {
        final String lockKey = "cdkey:" + cdKeyId;
        SingletonLockUtil.lock(lockKey);
        try {
            QCdkeyTable qCdkeyTable = dataRepository.dataTable(QCdkeyTable.class);
            QCdkey cdKeyEntity = qCdkeyTable.getDataMap().get(cdKeyId);
            if (cdKeyEntity == null) {
                return RunResult.fail("激活码不存在");
            }
            if (StringUtils.isNotBlank(cdKeyEntity.getCode())) {
                return RunResult.ok().data(List.of(cdKeyEntity.getCode()));
            }
            HoldMap cdkeyMap = this.mapDBDataHelper.bMap("cdkey:" + cdKeyId);
            ArrayList<String> strings = new ArrayList<>(cdkeyMap.keys());
            return RunResult.ok().data(strings);
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

}
