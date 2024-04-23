package cn.zhiyou.action.child;

import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class CreateNowAction extends AnAction {
    public CreateNowAction() {
        super("Create Now (日期)");
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
