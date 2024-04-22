package cn.zhiyou.notify;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2023/12/20
 */
public class OpenDataBaseSettingNotificationAction extends NotificationAction {

    public OpenDataBaseSettingNotificationAction() {
        super("配置插件数据源..");
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
        // 消息通知代码中，我们第一次使用到消息通知 Action，在消息通知中绑定了一个 NotificationAction 实现类，
        // NotificationAction 与我们前面接受过的 Action 概念类似，只是表现形式不同。
        // 此处实现的逻辑是用户可以通过点击消息通知中的高亮文字，触发 Action。
        // Action 使用到 ShowSettingsUtil 工具，
        // ShowSettingsUtil 类是由 IntelliJ SDK 提供的一个打开配置界面的工具类，在需要快捷打开配置界面的场景中经常会用到。

        // IntelliJ SDK 提供的一个工具类，可以通过配置项名字，直接显示对应的配置界面
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), "ZhiYouToolkit");
        notification.expire();
    }
}
