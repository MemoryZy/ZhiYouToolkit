package cn.zhiyou.enums;

/**
 * @author Memory
 * @since 2023/11/28
 */
public enum JavaDocumentEnum {

    FIELD_DOC("/**\n * {}\n */");


    private final String value;


    JavaDocumentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
