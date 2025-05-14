package wxdgaming.game.test.bean.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectLong;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.test.bean.goods.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 邮件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-12 15:10
 **/
@Getter
@Setter
@Accessors(chain = true)
public class MailInfo extends ObjectLong {

    private String title;
    private String content;
    private List<String> contentParams = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private String sender;
    private long sendTime;
    private boolean read;
    private boolean reward;
    /** 邮件日志，特别是领取邮件附件记录 */
    private String sourceLog;

    /** 检查邮件是否有效 */
    public boolean checkValidity() {
        if (MyClock.millis() - sendTime > TimeUnit.DAYS.toMillis(30)) {
            /*已经超过30天，删除*/
            return false;
        }
        return true;
    }

}
