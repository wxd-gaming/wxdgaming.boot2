package wxdgaming.game.center.modules.recharge.api.sdk.quick;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.game.center.modules.recharge.RechargeService;
import wxdgaming.game.center.util.XmlUtil;
import wxdgaming.game.common.bean.login.AppPlatformParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * quick 充值回调接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-19 10:06
 **/
@Slf4j
@RestController
@RequestMapping("/order/call")
public class QuickCallRechargeController {

    private final RechargeService rechargeService;

    public QuickCallRechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @RequestMapping("/quick/{appid}")
    public String quick(HttpServletRequest req, @PathVariable("appid") Integer appid) {
        AppPlatformParams appPlatformParams = AppPlatformParams.getAppPlatformParams(appid);

        Map<String, String[]> trailerFields = req.getParameterMap();
        try {
            log.info("解析渠道：{}, {}", appPlatformParams, JSON.toJSONString(trailerFields));
            String nt_data = req.getParameter("nt_data");
            String sign = req.getParameter("sign");
            String md5Sign = req.getParameter("md5Sign");

            if (StringUtils.isBlank(nt_data) || StringUtils.isBlank(sign) || StringUtils.isBlank(md5Sign)) {
                return "fail";
            }

            String MD5_KEY = appPlatformParams.getPayKey();
            String CALLBACK_KEY = appPlatformParams.getOtherKey();
            String md5Self = nt_data + sign + MD5_KEY;
            String md5Encode = Md5Util.md5(md5Self);
            if (!Objects.equals(md5Encode, md5Sign)) {
                log.info("解析渠道：{}, 订单查询MD5签名失败：{}, md5Sign={}, md5Self={}, md5Encode={}", appPlatformParams, nt_data, md5Sign, md5Self, md5Encode);
                return "fail";
            }
            String decode = IOSDesUtil.decode(nt_data, CALLBACK_KEY);
            log.warn("解析渠道：{}, {}", appPlatformParams, decode);
            quicksdk_message readerXMLBySXml = XmlUtil.fromXml(decode, quicksdk_message.class);
            message me = readerXMLBySXml.getMe();

            String amount = me.getAmount();
            String orderNo = me.getOrder_no();
            String outOrderNo = me.getOut_order_no();
            /* TODO 透传回来的 orderId*/
            String extrasParams = me.getExtras_params();

            RunResult runResult = rechargeService.callRecharge(orderNo, outOrderNo, amount);
            return runResult.isOk() ? "SUCCESS" : "FAIL";

        } catch (Exception e) {
            log.error("解析渠道：{}, 透传参数回调异常 {}", appPlatformParams, trailerFields, e);
        }
        return "fail";
    }

    @Setter
    @Getter
    @Root(name = "quicksdk_message")
    public static final class quicksdk_message {

        @Element(name = "message")
        private message me;

    }

    @Setter
    @Getter
    public static final class message {

        /** 购买道具的用户uid */
        private String uid = "";
        /** 购买道具的用户username */
        private String login_name = "";
        /** 游戏下单时传递的游戏订单号，原样返回 */
        private String out_order_no = "";
        /** SDK唯一订单号 */
        private String order_no = "";
        /** 支付时间 2015-01-01 23:00:00 */
        private String pay_time = "";
        /** 成交金额，单位元，游戏最终发放道具金额应以此为准 */
        private String amount = "";
        /** 内购订阅型商品订单使用，如果有此字段表示订单订阅状态。cp监测到有此字段时不需要发货。字段取值为：2：订阅取消 */
        private String subscriptionStatus = "";
        /** 内购订阅型商品订单取消订阅原因。当有subscriptionStatus字段时此字段必有 */
        private String subReason;
        /** 客户端下单时透传参数 原样返回 */
        private String extras_params = "";

    }

    final static class IOSDesUtil {

        private final static Pattern pattern = Pattern.compile("\\d+");

        private final static String charset = "utf-8";

        public static String decode(String src, String key) {
            if (src == null || src.isEmpty()) {
                return src;
            }
            Matcher m = pattern.matcher(src);
            List<Integer> list = new ArrayList<Integer>();
            while (m.find()) {
                try {
                    String group = m.group();
                    list.add(Integer.valueOf(group));
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    return src;
                }
            }

            if (!list.isEmpty()) {
                try {
                    byte[] data = new byte[list.size()];
                    byte[] keys = key.getBytes();

                    for (int i = 0; i < data.length; i++) {
                        data[i] = (byte) (list.get(i) - (0xff & keys[i % keys.length]));
                    }
                    return new String(data, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(System.err);
                }
                return src;
            } else {
                return src;
            }
        }
    }

}
