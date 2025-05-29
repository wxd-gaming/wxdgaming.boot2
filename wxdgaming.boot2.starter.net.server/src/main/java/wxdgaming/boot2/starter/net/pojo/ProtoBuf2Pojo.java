package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Singleton;
import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.Comment;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * pojo生成
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-27 16:39
 **/
public class ProtoBuf2Pojo {

    /** 处理proto文件 */
    public static void actionProtoFile(String outPath, String readPath) {
        Path path = Path.of(readPath);
        System.out.println(path);
        FileUtil
                .walkFiles(readPath, ".proto")
                .forEach(filePath -> {

                    AtomicBoolean clean_files = new AtomicBoolean(false);

                    AtomicReference<BeanInfo> comment = new AtomicReference<>(new BeanInfo(""));
                    AtomicBoolean start = new AtomicBoolean();
                    AtomicReference<String> packageName = new AtomicReference<>("");

                    TreeSet<String> imports = new TreeSet<>();
                    imports.add(Tag.class.getName());
                    imports.add(Comment.class.getName());
                    imports.add(Getter.class.getName());
                    imports.add(Setter.class.getName());
                    imports.add(PojoBase.class.getName());
                    imports.add(Accessors.class.getName());
                    imports.add(List.class.getName());
                    imports.add(ArrayList.class.getName());
                    imports.add(Map.class.getName());
                    imports.add(LinkedHashMap.class.getName());
                    imports.add(MapOf.class.getName());

                    FileReadUtil.readLine(filePath, StandardCharsets.UTF_8, line -> {
                        line = line.trim();
                        if (StringUtils.isBlank(line)) return;
                        if (line.contains("java_multiple_files")) {
                            // if (line.contains("true")) {
                            //     multiple_files.set(true);
                            //     comment.get().multiple_files = true;
                            // }
                        } else if (line.contains("java_package")) {
                            int i = line.indexOf("=");
                            int i1 = line.indexOf(";");
                            String trim = line.substring(i + 1, i1).trim().replace("\"", "");
                            packageName.set(trim);
                            comment.get().packageName = trim;
                        } else if (line.contains("import")) {
                            int i1 = line.indexOf(";");
                            String trim = line.substring(7, i1).trim().replace("\"", "");
                            trim = trim.replace(".proto", "");
                            imports.add(packageName.get() + "." + trim + ".*");
                        } else if (line.startsWith("//")) {
                            comment.get().comment = line.substring(2);
                        } else if (line.startsWith("message") || line.startsWith("enum")) {
                            String[] split = line.split(" ");
                            System.out.println("【" + split[1] + "】");

                            comment.get().classType = line.startsWith("message") ? "class" : "enum";
                            comment.get().className = split[1];

                            start.set(true);
                        } else if (line.contains("}")) {
                            String p1 = filePath.getFileName().toString().replace(".proto", "").replace("Message", "");
                            p1 = wxdgaming.boot2.core.chatset.StringUtils.lowerFirst(p1);
                            comment.get().packageName = " %s.%s".formatted(packageName.get(), p1);
                            String to = "package %s;".formatted(comment.get().packageName);
                            to += "\n";
                            for (String anImport : imports) {
                                to += "\nimport %s;".formatted(anImport);
                            }
                            to += "\n\n\n";
                            to += comment.get().string();
                            String javaFileName = "%s/%s/%s/%s.java".formatted(outPath, packageName.get().replace(".", "/"), p1, comment.get().getClassName());
                            File javaFile = new File(javaFileName);
                            if (clean_files.compareAndSet(false, true)) {
                                FileUtil.del(javaFile.getParent());
                            }
                            System.out.println(to);
                            FileWriteUtil.writeString(
                                    javaFile,
                                    to
                            );
                            comment.set(new BeanInfo(packageName.get()));
                            start.set(false);
                        } else {
                            if (start.get()) {
                                comment.get().addField(line);
                            }
                        }
                    });
                });

    }

    @Getter
    public static class BeanInfo {

        private String packageName;
        private String classType;
        private String className;
        private String comment;
        private final List<FiledInfo> filedInfos = new ArrayList<>();

        public BeanInfo(String packageName) {
            this.packageName = packageName;
        }

        public void addField(String line) {
            FiledInfo filedInfo = new FiledInfo(classType, line);
            addField(filedInfo);
        }

