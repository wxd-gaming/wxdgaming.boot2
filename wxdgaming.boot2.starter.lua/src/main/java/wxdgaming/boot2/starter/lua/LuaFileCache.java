package wxdgaming.boot2.starter.lua;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * lua文件缓存模式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-11-07 14:43
 **/
@Getter
public class LuaFileCache {
    static String[] extensions = {"lua", "LUA", "dll", "exe"};

    static Predicate<Path> filter = test -> {
        boolean found = true;
        if (extensions.length > 0) {
            found = false;
            for (int i = 0; i < extensions.length; i++) {
                String extension = extensions[i];
                found |= test.getFileName().toString().endsWith(extension);
            }
        }
        return found;
    };


    /** 系统扩展 */
    final List<ImmutablePair<Path, byte[]>> extendList;
    final List<ImmutablePair<Path, byte[]>> pathList;


    public LuaFileCache(String dir) {

        extendList = Utils.resourcesList("lua-extend", extensions);
        Path dirPath = Paths.get(dir);
        Path fileBase = dirPath.resolve("base");
        Path fileCommon = dirPath.resolve("common");
        Path fileUtils = dirPath.resolve("utils");

        try {
            pathList = Files.walk(dirPath, 99)
                    .filter(Files::isRegularFile)
                    .filter(filter)
                    .sorted((o1, o2) -> {
                        /* TODO 加载顺序，base common utils 其它文件夹 文件 */
                        if (o1.startsWith(fileBase) && !o2.startsWith(fileBase)) {
                            return -1;
                        }
                        if (!o1.startsWith(fileBase) && o2.startsWith(fileBase)) {
                            return 1;
                        }

                        if (o1.startsWith(fileCommon) && !o2.startsWith(fileCommon)) {
                            return -1;
                        }
                        if (!o1.startsWith(fileCommon) && o2.startsWith(fileCommon)) {
                            return 1;
                        }

                        if (o1.startsWith(fileUtils) && !o2.startsWith(fileUtils)) {
                            return -1;
                        }
                        if (!o1.startsWith(fileUtils) && o2.startsWith(fileUtils)) {
                            return 1;
                        }

                        if (o1.getParent().equals(o2.getParent())) {
                            return o1.getFileName().toString().toLowerCase().compareTo(o2.getFileName().toString().toLowerCase());
                        }

                        return o1.getParent().compareTo(o2.getParent());
                    })
                    .map(path -> {
                        try {
                            byte[] bytes = Files.readAllBytes(path);
                            return ImmutablePair.of(path, bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
