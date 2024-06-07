package cn.zhiyou.listener;

import cn.zhiyou.bundle.ZhiYouBundle;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author wcp
 * @since 2024/5/27
 */
public class ErrorSubmissionHandler extends ErrorReportSubmitter {

    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return ZhiYouBundle.message("report.action.text");
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo,
                          @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {




        return super.submit(events, additionalInfo, parentComponent, consumer);
    }
}
