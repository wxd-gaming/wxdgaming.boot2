package wxdgaming.game.bean.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-12 15:10
 */
@Getter
@Setter
@Accessors(chain = true)
public class MailPack {

    private List<MailInfo> mailInfoList = new ArrayList<>();

}
