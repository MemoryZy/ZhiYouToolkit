package cn.zhiyou.bundle;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class ZhiYouBundle extends DynamicBundle {

    @NonNls
    public static final String ZHI_YOU_BUNDLE = "messages.ZhiYouBundle";

    private static final ZhiYouBundle INSTANCE = new ZhiYouBundle();

    private ZhiYouBundle() {
        super(ZHI_YOU_BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = ZHI_YOU_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

}