        public void addField(FiledInfo filedInfo) {
            if (StringUtils.isNotBlank(filedInfo.getField())) {
                if (filedInfos.stream().anyMatch(v -> v.getTag() == filedInfo.getTag())) {
                    // tag重复
                    throw new RuntimeException(className + " - tag " + filedInfo.getTag() + " 重复");
                }
                if (filedInfos.stream().anyMatch(v -> Objects.equals(v.getFieldName(), filedInfo.getFieldName()))) {
                    // tag重复
                    throw new RuntimeException(className + " - tag " + filedInfo.getTag() + " 重复");
                }
                getFiledInfos().add(filedInfo);
            }
        }

        public String string() {
            if ("class".equals(classType)) {
                return classString();
            }
            return enumString();
        }

        public String classString() {
            String f = "\n";
            for (FiledInfo filedInfo : filedInfos) {
                f += """
                            /** %s */
                            @Tag(%s) private %s;
                        """.formatted(filedInfo.comment, filedInfo.tag, filedInfo.field);
            }
            String formatted = "%s.%s".formatted(packageName, className).trim();
            int hashcode = StringUtils.hashcode(formatted);
            System.out.println(formatted + " = " + hashcode);
            return """                        
                    /** %s */
                    @Getter
                    @Setter
                    @Accessors(chain = true)
                    @Comment("%s")
                    public class %s extends PojoBase {                        
                    
                        /** 消息ID */
                        public static int _msgId() {
                            return %s;
                        }
                    
                        /** 消息ID */
                        public int msgId() {
                            return _msgId();
                        }
                    
                    %s
                    
                    }
                    """.formatted(comment, comment, className, hashcode, f);

        }

        public String enumString() {
            String f = "\n";
            for (FiledInfo filedInfo : filedInfos) {
                f += """
                            /** %s */
                            @Tag(%s)
                            %s(%s, "%s"),
                        """.formatted(filedInfo.comment, filedInfo.tag, filedInfo.field.trim(), filedInfo.tag, filedInfo.comment);
            }
            return """
                    /** %s */
                    @Getter
                    @Comment("%s")
                    public enum %s {
                    %s
                        ;
                    
                        private static final Map<Integer, %s> static_map = MapOf.ofMap(%s::getCode, %s.values());
                    
                        public static %s valueOf(int code) {
                            return static_map.get(code);
                        }
                    
                        /** code */
                        private final int code;
                        /** 备注 */
                        private final String command;
                    
                        %s(int code, String command) {
                            this.code = code;
                            this.command = command;
                        }
                    }
                    """.formatted(comment, comment, className, f, className, className, className, className, className);
        }

    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class FiledInfo extends ObjectBase {

        private int tag;
        private String fieldName;
        private String field;
        private String comment = "";

        public FiledInfo(String classType, String line) {
            if (StringUtils.isBlank(line)) {
                return;
            }
            line = line.trim();
            String[] split = line.split("[;；]");
            if (split.length > 1) {
                comment = split[1].replace("//", "");
            }
            ArrayList<String> tmp = new ArrayList<>();
            if ("enum".equals(classType)) {
                tmp.add("enum");
            }

            List<String> split1 = List.of(split[0].split(" "));

            for (String string : split1) {
                if (StringUtils.isBlank(string)) continue;
                tmp.add(string.trim());
            }

            String string = tmp.getFirst();
            boolean repeated = false;
            if (string.equals("repeated")) {
                repeated = true;
                tmp.removeFirst();
                string = tmp.getFirst();
            }
            switch (string) {
                case "bool":
                    if (repeated) {
                        field = "List<" + Boolean.class.getSimpleName() + ">";
                    } else {
                        field = boolean.class.getSimpleName();
                    }
                    break;
                case "int32":
                    if (repeated) {
                        field = "List<" + Integer.class.getSimpleName() + ">";
                    } else {
                        field = int.class.getSimpleName();
                    }
                    break;
                case "int64":
                    if (repeated) {
                        field = "List<" + Long.class.getSimpleName() + ">";
                    } else {
                        field = long.class.getSimpleName();
                    }
                    break;
                case "string":
                    if (repeated) {
                        field = "List<" + String.class.getSimpleName() + ">";
                    } else {
                        field = String.class.getSimpleName();
                    }
                    break;
                case "enum": {
                    field = "";
                }
                break;
                case "bytes": {
                    field = "byte[]";
                }
                break;
                default: {
                    if (split[0].contains("map<") && split[0].contains(">")) {
                        int start = split[0].indexOf("<");
                        int end = split[0].indexOf(">");
                        field = "Map<" + split[0].substring(start + 1, end) + ">";
                        field = field
                                .replace("bool", Boolean.class.getSimpleName())
                                .replace("int32", Integer.class.getSimpleName())
                                .replace("int64", Long.class.getSimpleName())
                                .replace("bytes", byte[].class.getSimpleName())
                                .replace("string", String.class.getSimpleName())
                        ;
                        tmp.set(1, tmp.get(2));
                        tmp.set(3, tmp.get(4));
                    } else {
                        if (repeated) {
                            field = "List<" + string + ">";
                        } else {
                            field = string;
                        }
                    }
                }
            }
            fieldName = tmp.get(1);
            field += " " + tmp.get(1);
            if (repeated) {
                field += " = new ArrayList<>()";
            } else if (field.contains("Map")) {
                field += " = new LinkedHashMap<>()";
            }
            try {
                tag = Integer.parseInt(tmp.get(3));
            } catch (NumberFormatException e) {
                throw Throw.of(line, e);
            }
        }

    }

