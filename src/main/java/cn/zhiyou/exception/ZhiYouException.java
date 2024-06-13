package cn.zhiyou.exception;

import cn.zhiyou.enums.CreateMapperTextFieldEnum;

/**
 * @author Memory
 * @since 2023/12/28
 */
public class ZhiYouException extends RuntimeException {

    private final boolean isTip;
    private CreateMapperTextFieldEnum createMapperTextFieldEnum;

    public ZhiYouException(String message) {
        super(message);
        this.isTip = true;
    }

    public ZhiYouException(Throwable cause) {
        super(cause);
        this.isTip = true;
    }

    public ZhiYouException(String message, boolean isTip) {
        this.isTip = isTip;
    }

    public ZhiYouException(String message, CreateMapperTextFieldEnum createMapperTextFieldEnum) {
        super(message);
        isTip = true;
        this.createMapperTextFieldEnum = createMapperTextFieldEnum;
    }

    public boolean isTip() {
        return isTip;
    }

    public CreateMapperTextFieldEnum getCreateMapperTextFieldEnum() {
        return createMapperTextFieldEnum;
    }
}
