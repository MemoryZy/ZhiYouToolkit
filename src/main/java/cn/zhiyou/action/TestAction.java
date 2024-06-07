package cn.zhiyou.action;

import cn.zhiyou.utils.ActionUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/2/4
 */
public class TestAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(TestAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "File generation") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(false);
                progressIndicator.setText("File generation in progress...");
                progressIndicator.setFraction(0);

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                ActionUtil.runWriteCommandAction(project, () -> {

                    try {
                        throw new RuntimeException();
                    } catch (RuntimeException e) {
                        // LOG.error(e.getMessage(), e);
                        throw e;
                    }

                    // ErrorReportSubmitter

                });


                progressIndicator.setFraction(1.0);
                progressIndicator.setText("Finished");
            }
        });
    }

}
