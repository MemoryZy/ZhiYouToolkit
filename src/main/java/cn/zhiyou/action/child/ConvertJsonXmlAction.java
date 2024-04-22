package cn.zhiyou.action.child;

import cn.zhiyou.ui.ConvertJsonXmlDialogWrapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class ConvertJsonXmlAction extends AnAction {
    public ConvertJsonXmlAction() {
        super("Convert (JSON/XML)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project))
            return;

        new ConvertJsonXmlDialogWrapper(project).show();
    }

}
