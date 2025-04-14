package wxdgaming.boot2.starter.excel.store;

import lombok.Getter;

@Getter
public class TypeString {

    public static final TypeString JAVA = new TypeString("java");
    public static final TypeString TypeScript = new TypeString("ts");

    private final String value;

    public TypeString(String value) {
        this.value = value;
    }
}
