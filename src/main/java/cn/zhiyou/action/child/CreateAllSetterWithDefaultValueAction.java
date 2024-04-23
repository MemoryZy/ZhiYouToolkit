package cn.zhiyou.action.child;

import cn.zhiyou.action.CreateSetterGetterMappingAction;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class CreateAllSetterWithDefaultValueAction extends AnAction {
    public CreateAllSetterWithDefaultValueAction() {
        super("Create Setter (有默认值列举Setter)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CreateSetterGetterMappingAction.createAllSetter(e, true);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(CreateSetterGetterMappingAction.isVarAvailable(e));
    }

}
