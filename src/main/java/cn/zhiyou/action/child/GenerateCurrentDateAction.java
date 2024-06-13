package cn.zhiyou.action.child;

import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class GenerateCurrentDateAction extends AnAction {
    public GenerateCurrentDateAction() {
        super("Generate Current Date (当前日期)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.insertText(e, CommonUtil.getDateNow());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(ActionUtil.isWrite(e));
    }

}
