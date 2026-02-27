package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import wxdgaming.boot2.starter.lua.LuaService;
import wxdgaming.boot2.starter.lua.bean.LuaData;
import wxdgaming.boot2.starter.lua.bean.LuaMap;
import wxdgaming.boot2.starter.lua.bean.LuaMonster;
import wxdgaming.boot2.starter.lua.bean.LuaPlayer;
import wxdgaming.boot2.starter.lua.impl.Lua55Impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@ComponentScan(basePackages = {"code", "wxdgaming"})
@SpringBootTest(classes = LuaService55Test.class)
public class LuaService55Test {

    static ScheduledExecutorService scheduledExecutorService;

    static {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Autowired
    LuaService luaService;

    @BeforeEach
    public void init() {
        luaService.init("src/test/lua", Lua55Impl::new);
    }

    @Test
    public void t2() {

        LuaPlayer luaPlayer1 = new LuaPlayer(System.nanoTime(), "player1");
        LuaPlayer luaPlayer2 = new LuaPlayer(System.nanoTime(), "player2");

        scheduledExecutorService.execute(() -> {

            luaService.postEvent("onPlayerCreate", luaPlayer1);
            luaService.postEvent("onPlayerCreate", luaPlayer2);

            LuaMap luaMap1001 = createLuaMap(1001);
            LuaMap luaMap1002 = createLuaMap(1002);

            luaService.postEvent("onPlayerEnter", luaMap1001, luaPlayer1);
            luaService.postEvent("onPlayerEnter", luaMap1002, luaPlayer2);

            scheduledExecutorService.scheduleAtFixedRate(() -> {
                for (LuaMap value : map.values()) {
                    onHeart(value);
                }
            }, 5, 1, TimeUnit.SECONDS);

        });
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(1));
    }

    Map<Long, LuaMap> map = new ConcurrentHashMap<>();

    public LuaMap createLuaMap(int cfgId) {
        LuaMap luaMap = new LuaMap(System.nanoTime(), cfgId);
        luaService.postEvent("onMapCreate", luaMap);
        map.put(luaMap.getUid(), luaMap);
        createMonster(luaMap, 1);
        createMonster(luaMap, 2);
        return luaMap;
    }

    public void createMonster(LuaMap luaMap, int cfgId) {
        LuaMonster luaMonster = new LuaMonster(System.nanoTime(), cfgId, "monster");
        luaService.postEvent("onMonsterCreate", luaMap, luaMonster);
        luaMap.getObjects().put(luaMonster.getUid(), luaMonster);
        luaService.postEvent("onMonsterEnter", luaMap, luaMonster);
    }

    public void onHeart(LuaMap luaMap) {
        try {
            if (luaMap.isOpenHeart()) {
                luaService.postEvent("onMapHeart", luaMap);
                for (LuaData value : luaMap.getObjects().values()) {
                    try {
                        if (value instanceof LuaPlayer luaPlayer) {
                            luaService.postEvent("onPlayerHeart", luaMap, luaPlayer);
                        } else if (value instanceof LuaMonster luaMonster) {
                            luaService.postEvent("onMonsterHeart", luaMap, luaMonster);
                        }
                    } catch (Exception e) {
                        log.error("{} onHeart {}", luaMap, value, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("{} onHeart", luaMap, e);
        }
    }

}
