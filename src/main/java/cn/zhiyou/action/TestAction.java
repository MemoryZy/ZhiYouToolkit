package cn.zhiyou.action;

import cn.zhiyou.ui.DasDataBaseChangeDialog;
import cn.zhiyou.ui.basic.DasMutableTreeNode;
import cn.zhiyou.utils.ActionUtil;
import com.intellij.database.model.DasDataSource;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author wcp
 * @since 2024/2/4
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Editor editor = ActionUtil.getEditor(event);
        // EditorImpl editor1 = (EditorImpl) editor;
        //
        // Document document = editor.getDocument();
        //
        // EditorHighlighter highlighter = editor.getHighlighter();
        // VirtualFile virtualFile = editor.getVirtualFile();
        //
        // Language languageForPsi = LanguageUtil.getLanguageForPsi(event.getProject(), virtualFile);
        // // json、groovy、shell、java、kotlin、sql、xml、html、js
        //
        //
        // String name = languageForPsi.getClass().getName();
        //
        // String displayName = languageForPsi.getDisplayName();
        // String id = languageForPsi.getID();
        //
        //
        //
        //
        //
        // IdeaPluginDescriptor[] plugins = PluginManager.getPlugins();
        //
        // for (IdeaPluginDescriptor plugin : plugins) {
        //
        //     System.out.println();
        // }
        //
        // // JavascriptLanguage
        //
        //
        // boolean pluginInstalled = PluginManager.isPluginInstalled(PluginId.getId("org.intellij.groovy"));
        //
        // PluginDescriptor pluginByClass = PluginManager.getPluginByClass(languageForPsi.getClass());

        List<DasDataSource> dasDataSourceList = ActionUtil.getAllLocalDataSource(event.getProject());

        DasDataBaseChangeDialog dasDataBaseChangeDialog = new DasDataBaseChangeDialog(event.getProject(), dasDataSourceList);
        if (dasDataBaseChangeDialog.showAndGet()) {
            DasMutableTreeNode dasMutableTreeNode = dasDataBaseChangeDialog.getDasMutableTreeNode();

            System.out.println();
        }

    }

}
