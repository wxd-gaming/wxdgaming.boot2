package code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.MainApplicationContextProvider;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.game.server.script.validation.Validation;
import wxdgaming.game.server.script.validation.ValidationService;
import wxdgaming.game.server.script.validation.impl.LevelValidationHandler;

import java.util.List;

@SpringBootTest(classes = {
        CoreScan.class,
        MainApplicationContextProvider.class,
        TipsService.class,
        ProtoListenerFactory.class,
        LevelValidationHandler.class,
        ValidationService.class
})
public class ValidationTest {

    @Autowired MainApplicationContextProvider mainApplicationContextProvider;
    @Autowired ValidationService validationService;

    @BeforeEach
    public void before() {
        mainApplicationContextProvider.executeMethodWithAnnotatedInit();
    }

    @Test
    public void v1() {
        String t = "Level|gte|1;Level|lte|999";
        System.out.println(t);
        List<Validation> apply = Validation.Parse.apply(t);
        System.out.println(apply);
        ConfigString configString = new ConfigString("Level|gte|1;Level|lte|999");
        Player player = new Player();
        player.setLevel(100);
        System.out.println(validationService.validate(player, configString, Validation.Parse, false));
    }

    @Test
    public void v2() {
        String t = "Level|>=|1;Level|<=|999";
        System.out.println(t);
        List<Validation> apply = Validation.Parse2.apply(t);
        System.out.println(apply);
        ConfigString configString = new ConfigString("Level|>=|1;Level|<=|999");
        Player player = new Player();
        player.setLevel(100);
        System.out.println(validationService.validate(player, configString, Validation.Parse2, false));
    }

}
