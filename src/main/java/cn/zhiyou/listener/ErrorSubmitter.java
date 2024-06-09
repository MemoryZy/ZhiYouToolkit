// package cn.zhiyou.listener;
//
// import com.intellij.ide.BrowserUtil;
// import com.intellij.ide.plugins.PluginManagerCore;
// import com.intellij.openapi.application.ApplicationInfo;
// import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
// import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
// import com.intellij.openapi.diagnostic.SubmittedReportInfo;
// import com.intellij.openapi.extensions.PluginId;
// import com.intellij.openapi.util.SystemInfo;
// import com.intellij.openapi.util.text.StringUtil;
// import com.intellij.util.ArrayUtil;
// import com.intellij.util.Consumer;
// import pers.wjx.plugin.progress.common.ProgressBarBundle;
//
// import java.awt.*;
// import java.net.URLEncoder;
// import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
//
// /**
//  * @author wjx
//  */
// public class ErrorSubmitter extends ErrorReportSubmitter {
//     private final String url = "https://github.com/Wujiaxuan007/YourProgressBar/issues/new?";
//     private final String pluginId = "pers.wjx.plugin.yourProgressBar";
//     private final String label = "exception";
//     private final int stacktraceLen = 6500;
//
//     @Override
//     public String getReportActionText() {
//         return ProgressBarBundle.message("report.to.vendor");
//     }
//
//     @Override
//     public boolean submit(IdeaLoggingEvent[] events, String additionalInfo, Component parentComponent, Consumer<SubmittedReportInfo> consumer) {
//         IdeaLoggingEvent event = ArrayUtil.getFirstElement(events);
//         String title = "Exception: ";
//         String stacktrace = "Please paste the full stacktrace from the IDEA error popup.\n";
//         if (event != null) {
//             String throwableText = event.getThrowableText();
//             String exceptionTitle = throwableText.lines().findFirst().orElse("");
//             title += StringUtil.isEmptyOrSpaces(exceptionTitle) ? "<Fill in title>" : exceptionTitle;
//             if (!StringUtil.isEmptyOrSpaces(throwableText)) {
//                 String quotes = "\n```\n";
//                 stacktrace += quotes + StringUtil.first(throwableText, stacktraceLen, true) + quotes;
//             }
//         }
//         PluginManagerCore.PluginInfo plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginId));
//         String pluginVersion = plugin != null ? plugin.getVersion() : "";
//         String ideaVersion = ApplicationInfo.getInstance().getBuild().asString();
//         StringBuilder template = new StringBuilder();
//         template.append("### Description\n");
//         if (additionalInfo != null) {
//             template.append(additionalInfo).append("\n");
//         }
//         template.append("\n");
//         template.append("### Stacktrace\n").append(stacktrace).append("\n");
//         template.append("### Version and Environment Details\n")
//                 .append("Operation system: ").append(SystemInfo.getOsNameAndVersion()).append("\n")
//                 .append("IDE version: ").append(ideaVersion).append("\n")
//                 .append("Plugin version: ").append(pluginVersion).append("\n");
//         Charset charset = StandardCharsets.UTF_8;
//         String url = String.format(
//                 "%stitle=%s&labels=%s&body=%s",
//                 url,
//                 URLEncoder.encode(title, charset),
//                 URLEncoder.encode(label, charset),
//                 URLEncoder.encode(template.toString(), charset)
//         );
//         BrowserUtil.browse(url);
//         consumer.consume(
//                 new SubmittedReportInfo(
//                         null,
//                         "GitHub issue",
//                         SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
//                 )
//         );
//         return true;
//     }
// }
//
