package cn.zhiyou.ui.basic.note;

import cn.zhiyou.utils.ActionUtil;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Memory
 * @since 2024/1/19
 */
public class CodeDetailMultiRowLanguageTextField extends LanguageTextField {

    private final Project project;
    private final boolean needBorder;

    public CodeDetailMultiRowLanguageTextField(Language language, @Nullable Project project, @NotNull String value, boolean needBorder) {
        super(language, project, value);
        this.project = project;
        this.needBorder = needBorder;
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        EditorEx editor = super.createEditor();

        ActionUtil.customize(editor);

        editor.getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 开始时横向滚动条定位到初始位置
        LogicalPosition logicalPosition = editor.offsetToLogicalPosition(0);
        editor.getScrollingModel().scrollTo(logicalPosition, ScrollType.RELATIVE);

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            // 消除代码底部红色线条
            DaemonCodeAnalyzer daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(project);
            daemonCodeAnalyzer.setHighlightingEnabled(psiFile, false);
        }

        if (!needBorder) {
            editor.setBorder(null);
        }

        return editor;
    }

}
