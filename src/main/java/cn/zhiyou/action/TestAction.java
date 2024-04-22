package cn.zhiyou.action;

import cn.zhiyou.utils.ActionUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.markdown.lang.MarkdownLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/2/4
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = ActionUtil.getEditor(event);
        EditorImpl editor1 = (EditorImpl) editor;

        Document document = editor.getDocument();

        EditorHighlighter highlighter = editor.getHighlighter();
        VirtualFile virtualFile = editor.getVirtualFile();

        Language languageForPsi = LanguageUtil.getLanguageForPsi(event.getProject(), virtualFile);
        // json、groovy、shell、java、kotlin、sql、xml、html、js


        String name = languageForPsi.getClass().getName();

        String displayName = languageForPsi.getDisplayName();
        String id = languageForPsi.getID();





        IdeaPluginDescriptor[] plugins = PluginManager.getPlugins();

        for (IdeaPluginDescriptor plugin : plugins) {

            System.out.println();
        }

        // JavascriptLanguage


        boolean pluginInstalled = PluginManager.isPluginInstalled(PluginId.getId("org.intellij.groovy"));

        PluginDescriptor pluginByClass = PluginManager.getPluginByClass(languageForPsi.getClass());



    }

}
