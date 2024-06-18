package cn.zhiyou.action.child;

import cn.zhiyou.action.CreateSetterGetterMappingAction;
import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.utils.ActionUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


/**
 * @author Memory
 * @since 2024/3/15
 */
public class CreateAllSetterWithoutDefaultValueAction extends AnAction {

    public CreateAllSetterWithoutDefaultValueAction() {
        super(ActionBundle.message("action.create.all.setter.without.default.value.text"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CreateSetterGetterMappingAction.createAllSetter(e, false);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(ActionUtil.isJavaFile(e) && CreateSetterGetterMappingAction.isVarAvailable(e));
    }

}
