package wxdgaming.game.server.module.inner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.util.Md5Util;
import wxdgaming.game.basic.login.LoginProperties;

/**
 * 内部服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 09:46
 **/
@Slf4j
@Service
public class InnerService implements InitPrint {

    final LoginProperties loginProperties;

    public InnerService(LoginProperties loginProperties) {

        this.loginProperties = loginProperties;
    }

    public void sign(JSONObject jsonData) {
        String json = jsonData.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginProperties.getJwtKey());
        jsonData.put("sign", md5DigestEncode);
    }

}
