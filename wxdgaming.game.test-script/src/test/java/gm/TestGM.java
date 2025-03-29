package gm;

import wxdgaming.boot2.core.RunApplication;
import wxdgaming.game.test.script.gm.IGmDynamic;

public class TestGM implements IGmDynamic {


    @Override public Object execute(RunApplication runApplication) {
        return "远程3";
    }

}
