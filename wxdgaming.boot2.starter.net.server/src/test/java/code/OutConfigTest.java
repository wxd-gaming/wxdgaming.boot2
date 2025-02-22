package code;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:05
 **/
public class OutConfigTest {

    @Test
    public void out() {
        BootConfig.getIns().getConfig().put("executor", ExecutorConfig.INSTANCE);
        BootConfig.getIns().getConfig().put("socket.server", new SocketServerConfig());
        String jsonFmt = FastJsonUtil.toJsonFmt(BootConfig.getIns().getConfig());
        System.out.println(jsonFmt);
        JSONObject jsonObject = JSONObject.parseObject(jsonFmt);
        ExecutorConfig executor = jsonObject.getObject("executor", ExecutorConfig.class);
        System.out.println(executor);
    }

}
