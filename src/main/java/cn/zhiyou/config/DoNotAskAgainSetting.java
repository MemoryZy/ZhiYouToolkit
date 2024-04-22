package cn.zhiyou.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wcp
 * @since 2024/2/6
 */
// @State(name = "ZhiYouDoNotAskAgain", storages = {@Storage(value = "ZhiYouDoNotAskAgain.xml")})
@State(name = "ZhiYouDoNotAskAgain")
public class DoNotAskAgainSetting implements PersistentStateComponent<DoNotAskAgainSetting> {
    public static DoNotAskAgainSetting getInstance(Project project) {
        return project.getService(DoNotAskAgainSetting.class);
    }

    public boolean doNotAskAgain;

    @Override
    public @Nullable DoNotAskAgainSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DoNotAskAgainSetting doNotAskAgainSetting) {
        this.doNotAskAgain = doNotAskAgainSetting.doNotAskAgain;
    }
}
