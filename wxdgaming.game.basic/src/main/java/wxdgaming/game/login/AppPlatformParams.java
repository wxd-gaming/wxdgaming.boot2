package wxdgaming.game.login;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 渠道参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-26 09:43
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@Accessors(chain = true)
public class AppPlatformParams extends ObjectBase {

    private static final ConcurrentHashMap<Integer, AppPlatformParams> appPlatformParamsMap = new ConcurrentHashMap<>();

    public static AppPlatformParams getAppPlatformParams(int appId) {
        return appPlatformParamsMap.get(appId);
    }

    public enum Platform {
        LOCAL,
        QUICK
    }

    public static final AppPlatformParams LOCAL = new AppPlatformParams(1).setPlatform(Platform.LOCAL).setLoginKey("local");
    public static final AppPlatformParams QUICK = new AppPlatformParams(2).setPlatform(Platform.QUICK).setLoginKey("local");

    private int appId;
    private Platform platform;
    private String appKey;
    private String appPlatform;
    private String loginKey;
    private String payKey;
    private String url;

    public AppPlatformParams(int appId) {
        this.appId = appId;
        AppPlatformParams put = appPlatformParamsMap.put(appId, this);
        AssertUtil.assertTrue(put == null, "重复注册appId：" + appId);
    }

}
