package cn.zhiyou.notify;

import cn.hutool.core.util.URLUtil;
import cn.zhiyou.bundle.ZhiYouBundle;
import cn.zhiyou.constant.PluginHelpConstant;
import cn.zhiyou.utils.ActionUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/**
 * @author wcp
 * @since 2024/2/5
 */
public class OpenHelpNotificationAction extends NotificationAction {
    public OpenHelpNotificationAction() {
        super("使用指南");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }

        // openHelpHtmlEditor(project);

        String message = ZhiYouBundle.message("how.to.use");
        URL url = URLUtil.url(message);
        BrowserUtil.browse(url);
    }


    public static void openHelpHtmlEditor(Project project) {
        String helpHtmlText = PluginHelpConstant.helpHtmlText;
        String theme = UIUtil.isUnderDarcula() ? "theme-dark" : "";
        helpHtmlText = helpHtmlText.replace("__THEME__", theme);

        ActionUtil.openHtmlEditor(project, "How to use ZhiYou", helpHtmlText);
    }


}
