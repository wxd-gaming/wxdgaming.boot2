package wxdgaming.boot2.starter.net.httpclient5;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * ip数据查询
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-28 20:26
 **/
@Getter
@Setter
public class IPInfo extends ObjectBase {

    public static IPInfo of(String ip) {
        final String format = "http://ip-api.com/json/%s?lang=zh-CN";
        GetRequest getRequest = GetRequest.of(String.format(format, ip));
        IPInfo ipInfo = getRequest.execute().bodyObject(IPInfo.class);
        if (!"success".equals(ipInfo.getStatus()))
            throw new AssertException("ip地址解析失败");
        return ipInfo;
    }

    /** success 表示成功 */
    private String status;
    /** 国家 */
    private String country;
    /** 省 */
    private String regionName;
    /** 城市 */
    private String city;
    /** ip */
    private String query;

}

