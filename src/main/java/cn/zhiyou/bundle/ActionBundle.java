package cn.zhiyou.bundle;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author Memory
 * @since 2024/6/9
 */
public class ActionBundle extends BaseActionBundle {

    @NonNls
    public static final String ACTION_BUNDLE = "messages.ActionBundle";

    /**
     * 默认以本地语言为准，加上 DynamicBundle.getLocale() 以语言包为准
     */
    private static final ActionBundle BUNDLE = new ActionBundle();

    private ActionBundle() {
        super(ACTION_BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = ACTION_BUNDLE) String key, Object... params) {
        return BUNDLE.getAdaptedMessage(key, params);
    }

}
