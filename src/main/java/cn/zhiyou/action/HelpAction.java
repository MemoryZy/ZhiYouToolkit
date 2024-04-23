package cn.zhiyou.action;

import cn.zhiyou.ui.AboutZhiYouWindow;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 插件帮助
 *
 * @author wcp
 * @since 2024/2/5
 */
public class HelpAction extends DumbAwareAction {

    // 光 #6C707E
    // 暗 #CED0D6

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        new AboutZhiYouWindow(project).show();
    }
}
