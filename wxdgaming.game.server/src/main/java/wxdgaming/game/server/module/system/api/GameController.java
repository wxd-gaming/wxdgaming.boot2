package wxdgaming.game.server.module.system.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.game.server.module.system.GameService;

/**
 * 系统接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-02 11:00
 **/
@Slf4j
@RestController
@RequestMapping("/game")
public class GameController implements InitPrint {

    final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping("/reloadScript")
    public String reloadScript() {
        return gameService.reloadScript();
    }


    @RequestMapping("/reloadCfg")
    public String reloadCfg() {
        return "ok";
    }

}
