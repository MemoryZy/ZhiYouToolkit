package cn.zhiyou.bundle;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class ZhiYouBundle extends DynamicBundle {

    @NonNls
    public static final String BUNDLE = "messages.ZhiYouBundle";

    private static final ZhiYouBundle INSTANCE = new ZhiYouBundle();

    private ZhiYouBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

}