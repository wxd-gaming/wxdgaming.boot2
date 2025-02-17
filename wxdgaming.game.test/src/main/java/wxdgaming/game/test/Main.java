package wxdgaming.game.test;

import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.starter.WxdApplication;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        RunApplication run = WxdApplication.run(Main.class);
        ExecutorUtil.getDefaultExecutor().schedule(
                () -> {
                    run.executeMethodWithAnnotated(Init.class);
                },
                10,
                TimeUnit.SECONDS
        );
    }

}