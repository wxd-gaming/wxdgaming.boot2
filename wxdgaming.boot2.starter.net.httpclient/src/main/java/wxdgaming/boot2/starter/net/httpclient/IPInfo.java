package wxdgaming.boot2.starter.net.httpclient;

import lombok.Getter;
import lombok.Setter;
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

