package cn.zhiyou.action.child;

import cn.zhiyou.action.CreateAnnotationOnFieldAction;
import cn.zhiyou.enums.JsonAnnotationEnum;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class CreateFastJsonAnnotationAction extends AnAction {
    public CreateFastJsonAnnotationAction() {
        super("FastJson Annotation");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CreateAnnotationOnFieldAction.createAnnotation(e,
                "生成FastJson注解",
                "name", JsonAnnotationEnum.FAST_JSON_JSON_FIELD.getValue(), JsonAnnotationEnum.FAST_JSON2_JSON_FIELD.getValue());
    }
}
