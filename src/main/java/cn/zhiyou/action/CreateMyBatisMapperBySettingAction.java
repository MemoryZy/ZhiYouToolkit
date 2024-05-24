package cn.zhiyou.action;

import cn.zhiyou.utils.CompatibilityUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/5/24
 */
public class CreateMyBatisMapperBySettingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        NotificationUtil.notifyApplication("待开发...", NotificationType.WARNING, anActionEvent.getProject());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        // 设置可见性，当Database插件存在时不显示，不存在则显示
        e.getPresentation().setEnabledAndVisible(!CompatibilityUtil.existDatabasePlugin());
    }
}
