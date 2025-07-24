package wxdgaming.boot2.starter.net.httpclient5;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.util.AssertUtil;

/**
 * ip数据查询
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-28 20:26
 **/
@Getter
@Setter
public class IPInfo extends ObjectBase {

    static final String format = "http://ip-api.com/json/%s?lang=zh-CN";

    public static IPInfo get(String ip) {
        HttpRequestGet httpRequestGet = HttpRequestGet.of(String.format(format, ip));
        IPInfo ipInfo = httpRequestGet.execute().bodyObject(IPInfo.class);
        AssertUtil.assertTrue(!"success".equals(ipInfo.getStatus()), "ip地址解析失败");
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

