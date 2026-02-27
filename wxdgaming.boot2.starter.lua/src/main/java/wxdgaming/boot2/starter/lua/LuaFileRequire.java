package wxdgaming.boot2.starter.lua;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * lua require 方式加载文件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-21 19:21
 **/
@Getter
public class LuaFileRequire {

    final String luaPath;
    final List<String> modules;

    @SneakyThrows public LuaFileRequire(LuaFileCache luaFileCache) {

        Path path = Paths.get("target", "temp", "lua");

        FileUtils.deleteDirectory(path.toFile());
        FileUtils.forceMkdir(path.toFile());

        LinkedHashSet<Path> moduleSet = new LinkedHashSet<>();

        for (ImmutablePair<Path, byte[]> pathImmutablePair : luaFileCache.getExtendList()) {
            Path resolve = path.resolve(pathImmutablePair.getLeft().getFileName().toString());
            if (!moduleSet.add(resolve)) {
                throw new RuntimeException("重复模块 " + pathImmutablePair.getLeft());
            }
            Files.write(resolve, pathImmutablePair.getRight());
        }

        for (ImmutablePair<Path, byte[]> pathImmutablePair : luaFileCache.getPathList()) {
            Path resolve = path.resolve(pathImmutablePair.getLeft().getFileName().toString());
            if (!moduleSet.add(resolve)) {
                throw new RuntimeException("重复模块 " + pathImmutablePair.getLeft());
            }
            Files.write(resolve, pathImmutablePair.getRight());
        }

        List<String> tmpModules = new ArrayList<>();
        ArrayList<String> paths = new ArrayList<>();
        for (Path loadPath : moduleSet) {
            String fileName = loadPath.getFileName().toString();
            if (fileName.endsWith(".lua")) {
                String string = loadPath.getParent().toFile().getCanonicalPath() + File.separator + "?.lua";
                if (!paths.contains(string)) {
                    paths.add(string);
                }
                tmpModules.add(fileName.replace(".lua", ""));
            } else if (fileName.endsWith(".dll")) {
                String string = loadPath.getParent().toFile().getCanonicalPath() + File.separator + "?.dll";
                if (!paths.contains(string)) {
                    paths.add(string);
                }
            } else if (fileName.endsWith(".exe")) {
                String string = loadPath.getParent().toFile().getCanonicalPath() + File.separator + "?.exe";
                if (!paths.contains(string)) {
                    paths.add(string);
                }
            }
        }
//        List<String> list = paths.stream().map(i -> URLEncoder.encode(i, StandardCharsets.UTF_8)).toList();
        String tmp = String.join(";", paths);
        modules = Collections.unmodifiableList(tmpModules);
        luaPath = new String(tmp.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }


}
