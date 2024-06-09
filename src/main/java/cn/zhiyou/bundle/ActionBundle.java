package cn.zhiyou.bundle;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author Memory
 * @since 2024/6/9
 */
public class ActionBundle extends DynamicBundle {

    @NonNls
    public static final String ACTION_BUNDLE = "messages.ActionBundle";

    private static final ActionBundle INSTANCE = new ActionBundle();

    public ActionBundle() {
        super(ACTION_BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = ACTION_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
