package cn.zhiyou.action;

import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.ui.CodeNoteDetailWindow;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CodeCreateUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Memory
 * @since 2024/1/15
 */
public class RecordCodeNoteAction extends AnAction {

    public RecordCodeNoteAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(ActionBundle.message("action.record.codeNote.text"));
        presentation.setDescription(ActionBundle.message("action.record.codeNote.description"));
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        Editor editor = ActionUtil.getEditor(event);
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        selectedText = CodeCreateUtil.removeShortestIndentation(selectedText);

        // 展示窗口
        new CodeNoteDetailWindow(project, null, null, selectedText, true, false).show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(ActionUtil.isSelected(event));
    }


}
