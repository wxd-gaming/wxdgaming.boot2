package wxdgaming.boot2;

import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.RunApplicationMain;
import wxdgaming.boot2.starter.WxdApplication;
import wxdgaming.boot2.starter.batis.mapdb.MapDBScan;
import wxdgaming.boot2.starter.net.SocketScan;
import wxdgaming.boot2.starter.scheduled.ScheduledScan;

public class ChatRoomApplication {

    public static void main(String[] args) {
        RunApplicationMain runApplication = WxdApplication.run(
                CoreScan.class,
                ScheduledScan.class,
                SocketScan.class,
                MapDBScan.class,
                ChatRoomApplication.class
        );
        runApplication.start();
    }

}