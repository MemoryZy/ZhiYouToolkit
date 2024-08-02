package cn.memoryzy.zhiyou.group;

import cn.memoryzy.zhiyou.bundle.ActionBundle;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import icons.ZhiYouToolkitIcons;

/**
 * @author Memory
 * @since 2024/8/2
 */
public class ExtendedFeaturesActionGroup extends DefaultActionGroup implements DumbAware {

    public ExtendedFeaturesActionGroup() {
        super();
        setPopup(true);
        setEnabledInModalContext(true);
        Presentation templatePresentation = getTemplatePresentation();
        templatePresentation.setText(ActionBundle.message("group.extended.features.text"));
        templatePresentation.setDescription(ActionBundle.messageOnSystem("group.extended.features.description"));
        templatePresentation.setIcon(ZhiYouToolkitIcons.OPEN_HAND);
    }

}