    /**
     * 创建mapping
     *
     * @param outPath         输出路径
     * @param packageName     输出包名
     * @param readPackageName 读取包名
     * @param spi             要处理的接口 例如Req or Res
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-01-15 14:58
     */
    public static void createMapping(String outPath, String packageName, String spi, String readPackageName, Predicate<Class<?>> filter, Supplier<String> methodParamsSupplier, Supplier<String> methodContentSupplier) {
        ReflectContext reflectContext = ReflectContext.Builder.of(readPackageName).build();
        reflectContext
                .withSuper(PojoBase.class)
                .forEach(pojoBase -> {
                    Class<?> cls = pojoBase.getCls();
                    if (filter != null && !filter.test(cls)) {
                        return;
                    }
                    createMapping0(outPath, packageName, spi, cls, methodParamsSupplier, methodContentSupplier);
                });
    }

    public static void createMapping0(String outPath, String packageName, String spi, Class<?> cls, Supplier<String> methodParamsSupplier, Supplier<String> methodContentSupplier) {
        String simpleName = cls.getSimpleName();
        if (!simpleName.startsWith(spi)) {
            return;
        }
        String[] split = cls.getPackageName().split("[.]");
        String p1 = split[split.length - 1];
        packageName = packageName + "." + p1 + "." + "handler";
        String className = simpleName + "Handler";
        String fileName = outPath + "/" + packageName.replace(".", "/") + "/" + className + ".java";
        Path filePath = Paths.get(fileName);
        boolean exists = Files.exists(filePath);
        System.out.println(String.format("\n是否存在=%s\n文件=%s\n包名=%s\n类名=%s\n", exists, fileName, packageName, className));
        if (exists) {
            return;
        }

        Comment ann = AnnUtil.ann(cls, Comment.class);

        TreeSet<String> imports = new TreeSet<>();
        imports.add(ProtoRequest.class.getName());
        imports.add(Slf4j.class.getName());
        imports.add(Singleton.class.getName());
        imports.add(SocketSession.class.getName());
        imports.add(cls.getName());

        String importString = imports.stream().map(s -> "import " + s + ";").collect(Collectors.joining("\n"));

        String comment = ann.value();
        String methodContent = "";
        if (methodContentSupplier != null) {
            methodContent = methodContentSupplier.get();
        }

        String methodParams = "";
        if (methodParamsSupplier != null) {
            methodParams = methodParamsSupplier.get();
        }
        if (StringUtils.isNotBlank(methodParams)) {
            methodParams = ", " + methodParams;
        }
        String spiCode = """
                package %s;
                
                %s
                
                /**
                 * %s
                 *
                 * @author: wxd-gaming(無心道, 15388152619)
                 * @version: v1.1
                 **/
                @Slf4j
                @Singleton
                public class %s {
                
                    /** %s */
                    @ProtoRequest
                    public void %s(SocketSession socketSession, %s req%s) {
                        %s
                    }
                
                }""".formatted(packageName, importString, comment, className, comment, StringUtils.lowerFirst(cls.getSimpleName()), cls.getSimpleName(), methodParams, methodContent);

        System.out.println(spiCode);
        FileWriteUtil.writeString(fileName, spiCode);

    }

}
