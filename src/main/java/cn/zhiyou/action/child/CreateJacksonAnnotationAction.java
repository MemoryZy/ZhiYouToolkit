package cn.zhiyou.action.child;

import cn.zhiyou.action.CreateAnnotationOnFieldAction;
import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.enums.JsonAnnotationEnum;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class CreateJacksonAnnotationAction extends AnAction {
    public CreateJacksonAnnotationAction() {
        super(ActionBundle.message("action.create.Jackson.annotation.text"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CreateAnnotationOnFieldAction.createAnnotation(e, "生成Jackson注解", "value", JsonAnnotationEnum.JACKSON_JSON_PROPERTY.getValue());
    }
}
