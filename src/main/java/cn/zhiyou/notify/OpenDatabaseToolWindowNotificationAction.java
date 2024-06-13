package cn.zhiyou.notify;

import cn.zhiyou.utils.ActionUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/2/7
 */
public class OpenDatabaseToolWindowNotificationAction extends NotificationAction {

    public OpenDatabaseToolWindowNotificationAction() {
        super("配置Database");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            ActionUtil.openToolWindow(project, "Database");
        }
    }
}
