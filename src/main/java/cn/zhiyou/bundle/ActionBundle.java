package cn.zhiyou.bundle;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

/**
 * @author Memory
 * @since 2024/6/9
 */
public class ActionBundle {

    // @NonNls
    private static final String ACTION_BUNDLE = "messages.ActionBundle";

    /**
     * 默认以本地语言为准，加上 DynamicBundle.getLocale() 以语言包为准
     */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(ACTION_BUNDLE, DynamicBundle.getLocale());

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = ACTION_BUNDLE) String key) {
        return BUNDLE.getString(key);
    }

}
