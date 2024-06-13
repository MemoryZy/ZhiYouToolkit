package cn.zhiyou.notify;

import cn.zhiyou.config.DoNotAskAgainSetting;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/2/6
 */
public class DoNotAskAgainNotificationAction extends NotificationAction {
    public DoNotAskAgainNotificationAction() {
        super("不再显示");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }

        DoNotAskAgainSetting askAgainSetting = DoNotAskAgainSetting.getInstance(project);
        askAgainSetting.doNotAskAgain = true;

        notification.hideBalloon();
    }
}
