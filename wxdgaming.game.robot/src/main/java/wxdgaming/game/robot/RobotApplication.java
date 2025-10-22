package wxdgaming.game.robot;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.starter.excel.DataExcelScan;
import wxdgaming.boot2.starter.net.SocketConfiguration;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;

/**
 * 启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 11:27
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                ConnectLoginProperties.class,
                SocketConfiguration.class,
                DataExcelScan.class,
                ScheduledScan.class,
                RobotApplication.class
        }
)
public class RobotApplication {

    public static void main(String[] args) {
        MainApplicationContextProvider.builder(RobotApplication.class).web(WebApplicationType.NONE).run(args);

        SpringUtil.mainApplicationContextProvider
                .postInitEvent()
                .executeMethodWithAnnotatedStart();

    }

}
