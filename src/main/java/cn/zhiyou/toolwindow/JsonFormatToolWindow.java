package cn.zhiyou.toolwindow;

import cn.zhiyou.ui.JsonFormatWindow;
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
 * @since 2023/12/8
 */
public class JsonFormatToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // toolWindow.hide(null)即可关闭toolWindow窗口
        ContentFactory contentFactory = ContentFactory.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();

        JsonFormatWindow jsonFormatWindow = new JsonFormatWindow(project, (ToolWindowEx) toolWindow);
        Content jsonContent = contentFactory.createContent(jsonFormatWindow.getRootPanel(), null, false);
        jsonContent.setPreferredFocusableComponent(jsonFormatWindow.getJsonEditorTextField());
        contentManager.addContent(jsonContent);
    }


}
