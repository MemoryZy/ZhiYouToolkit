package cn.zhiyou.utils;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import java.awt.*;

/**
 * @author Memory
 * @since 2024/3/21
 */
public class PopupUtil {

    /**
     * 计算组件下方的位置
     *
     * @param component 组件
     * @param position  位置
     * @return 位置
     */
    public static RelativePoint calculatePoint(Component component, Balloon.Position position) {
        // 假设 component 是某个组件对象，其 size 属性可能是 null
        // 假设 position 是某种枚举类型或者常量值，代表位置（atRight, below, above, atLeft）
        // 假设 offsetX 和 offsetY 是偏移量
        int offsetX = 0;
        int offsetY = 1;

        // 获取组件大小
        Dimension size = component.getSize();
        Point point;

        if (size != null) {
            int x = 0;
            int y = 0;

            if (position == Balloon.Position.atRight) {
                x = size.width;
            }
            if (position == Balloon.Position.below) {
                y = size.height;
            }
            if (position == Balloon.Position.below || position == Balloon.Position.above) {
                x = size.width / 2;
            }
            if (position == Balloon.Position.atLeft || position == Balloon.Position.atRight) {
                y = size.height / 2;
            }

            point = new Point(x + offsetX, y + offsetY);
        } else {
            // 如果 size 为 null，则使用 offsetX 和 offsetY 作为默认坐标
            point = new Point(offsetX, offsetY);
        }

        return new RelativePoint(component, point);
    }


    /**
     * 计算组件下方的位置
     *
     * @param component 组件
     * @return 位置
     */
    public static RelativePoint calculateBelowPoint(Component component) {
        int offsetX = 0;
        int offsetY = 1;
        // 获取组件大小
        Dimension size = component.getSize();
        Point point;

        if (size != null) {
            int x = size.width / 2;
            int y = size.height;

            point = new Point(x + offsetX, y + offsetY);
        } else {
            // 如果 size 为 null，则使用 offsetX 和 offsetY 作为默认坐标
            point = new Point(offsetX, offsetY);
        }

        return new RelativePoint(component, point);
    }


    /**
     * 计算组件上方的位置
     *
     * @param component 组件
     * @return 位置
     */
    public static RelativePoint calculateAbovePoint(Component component) {
        int offsetX = 0;
        int offsetY = 1;
        // 获取组件大小
        Dimension size = component.getSize();
        Point point;

        if (size != null) {
            point = new Point(component.getSize().width / 2, 4);
        } else {
            // 如果 size 为 null，则使用 offsetX 和 offsetY 作为默认坐标
            point = new Point(offsetX, offsetY);
        }

        return new RelativePoint(component, point);
    }


    public static void showHTmlTextBalloon(String htmlContent,
                                           MessageType messageType,
                                           RelativePoint relativePoint,
                                           Balloon.Position position) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlContent, null, messageType.getPopupBackground(), null)
                .setBorderColor(messageType.getPopupBackground())
                .setFadeoutTime(6000L)
                .setHideOnAction(true)
                .setHideOnClickOutside(true)
                .setShadow(true)
                .createBalloon()
                .show(relativePoint, position);
    }

}
