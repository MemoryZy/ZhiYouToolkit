package cn.zhiyou.bundle;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * @author Memory
 * @since 2024/6/13
 */
public class BaseBundle extends AbstractBundle {

    private final String pathToBundle;

    private final ResourceBundle.Control adaptedControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    public BaseBundle(@NonNls @NotNull String pathToBundle) {
        super(pathToBundle);
        this.pathToBundle = pathToBundle;
    }


}
