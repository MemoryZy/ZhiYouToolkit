package cn.zhiyou.enums;

/**
 * @author Memory
 * @since 2023/12/28
 */
public enum SwaggerAnnotationEnum {

    API_MODEL("io.swagger.annotations.ApiModel"),

    API_MODEL_PROPERTY("io.swagger.annotations.ApiModelProperty");

    private final String value;

    SwaggerAnnotationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
