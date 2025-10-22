package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.ArrayList;

/**
 * 全服邮件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-20 14:31
 **/
@Getter
@Setter
public class ServerMailDTO {

    int uid;
    /** 有效期 */
    private long[] validity = new long[2];
    /** 如果是空表示所有区服，如果不为空表示特定区服 */
    private ArrayList<Integer> serverIdList = new ArrayList<>();
    private ArrayList<Long> roleIdList = new ArrayList<>();
    private ArrayList<String> accountList = new ArrayList<>();
    private int[] lvCondition = new int[2];
    private int[] vipLvCondition = new int[2];
    private String title;
    private String content;
    private String itemListString;

    public boolean checkServerId(int serverId) {
        return serverIdList.isEmpty() || serverIdList.contains(serverId);
    }

    public boolean validTime() {
        long millis = MyClock.millis();
        if ((validity[0] < 1 || validity[0] <= millis) && (validity[1] < 1 || millis <= validity[1])) {
            return true;
        }
        return false;
    }

}
