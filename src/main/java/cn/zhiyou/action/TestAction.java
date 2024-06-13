package cn.zhiyou.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/2/4
 */
public class TestAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(TestAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        RuntimeException exception = new RuntimeException("Acs");

        LOG.error(exception.getMessage(), exception);

        // Project project = event.getProject();
        // ProgressManager.getInstance().run(new Task.Backgroundable(project, "File generation") {
        //     @Override
        //     public void run(@NotNull ProgressIndicator progressIndicator) {
        //         progressIndicator.setIndeterminate(false);
        //         progressIndicator.setText("File generation in progress...");
        //         progressIndicator.setFraction(0);
        //
        //         try {
        //             Thread.sleep(500L);
        //         } catch (InterruptedException e) {
        //             throw new RuntimeException(e);
        //         }
        //
        //
        //         ActionUtil.runWriteCommandAction(project, () -> {
        //
        //             try {
        //                 throw new RuntimeException();
        //             } catch (RuntimeException e) {
        //                 // LOG.error(e.getMessage(), e);
        //                 throw e;
        //             }
        //
        //             // ErrorReportSubmitter
        //
        //         });
        //
        //
        //         progressIndicator.setFraction(1.0);
        //         progressIndicator.setText("Finished");
        //     }
        // });
    }

}
