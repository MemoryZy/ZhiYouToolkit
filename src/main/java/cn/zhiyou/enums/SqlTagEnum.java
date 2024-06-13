package cn.zhiyou.enums;

import java.util.Objects;

/**
 * @author Memory
 * @since 2023/12/1
 */
public enum SqlTagEnum {

    SELECT("select"),
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private final String value;

    SqlTagEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SqlTagEnum match(String value) {
        for (SqlTagEnum sqlTag : SqlTagEnum.values()) {
            if (Objects.equals(sqlTag.getValue(), value)) {
                return sqlTag;
            }
        }
        return null;
    }

}
