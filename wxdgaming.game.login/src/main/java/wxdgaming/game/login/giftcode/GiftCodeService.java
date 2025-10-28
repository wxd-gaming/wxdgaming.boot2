package wxdgaming.game.login.giftcode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.GiftCodeUtil;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.DataTable;
import wxdgaming.boot2.starter.batis.mapdb.HoldMap;
import wxdgaming.boot2.starter.batis.mapdb.MapDBDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.entity.GiftCodeEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 礼包码服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 09:18
 **/
@Slf4j
@Getter
@Service
public class GiftCodeService implements InitPrint {

    final static String key_prefix = "gift-key:";

    final SqlDataHelper sqlDataHelper;
    final MapDBDataHelper mapDBDataHelper;
    final DataTable<GiftCodeEntity> dataTable;

    public GiftCodeService(PgsqlDataHelper sqlDataHelper, MapDBDataHelper mapDBDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        this.mapDBDataHelper = mapDBDataHelper;
        this.dataTable = new DataTable<>(GiftCodeEntity.class, sqlDataHelper,
                GiftCodeEntity::getUid,
                entity -> {
                    if (StringUtils.isNotBlank(entity.getCode()))
                        return entity.getCode().toUpperCase();
                    return null;
                }
        );
    }

    @EventListener
    public void startEvent(StartEvent event) {
        dataTable.loadAll();
    }

    public void del(int uid) {
        final String lockKey = key_prefix + uid;
        SingletonLockUtil.lock(lockKey);
        try {
            GiftCodeEntity giftCodeEntity = dataTable.get(uid);
            if (giftCodeEntity == null) {
                return;
            }
            HoldMap giftCodeMap = this.mapDBDataHelper.bMap(lockKey);
            giftCodeMap.clear();
            sqlDataHelper.deleteByKey(GiftCodeEntity.class, uid);
            dataTable.loadAll();
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

    public RunResult gain(int uid, int num) {
        final String lockKey = key_prefix + uid;
        SingletonLockUtil.lock(lockKey);
        try {
            GiftCodeEntity giftCodeEntity = dataTable.get(uid);
            if (giftCodeEntity == null) {
                return RunResult.fail("礼包码不存在");
            }
            if (StringUtils.isNotBlank(giftCodeEntity.getCode())) {
                return RunResult.fail("通用礼包码无需配置");
            }
            Collection<String> strings = GiftCodeUtil.giftCode(uid, num);
            HoldMap giftCodeMap = this.mapDBDataHelper.bMap(lockKey);
            for (String string : strings) {
                giftCodeMap.put(string, 1);
            }
            return RunResult.ok().data(strings);
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

    public RunResult use(String key, int sid, String account, long roleId, String roleName) {
        key = key.toUpperCase();
        int giftCodeId = GiftCodeUtil.getGiftCodeId(key);
        final String lockKey = key_prefix + giftCodeId;
        SingletonLockUtil.lock(lockKey);
        try {
            GiftCodeEntity giftCodeEntity = dataTable.get(key);
            if (giftCodeEntity == null) {
                /*通用礼包码不存在，查找特定礼包码*/
                giftCodeEntity = dataTable.get(giftCodeId);
            }
            if (giftCodeEntity == null) {
                return RunResult.fail("礼包码不存在");
            }
            if (StringUtils.isBlank(giftCodeEntity.getCode())) {
                /*特定礼包码*/
                HoldMap giftCodeMap = this.mapDBDataHelper.bMap(lockKey);
                Integer used = giftCodeMap.get(key);
                if (used == null || used < 1)
                    return RunResult.fail("礼包码已使用");
                used = used - 1;
                if (used < 1) {
                    giftCodeMap.remove(key);
                } else {
                    giftCodeMap.put(key, used);
                }
            }

            RunResult runResult = RunResult.ok()
                    .fluentPut("cid", giftCodeEntity.getUid())
                    .fluentPut("validate", giftCodeEntity.getValidation())
                    .fluentPut("comment", giftCodeEntity.getComment())
                    .fluentPut("rewards", giftCodeEntity.getRewards());
            log.info("使用礼包码 {}, sid={}, account={}, roleId={}, roleName={}, info={}", key, sid, account, roleId, roleName, runResult.toJSONString());
            return runResult;
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

    public RunResult queryByUid(int uid) {
        final String lockKey = key_prefix + uid;
        SingletonLockUtil.lock(lockKey);
        try {
            GiftCodeEntity giftCodeEntity = dataTable.get(uid);
            if (giftCodeEntity == null) {
                return RunResult.fail("礼包码不存在");
            }
            if (StringUtils.isNotBlank(giftCodeEntity.getCode())) {
                return RunResult.ok().data(List.of(giftCodeEntity.getCode()));
            }
            HoldMap giftCodeMap = this.mapDBDataHelper.bMap(lockKey);
            ArrayList<String> strings = new ArrayList<>(giftCodeMap.keys());
            return RunResult.ok().data(strings);
        } finally {
            SingletonLockUtil.unlock(lockKey);
        }
    }

}
