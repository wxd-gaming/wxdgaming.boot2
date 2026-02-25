package wxdgaming.game.server.bean;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.game.server.bean.goods.ItemCfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 游戏配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 15:04
 **/
public class GameCfgFunction {

    public static final Function<ConfigString, List<ItemCfg>> ConfigString2ItemCfgList = new Function<ConfigString, List<ItemCfg>>() {
        @Override public List<ItemCfg> apply(ConfigString configString) {
            if (configString == null) return Collections.emptyList();
            return configString.get(ItemCfgFunction);
        }
    };

    public static final Function<String, List<ItemCfg>> ItemCfgFunction = new Function<String, List<ItemCfg>>() {
        @Override public List<ItemCfg> apply(String value) {
            if (StringUtils.isBlank(value)) {
                return Collections.emptyList();
            } else {
                List<ItemCfg> itemCfgs = new ArrayList<>();
                String[] split = value.split("#");
                for (String one : split) {
                    String[] split1 = one.split("\\|");
                    ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
                    builder.cfgId(Integer.parseInt(split1[0]));
                    builder.num(Long.parseLong(split1[1]));
                    if (split1.length > 2)
                        builder.bind("1".endsWith(split1[2]));
                    if (split1.length > 3)
                        builder.expirationTime(Long.parseLong(split1[3]));
                    if (split1.length > 4)
                        builder.job(Integer.valueOf(split1[4]));
                    if (split1.length > 5)
                        builder.lv(Integer.valueOf(split1[5]));
                    if (split1.length > 6)
                        builder.sex(Integer.valueOf(split1[6]));
                    if (split1.length > 7)
                        builder.weight(Integer.valueOf(split1[7]));
                    itemCfgs.add(builder.build());
                }
                return itemCfgs;
            }
        }
    };

}
