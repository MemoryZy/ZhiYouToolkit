package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author Memory
 * @since 2024/8/2
 */
public class ZhiYouToolkitIcons {


    public static Icon LOGO = load("/icons/logo.svg");
    public static Icon OPEN_HAND = load("/icons/open_hand.svg");


    public static Icon load(String iconPath) {
        return IconLoader.getIcon(iconPath, ZhiYouToolkitIcons.class);
    }

}
