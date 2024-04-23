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
public class CreateAllSetterWithoutDefaultValueAction extends AnAction {
    public CreateAllSetterWithoutDefaultValueAction() {
        super("Create Setter (无默认值列举Setter)");
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
        e.getPresentation().setEnabled(CreateSetterGetterMappingAction.isVarAvailable(e));
    }

}
