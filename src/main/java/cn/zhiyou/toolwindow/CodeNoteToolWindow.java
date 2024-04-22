package cn.zhiyou.toolwindow;

import cn.zhiyou.ui.CodeNoteWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/1/17
 */
public class CodeNoteToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // toolWindow.hide(null)即可关闭toolWindow窗口
        ContentFactory contentFactory = ContentFactory.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();

        CodeNoteWindow codeNoteWindow = new CodeNoteWindow(project, (ToolWindowEx) toolWindow);
        Content content = contentFactory.createContent(codeNoteWindow.getRootPanel(), null, false);
        content.setPreferredFocusableComponent(codeNoteWindow.getShowTable());
        contentManager.addContent(content);
    }

    // @Override
    // public @Nullable Icon getIcon() {
    //     return IconLoader.getIcon("/icons/codeNote.svg", CodeNoteToolWindow.class.getClassLoader());
    // }

}
