package groovy

import wxdgaming.boot2.core.SpringUtil
import wxdgaming.game.server.bean.role.Player
import wxdgaming.game.server.module.drive.PlayerDriveService

def playerDriveService = SpringUtil.childApplicationContextProvider.getBean(PlayerDriveService.class)
def values = playerDriveService.playerDriveContentMap.values()
StringBuilder stringBuilder = new StringBuilder();
for (PlayerDriveService.PlayerDriveContent playerDriveContent : values) {
    def players = playerDriveContent.playerMap.values()
    for (Player player : players) {
        stringBuilder.append(player.toString()).append("\n");
    }
}
return stringBuilder.toString();

