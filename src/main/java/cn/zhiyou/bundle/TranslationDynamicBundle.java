package cn.zhiyou.bundle;

import com.intellij.AbstractBundle;
import com.intellij.DynamicBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.registry.Registry;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationDynamicBundle extends AbstractBundle {

    private static final String BUNDLE = "messages.TranslationBundle";

    private final String pathToBundle;

    private final ResourceBundle.Control adaptedControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private AbstractBundle adaptedBundle;

    public TranslationDynamicBundle(String pathToBundle) {
        super(pathToBundle);
        this.pathToBundle = pathToBundle;
    }

    @Override
    protected ResourceBundle findBundle(String pathToBundle, ClassLoader loader, ResourceBundle.Control control) {
        Locale dynamicLocale = getDynamicLocale();
        if (dynamicLocale != null) {
            ResourceBundle.Control controlUsed = forceFollowLanguagePack ? adaptedControl : control;
            return ResourceBundle.getBundle(pathToBundle, dynamicLocale, loader, controlUsed);
        } else {
            return super.findBundle(pathToBundle, loader, control);
        }
    }

    public String getAdaptedMessage(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        if (adaptedBundle != null) {
            return adaptedBundle.getMessage(key, params);
        } else {
            return getMessage(key, params);
        }
    }

    private static Locale getDynamicLocale() {
        try {
            return DynamicBundle.getLocale();
        } catch (NoSuchMethodError e) {
            LOGGER.debug("NoSuchMethodError: DynamicBundle.getLocale()");
            return null;
        }
    }

    private static final Logger LOGGER = Logger.getInstance(TranslationDynamicBundle.class);

    private static final boolean forceFollowLanguagePack = Registry.get("cn.yiiguxing.plugin.translate.bundle.forceFollowLanguagePack").asBoolean();

    private static final boolean isEnglishLocale(Locale locale) {
        return locale.toLanguageTag().equals(Locale.ENGLISH.toLanguageTag());
    }

    private void initAdaptedBundle() {
        Locale dynamicLocale = getDynamicLocale();
        if (dynamicLocale != null && isEnglishLocale(dynamicLocale)) {
            adaptedBundle = new AbstractBundle(pathToBundle) {
                @Override
                protected ResourceBundle findBundle(String pathToBundle, ClassLoader loader, ResourceBundle.Control control) {
                    ResourceBundle dynamicBundle = ResourceBundle.getBundle(pathToBundle, dynamicLocale, loader, adaptedControl);
                    return dynamicBundle != null ? dynamicBundle : super.findBundle(pathToBundle, loader, control);
                }
            };
        }
    }
}
