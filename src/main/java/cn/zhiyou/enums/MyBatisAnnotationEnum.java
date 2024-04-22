package cn.zhiyou.enums;

/**
 * @author wcp
 * @since 2023/12/14
 */
public enum MyBatisAnnotationEnum {
    MP_TABLE_ID("com.baomidou.mybatisplus.annotation.TableId"),
    MP_TABLE_FIELD("com.baomidou.mybatisplus.annotation.TableField"),
    JPA_COLUMN("javax.persistence.Column"),
    JPA_TRANSIENT("javax.persistence.Transient"),
    MYBATIS_IGNORE("MybatisIgnore.ZhiYou"),
    MP_TABLE_NAME("com.baomidou.mybatisplus.annotation.TableName")
    ;





    private final String value;

    MyBatisAnnotationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
