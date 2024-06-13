package cn.zhiyou.bundle;

import com.intellij.AbstractBundle;
import com.intellij.DynamicBundle;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 借鉴 Translation 插件
 *
 * @author Memory
 * @since 2024/6/13
 */
public class BaseActionBundle extends AbstractBundle {

    private static final Logger LOG = Logger.getInstance(BaseActionBundle.class);

    private final String pathToBundle;
    private final AbstractBundle adaptedBundle;
    private final ResourceBundle.Control adaptedControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    protected BaseActionBundle(@NonNls @NotNull String pathToBundle) {
        super(pathToBundle);
        this.pathToBundle = pathToBundle;
        this.adaptedBundle = createAdaptedBundle();
    }

    public String getAdaptedMessage(@PropertyKey(resourceBundle = ActionBundle.ACTION_BUNDLE) String key, Object... params) {
        if (adaptedBundle != null) {
            return adaptedBundle.getMessage(key, params);
        }
        return getMessage(key, params);
    }

    private AbstractBundle createAdaptedBundle() {
        Locale dynamicLocale = getDynamicLocale();
        if (dynamicLocale == null) {
            return null;
        }

        if (dynamicLocale.toLanguageTag().equals(Locale.ENGLISH.toLanguageTag())) {
            return new AbstractBundle(pathToBundle) {
                @Override
                protected @NotNull ResourceBundle findBundle(@NotNull String pathToBundle, @NotNull ClassLoader loader, ResourceBundle.@NotNull Control control) {
                    ResourceBundle dynamicBundle = ResourceBundle.getBundle(pathToBundle, dynamicLocale, loader, adaptedControl);
                    return (dynamicBundle != null) ? dynamicBundle : super.findBundle(pathToBundle, loader, control);
                }
            };
        }

        return null;
    }

    public static Locale getDynamicLocale() {
        try {
            return DynamicBundle.getLocale();
        } catch (NoSuchMethodError e) {
            LOG.debug("NoSuchMethodError: DynamicBundle.getLocale()");
            return null;
        }
    }

}
