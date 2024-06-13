package cn.zhiyou.listener;

import cn.zhiyou.bundle.ActionBundle;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author Memory
 * @since 2024/5/27
 */
public class ErrorSubmissionHandler extends ErrorReportSubmitter {

    private static final int stacktraceLen = 6500;

    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return ActionBundle.message("action.report.text");
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo,
                          @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        // 参考 https://github.com/Wujiaxuan007/YourProgressBar 项目
        IdeaLoggingEvent event = ArrayUtil.getFirstElement(events);

        String title = "Exception: ";
        String stacktrace = "Please paste the full stacktrace from the IDEA error popup.\n";

        if (event != null) {
            String throwableText = event.getThrowableText();
            String exceptionTitle = throwableText.lines().findFirst().orElse("");
            title += StringUtil.isEmptyOrSpaces(exceptionTitle) ? "<Fill in title>" : exceptionTitle;
            if (!StringUtil.isEmptyOrSpaces(throwableText)) {
                String quotes = "\n```\n";
                stacktrace += quotes + StringUtil.first(throwableText, stacktraceLen, true) + quotes;
            }
        }


        return super.submit(events, additionalInfo, parentComponent, consumer);
    }
}
