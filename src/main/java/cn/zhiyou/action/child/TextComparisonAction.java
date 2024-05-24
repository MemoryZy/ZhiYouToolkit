package cn.zhiyou.action.child;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class TextComparisonAction extends AnAction {
    public TextComparisonAction() {
        super("Text Comparison (文本比对)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        DocumentContent documentContent1 = DiffContentFactory.getInstance().createEditable(project, "", null);
        // DocumentContent documentContent1 = DiffContentFactory.getInstance().create("");
        DocumentContent documentContent2 = DiffContentFactory.getInstance().createEditable(project, "", null);

        SimpleDiffRequest simpleDiffRequest = new SimpleDiffRequest(
                "文本比对",
                documentContent1,
                documentContent2,
                "Left",
                "Right");

        DiffManager.getInstance().showDiff(event.getProject(), simpleDiffRequest, DiffDialogHints.NON_MODAL);
    }
}
