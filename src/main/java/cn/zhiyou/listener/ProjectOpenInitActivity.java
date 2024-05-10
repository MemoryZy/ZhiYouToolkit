package cn.zhiyou.listener;

import cn.zhiyou.config.DoNotAskAgainSetting;
import cn.zhiyou.notify.DoNotAskAgainNotificationAction;
import cn.zhiyou.notify.OpenHelpNotificationAction;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目启动
 *
 * @author wcp
 * @since 2024/1/2
 */
public class ProjectOpenInitActivity implements ProjectManagerListener {
    private static final Logger LOG = Logger.getInstance(ProjectOpenInitActivity.class);

    @Override
    public void projectOpened(@NotNull Project project) {
        DoNotAskAgainSetting askAgainSetting = DoNotAskAgainSetting.getInstance(project);
        if (!askAgainSetting.doNotAskAgain) {
            NotificationUtil.notifyWithLink(
                    "知游工具",
                    "使用指南：Help -> About ZhiYou",
                    new NotificationAction[]{new OpenHelpNotificationAction(), new DoNotAskAgainNotificationAction()},
                    NotificationType.INFORMATION,
                    project);
        }
    }
}
