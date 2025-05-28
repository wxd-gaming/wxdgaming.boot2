package gm;

import wxdgaming.boot2.core.RunApplication;
import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;

public class TestGM implements IGmDynamic {


    @Override public Object execute(RunApplication runApplication) {
        return "远程3";
    }

}
