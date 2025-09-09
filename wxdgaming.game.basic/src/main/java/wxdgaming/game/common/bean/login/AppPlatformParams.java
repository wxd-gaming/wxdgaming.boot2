package wxdgaming.game.common.bean.login;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 渠道参数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-26 09:43
 **/
@Getter
public class AppPlatformParams extends ObjectBase {

    private static final ConcurrentHashMap<Integer, AppPlatformParams> appPlatformParamsMap = new ConcurrentHashMap<>();

    public static void register(AppPlatformParams appPlatformParams) {
        AppPlatformParams put = appPlatformParamsMap.put(appPlatformParams.getAppId(), appPlatformParams);
        AssertUtil.assertTrue(put == null, "重复注册appId：" + appPlatformParams.getAppId());
    }

    public static AppPlatformParams getAppPlatformParams(int appId) {
        return appPlatformParamsMap.get(appId);
    }

    public static enum Platform {
        LOCAL,
        QUICK
    }

    public static final AppPlatformParams LOCAL;

    static {
        LOCAL = AppPlatformParams.builder().appId(1).platform(Platform.LOCAL).loginKey("local").build();
        register(LOCAL);
    }

    private final int appId;
    private final Platform platform;
    private final String appKey;
    private final String appParams;
    private final String loginKey;
    private final String payKey;
    private final String otherKey;
    private final String url;

    @Builder
    public AppPlatformParams(int appId, Platform platform, String appKey, String appParams, String loginKey, String payKey, String otherKey, String url) {
        this.appId = appId;
        this.platform = platform;
        this.appKey = appKey;
        this.appParams = appParams;
        this.loginKey = loginKey;
        this.payKey = payKey;
        this.otherKey = otherKey;
        this.url = url;
    }

    public static void main(String[] args) {
        String jsonString = AppPlatformParams.LOCAL.toJSONString();
        System.out.println(jsonString);
        AppPlatformParams parse = FastJsonUtil.parse(jsonString, AppPlatformParams.class);
        System.out.println(parse);
    }

}
