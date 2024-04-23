package cn.zhiyou.enums;

import cn.hutool.http.Method;

/**
 * Spring请求的一些注解
 *
 * @author wcp
 * @since 2023/12/1
 */
public enum SpringRequestAnnotationEnum {

    /**
     * RequestMapping
     */
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", null),

    /**
     * GetMapping
     */
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", Method.GET),

    /**
     * PostMapping
     */
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", Method.POST),

    /**
     * PutMapping
     */
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", Method.PUT),

    /**
     * DeleteMapping
     */
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", Method.DELETE),

    /**
     * PatchMapping
     */
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", Method.PATCH);


    private final String value;
    private final Method method;

    SpringRequestAnnotationEnum(String value, Method method) {
        this.value = value;
        this.method = method;
    }

    public String getValue() {
        return value;
    }

    public Method getMethod() {
        return method;
    }
}
