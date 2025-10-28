package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.batis.EntityIntegerUID;
import wxdgaming.boot2.starter.batis.sql.WebSqlQueryCondition;
import wxdgaming.game.login.entity.GiftCodeEntity;
import wxdgaming.game.login.giftcode.GiftCodeService;
import wxdgaming.game.server.bean.GameCfgFunction;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.util.Util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 礼包码接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 10:57
 **/
@Slf4j
@RestController
@RequestMapping("/admin/giftCode")
public class AdminGiftCodeController implements InitPrint {

    final GiftCodeService giftCodeService;

    public AdminGiftCodeController(GiftCodeService giftCodeService) {
        this.giftCodeService = giftCodeService;
    }

    @RequestMapping(value = "/edit")
    public RunResult add(CacheHttpServletRequest context,
                         @RequestParam(value = "uid", required = false) String uidStr,
                         @RequestParam(value = "code", required = false) String code,
                         @RequestParam("rewards") String rewards,
                         @RequestParam("validation") String validation,
                         @RequestParam("comment") String comment,
                         @RequestParam("startTime") String startTime,
                         @RequestParam("endTime") String endTime) {
        int uid = 0;
        if (StringUtils.isNotBlank(uidStr)) {
            uid = NumberUtil.parseInt(uidStr, 0);
        }
        Collection<GiftCodeEntity> list = giftCodeService.getDataTable().getList();
        int maxUid = list.stream().mapToInt(EntityIntegerUID::getUid).max().orElse(0);
        GiftCodeEntity giftCodeEntity = giftCodeService.getDataTable().get(uid);
        if (giftCodeEntity == null) {
            giftCodeEntity = new GiftCodeEntity();
            giftCodeEntity.setUid(maxUid + 1);
            giftCodeEntity.setCreateTime(MyClock.millis());
        }
        AssertUtil.isTrue(StringUtils.isNotBlank(comment), "礼包码说明不能空");
        checkRewardsConfig(rewards);
        if (StringUtils.isNotBlank(code)) {
            final int fuid = uid;
            if (list.stream().anyMatch(entity -> entity.getUid() != fuid && Objects.equals(code.toUpperCase(), entity.getCode().toUpperCase()))) {
                throw new IllegalArgumentException("固定礼包码重复");
            }
        }
        giftCodeEntity.setCode(code);

        giftCodeEntity.setRewards(rewards);
        giftCodeEntity.setValidation(validation);
        giftCodeEntity.setComment(comment);
        if (StringUtils.isBlank(startTime)) {
            giftCodeEntity.setStartTime(0);
        } else {
            giftCodeEntity.setStartTime(Util.parseWebDate(startTime));
        }
        if (StringUtils.isBlank(endTime)) {
            giftCodeEntity.setEndTime(0);
        } else {
            giftCodeEntity.setEndTime(Util.parseWebDate(endTime));
        }

        this.giftCodeService.getSqlDataHelper().save(giftCodeEntity);
        this.giftCodeService.getDataTable().loadAll();
        return RunResult.ok().msg("成功");
    }

    @RequestMapping(value = "/del")
    public RunResult del(CacheHttpServletRequest context, @RequestParam(value = "uid") int uid) {
        this.giftCodeService.del(uid);
        return RunResult.ok().msg("删除成功");
    }

    void checkRewardsConfig(String rewards) {
        if (StringUtils.isBlank(rewards)) {
            throw new IllegalArgumentException("礼包奖励道具不能空");
        }
        try {
            List<ItemCfg> rewardList = GameCfgFunction.ItemCfgFunction.apply(rewards);
            for (ItemCfg itemCfg : rewardList) {
                if (itemCfg.getCfgId() < 0 || itemCfg.getNum() < 0) {
                    throw new IllegalArgumentException("礼包码奖励配置异常");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("礼包码奖励配置异常");
        }
    }

    @RequestMapping("/gainCode")
    public RunResult gainCode(CacheHttpServletRequest request, @RequestParam("uid") int uid, @RequestParam("num") int num) {
        return giftCodeService.gain(uid, num);
    }

    @RequestMapping("/queryCode")
    public RunResult queryCode(CacheHttpServletRequest request, @RequestParam("uid") int uid) {
        return giftCodeService.queryByUid(uid);
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryList(@RequestBody WebSqlQueryCondition condition) {
        int pageIndex = condition.getPageIndex();
        int pageSize = condition.getPageSize();
        if (pageIndex < 1) pageIndex = 1;
        if (pageSize < 10) pageSize = 10;

        int skip = (pageIndex - 1) * pageSize;

        Collection<GiftCodeEntity> giftCodeEntities = giftCodeService.getDataTable().getList();
        List<JSONObject> list = giftCodeEntities.stream()
                .distinct()
                .skip(skip)
                .limit(pageSize)
                .map(entity -> {
                    JSONObject jsonObject = entity.toJSONObject();
                    jsonObject.put("createTime", Util.formatWebDate(entity.getCreateTime()));
                    jsonObject.put("startTime", Util.formatWebDate(entity.getStartTime()));
                    jsonObject.put("endTime", Util.formatWebDate(entity.getEndTime()));
                    return jsonObject;
                })
                .toList();
        return RunResult.ok().fluentPut("rowCount", list.size()).data(list);
    }

}
