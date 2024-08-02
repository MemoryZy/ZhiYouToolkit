package cn.memoryzy.zhiyou.action;

import cn.memoryzy.zhiyou.bundle.ActionBundle;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class TextComparisonAction extends DumbAwareAction {

    public TextComparisonAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(ActionBundle.message("action.text.comparison.text"));
        presentation.setDescription(ActionBundle.messageOnSystem("action.text.comparison.description"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 快捷键弹窗
        // EditKeymapsDialog dialog = new EditKeymapsDialog(null, id);
        // ApplicationManager.getApplication().invokeLater(dialog::show);

        // EditorAction

        DocumentContent documentContentLeft = DiffContentFactory.getInstance().createEditable(null, "", null);
        DocumentContent documentContentRight = DiffContentFactory.getInstance().createEditable(null, "", null);

        SimpleDiffRequest simpleDiffRequest = new SimpleDiffRequest(
                ActionBundle.messageOnSystem("action.text.comparison.text"),
                documentContentLeft,
                documentContentRight,
                "Left",
                "Right");

        DiffManager.getInstance().showDiff(null, simpleDiffRequest, DiffDialogHints.NON_MODAL);
    }
}
