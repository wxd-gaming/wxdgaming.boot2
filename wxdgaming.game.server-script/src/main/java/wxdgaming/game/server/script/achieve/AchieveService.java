package wxdgaming.game.server.script.achieve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.GameCfgFunction;
import wxdgaming.game.server.bean.goods.BagChangeDTO4ItemCfg;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.cfg.QAchieveTable;
import wxdgaming.game.cfg.bean.QAchieve;
import wxdgaming.game.server.bean.achieve.AchievePack;
import wxdgaming.game.server.bean.achieve.AchieveProgress;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.event.OnTask;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 成就
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 15:01
 **/
@Slf4j
@Service
public class AchieveService implements InitPrint {

    final DataRepository dataRepository;
    final TipsService tipsService;
    final BagService bagService;

    public AchieveService(DataRepository dataRepository, TipsService tipsService, BagService bagService) {
        this.dataRepository = dataRepository;
        this.tipsService = tipsService;
        this.bagService = bagService;
    }

    public void onLoginBefore(EventConst.LoginBeforePlayerEvent event) {
        Player player = event.player();
        QAchieveTable qAchieveTable = this.dataRepository.dataTable(QAchieveTable.class);
        HashMap<Integer, Condition> typeMap = qAchieveTable.getTypeMap();
        AchievePack achievePack = player.getAchievePack();
        for (Map.Entry<Integer, Condition> conditionEntry : typeMap.entrySet()) {
            achievePack.getAchieveMap().computeIfAbsent(conditionEntry.getKey(), k -> new AchieveProgress());
        }

        List<ItemCfg> rewards = qAchieveTable.getDataMap().get(1).getRewards().get(GameCfgFunction.ItemCfgFunction);

    }

    @OnTask
    public void update(Player player, Condition condition) {
        QAchieveTable qAchieveTable = this.dataRepository.dataTable(QAchieveTable.class);
        AchievePack achievePack = player.getAchievePack();
        HashSet<Integer> changed = new HashSet<>();
        for (Map.Entry<Integer, AchieveProgress> achieveProgressEntry : achievePack.getAchieveMap().entrySet()) {
            AchieveProgress achieveProgress = achieveProgressEntry.getValue();
            Condition achieveCondition = qAchieveTable.getTypeMap().get(achieveProgressEntry.getKey());
            if (achieveCondition.equals(condition)) {
                long old = achieveProgress.getProgress();
                achieveProgress.setProgress(achieveCondition.getUpdate().update(old, condition.getTarget()));
                if (achieveProgress.getProgress() != old) {
                    changed.add(achieveProgressEntry.getKey());
                }
            }
        }
        for (Integer type : changed) {
            /*推送成就变更*/
        }
    }

    public void rewards(Player player, int achieveId) {
        QAchieveTable qAchieveTable = this.dataRepository.dataTable(QAchieveTable.class);
        QAchieve qAchieve = qAchieveTable.get(achieveId);
        if (qAchieve == null) {
            tipsService.tips(player, "成就不存在");
            return;
        }

        AchieveProgress achieveProgress = player.getAchievePack().getAchieveMap().get(qAchieve.getType());
        if (achieveProgress == null || qAchieve.getCondition().getTarget() >= achieveProgress.getProgress()) {
            tipsService.tips(player, "成就未完成");
            return;
        }

        if (achieveProgress.getRewardIdList().contains(achieveId)) {
            tipsService.tips(player, "已经领取奖励");
            return;
        }

        List<ItemCfg> itemCfgs = qAchieve.getRewards().get(GameCfgFunction.ItemCfgFunction);
        ReasonDTO reasonDTO = ReasonDTO.of(ReasonConst.Achieve, "achieveId=" + achieveId);
        BagChangeDTO4ItemCfg bagChangeDTO4ItemCfg = BagChangeDTO4ItemCfg.builder()
                .setReasonDTO(reasonDTO)
                .setItemCfgList(itemCfgs)
                .setBagFullSendMail(true)
                .build();

        achieveProgress.getRewardIdList().add(achieveId);
        log.info("{} 领取成就奖励, {}, itemCfgs={}", player, reasonDTO, itemCfgs);
        bagService.gainItemCfg(player, bagChangeDTO4ItemCfg);

    }

}
