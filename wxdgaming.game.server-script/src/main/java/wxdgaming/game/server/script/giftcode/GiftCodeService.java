package wxdgaming.game.server.script.giftcode;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.httpclient5.HttpResponse;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.login.bean.UseGiftCodeDTO;
import wxdgaming.game.message.giftcode.ResUseGiftCode;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.GameCfgFunction;
import wxdgaming.game.server.bean.count.CountData;
import wxdgaming.game.server.bean.count.CountMap;
import wxdgaming.game.server.bean.count.CountValidationType;
import wxdgaming.game.server.bean.goods.BagChangeDTO4Item;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.inner.ConnectLoginService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.game.server.script.validation.ValidationService;

import java.util.HashMap;
import java.util.List;

/**
 * GiftCode
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-29 11:14
 **/
@Slf4j
@Getter
@Service
public class GiftCodeService implements InitPrint {

    final ConnectLoginProperties connectLoginProperties;
    final GameServerProperties gameServerProperties;
    final DataCenterService dataCenterService;
    final TipsService tipsService;
    final BagService bagService;
    final ConnectLoginService connectLoginService;
    final ValidationService validationService;

    public GiftCodeService(ConnectLoginProperties connectLoginProperties, GameServerProperties gameServerProperties,
                           DataCenterService dataCenterService,
                           TipsService tipsService, BagService bagService,
                           ConnectLoginService connectLoginService, ValidationService validationService) {
        this.dataCenterService = dataCenterService;
        this.tipsService = tipsService;
        this.bagService = bagService;
        this.connectLoginProperties = connectLoginProperties;
        this.gameServerProperties = gameServerProperties;
        this.connectLoginService = connectLoginService;
        this.validationService = validationService;
    }

    public void use(Player player, String giftCode) {

        String url = connectLoginProperties.getUrl();
        url = url + "/inner/game/giftCode/use";

        UseGiftCodeDTO useGiftCodeDTO = new UseGiftCodeDTO();
        useGiftCodeDTO.setGiftCode(giftCode);
        useGiftCodeDTO.setSid(gameServerProperties.getSid());
        useGiftCodeDTO.setRoleId(player.getUid());
        useGiftCodeDTO.setRoleName(player.getName());
        useGiftCodeDTO.setAccount(player.getAccount());

        String sign = SignUtil.signByJsonKey(useGiftCodeDTO, connectLoginProperties.getJwtKey());
        HttpResponse execute = HttpRequestPost.ofJson(url, useGiftCodeDTO.toJSONString())
                .addHeader(HttpHeaderNames.AUTHORIZATION.toString(), sign)
                .execute();
        RunResult runResult = execute.bodyRunResult();

        if (runResult.isFail()) {
            tipsService.tips(player, runResult.msg());
            return;
        }

        Integer cid = runResult.getInteger("cid");
        if (cid == null || cid < 1) {
            tipsService.tips(player, "内部异常");
            return;
        }
        HashMap<Integer, CountMap> useGiftCodeCountMap = player.getUseGiftCodeCountMap();
        CountMap countMap = useGiftCodeCountMap.computeIfAbsent(cid, k -> new CountMap());

        String validateConfig = runResult.getString("validate");
        if (StringUtils.isNotBlank(validateConfig)) {
            /*配置检查，每天使用次数，每周使用次数，每月使用次数*/
            List<Validation> apply = CountValidationType.Parse.apply(validateConfig);
            boolean validate = validationService.validateAll(countMap, apply, (handler, validation) -> {
                String tips = handler.tips();
                tipsService.tips(player, tips);
                log.info("条件验证不通过：{}, 使用礼包码：{} {} -> {}", player, giftCode, validation, countMap);
            });
            if (!validate) {
                return;
            }
        }
        player.getUseGiftCodeTotalMap().addTo(cid, 1);
        for (CountData countData : countMap.getValidationMap().values()) {
            countData.update(1);
        }

        List<ItemCfg> rewards = GameCfgFunction.ItemCfgFunction.apply(runResult.getString("rewards"));
        ReasonDTO reasonDTO = ReasonDTO.of(
                ReasonConst.USE_GiftCode,
                "giftCode=", giftCode, "cid=%d(%s)".formatted(cid, runResult.getString("comment"))
        );

        List<Item> itemList = bagService.newItems(rewards);

        BagChangeDTO4Item rewardItemArgs = BagChangeDTO4Item.builder()
                .setItemList(itemList)
                .setBagFullSendMail(true)
                .setBagErrorNoticeClient(false)
                .setReasonDTO(reasonDTO)
                .build();

        bagService.gainItems(player, rewardItemArgs);

        ResUseGiftCode resUseGiftCode = new ResUseGiftCode();
        resUseGiftCode.setGiftCode(giftCode);
        player.write(resUseGiftCode);
    }

}
