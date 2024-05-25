package cn.zhiyou.ui;

import cn.hutool.core.util.URLUtil;
import cn.zhiyou.bundle.ZhiYouBundle;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.notify.OpenHelpNotificationAction;
import cn.zhiyou.utils.ActionUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.impl.HTMLEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/4/10
 */
public class AboutZhiYouWindow extends DialogWrapper {
    private JPanel rootPanel;
    private JLabel icon;
    private JBLabel content;
    private JBLabel title;
    private JBLabel footer;
    private final Project project;

    public static final String contentStr = """
            <html>
            <head>

            </head>
            <body>
            <p>
                -&nbsp;人生天地之间
            </p>
            <p>
                -&nbsp;若白驹之过隙
            </p>
            <p>
                -&nbsp;忽然而已
            </p>
            </body>
            </html>""";

    public static final String footerStr = """
            <html>
            <head>

            </head>
            <body>
            <p>
                如若发现问题，请及时<a href="https://github.com/MemoryZy/ZhiYouToolkit/issues">反馈</a>给我，感谢支持！
            </p>
            </body>
            </html>""";

    public AboutZhiYouWindow(@Nullable Project project) {
        super(project, true);
        this.project = project;

        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId(ZhiYouBundle.message("app.id")));
        if (Objects.isNull(plugin)) {
            throw new ZhiYouException("未安装本插件，无法使用!");
        }

        title.setCopyable(true);
        title.setText(ZhiYouBundle.message("app.name") + " v" + plugin.getVersion());

        content.setCopyable(true);
        content.setText(contentStr);
        icon.setIcon(Icons.zhiyou_big);

        footer.setCopyable(true);
        footer.setText(footerStr);

        setModal(false);
        setTitle("About ZhiYou");
        setOKButtonText("帮助");
        setCancelButtonText("关闭");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            // OpenHelpNotificationAction.openHelpHtmlEditor(project);

            String message = ZhiYouBundle.message("how.to.use");
            URL url = URLUtil.url(message);
            BrowserUtil.browse(url);
        }
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        actions.add(new AboutMeAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
    }

    private class AboutMeAction extends DialogWrapperExitAction {
        public AboutMeAction() {
            super("关于我", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            String message = ZhiYouBundle.message("home.page");
            URL url = URLUtil.url(message);
            BrowserUtil.browse(url);
        }
    }

}
