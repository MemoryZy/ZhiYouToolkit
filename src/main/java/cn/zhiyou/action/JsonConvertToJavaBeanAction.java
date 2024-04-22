package cn.zhiyou.action;

import cn.zhiyou.ui.JsonToJavaBeanDialogWrapper;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Json转换为JavaBean
 *
 * @author wcp
 * @since 2023/11/28
 */
@SuppressWarnings("DuplicatedCode")
public class JsonConvertToJavaBeanAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(JsonConvertToJavaBeanAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        // 当前 module
        Module module = event.getData(PlatformDataKeys.MODULE);
        if (Objects.isNull(project) || Objects.isNull(module)) {
            return;
        }

        // 鼠标右键选择的路径
        IdeView ideView = event.getRequiredData(LangDataKeys.IDE_VIEW);
        // 文件夹(包)
        PsiDirectory directory = ideView.getOrChooseDirectory();
        // 窗口
        new JsonToJavaBeanDialogWrapper(project, directory, module).show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = false;
        // 当前工程
        Project project = e.getProject();
        if (Objects.nonNull(project)) {
            // 鼠标右键选择的路径
            IdeView ideView = null;
            try {
                ideView = e.getRequiredData(LangDataKeys.IDE_VIEW);
            } catch (Throwable ex) {
                // LOG.error(ex.getMessage(), ex);
            }

            if (Objects.nonNull(ideView) && ideView.getDirectories().length > 0) {
                // 文件夹(包)
                PsiDirectory directory = ideView.getOrChooseDirectory();
                if (Objects.nonNull(directory)) {
                    VirtualFile virtualFile = directory.getVirtualFile();
                    String path = virtualFile.getPath();
                    enable = path.contains("/test/java") || path.contains("/main/java");
                }
            }
        }

        // 设置可见性
        e.getPresentation().setVisible(enable);
    }

}
