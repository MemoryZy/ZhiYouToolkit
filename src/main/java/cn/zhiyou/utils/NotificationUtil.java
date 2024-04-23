package cn.zhiyou.utils;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * @author wcp
 * @since 2023/11/27
 */
public class NotificationUtil {

    /**
     * 获取通知组管理器
     */
    private static final NotificationGroupManager MANAGER = NotificationGroupManager.getInstance();
    /**
     * 获取注册的通知组
     */
    public static final NotificationGroup BALLOON = MANAGER.getNotificationGroup("ZhiYou.balloon");
    /**
     * 在通知历史中记录
     */
    public static final NotificationGroup BALLOON_LOG = MANAGER.getNotificationGroup("ZhiYou.balloon.log");
    /**
     * 粘性通知（不会自动消失）
     */
    public static final NotificationGroup STICKY_BALLOON = MANAGER.getNotificationGroup("ZhiYou.sticky.balloon");
    public static final NotificationGroup TOOLWINDOW = MANAGER.getNotificationGroup("ZhiYou.tool.window");
    public static final NotificationGroup NONE = MANAGER.getNotificationGroup("ZhiYou.none");


    /**
     * 给定程序通知
     *
     * @param title            标题
     * @param content          内容
     * @param notificationType 通知级别
     */
    public static void notifyApplication(String title, String content, NotificationType notificationType, Project project) {
        // 使用通知组创建通知
        BALLOON.createNotification(title, content, notificationType).notify(project);
    }


    /**
     * 给定程序通知
     *
     * @param content          内容
     * @param notificationType 通知级别
     */
    public static void notifyApplication(String content, NotificationType notificationType, Project project) {
        // 使用通知组创建通知
        BALLOON.createNotification("", content, notificationType).notify(project);
    }

    /**
     * 通知，会在通知历史中被记录
     */
    public static void notifyWithLog(String title, String content, NotificationType notificationType, Project project) {
        BALLOON_LOG.createNotification(title, content, notificationType).notify(project);
    }

    /**
     * 通知，会在通知历史中被记录
     */
    public static void notifyWithLog(String content, NotificationType notificationType, Project project) {
        BALLOON_LOG.createNotification("", content, notificationType).notify(project);
    }


    /**
     * 发送通知可以携带设置的跳转
     *
     * @param title              通知标题
     * @param content            通知内容
     * @param notificationAction 通知中嵌套的action
     * @param notificationType   通知类型
     * @param project            项目
     */
    public static void notifyWithLink(String title, String content,
                                      NotificationAction[] notificationAction,
                                      NotificationType notificationType,
                                      Project project) {
        // 提示进行配置
        Notification notification = new Notification("ZhiYou.balloon", title, content, notificationType);
        // 在提示消息中，增加一个 Action，可以通过 Action 一步打开配置界面
        for (NotificationAction action : notificationAction) {
            notification.addAction(action);
        }
        // 发送通知
        notification.notify(project);
    }


    /**
     * 粘性通知
     */
    public static void notifyStickyApplication(String title, String content, NotificationType notificationType, Project project) {
        STICKY_BALLOON.createNotification(title, content, notificationType).notify(project);
    }

}
