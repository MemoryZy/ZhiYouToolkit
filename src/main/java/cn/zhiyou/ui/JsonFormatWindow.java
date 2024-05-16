package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.ui.basic.MultiRowLanguageTextField;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.json.json5.Json5Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author wcp
 * @since 2023/12/11
 */
public class JsonFormatWindow {
    private JPanel rootPanel;
    private EditorTextField jsonEditorTextField;
    private final Project project;
    private final ToolWindowEx toolWindow;

    public JsonFormatWindow(Project project, ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        toolWindow.setTitleActions(List.of(new JsonCollectTreeAction()));
    }

    private void createUIComponents() {
        jsonEditorTextField = new MultiRowLanguageTextField(Json5Language.INSTANCE, project, "", false);
        jsonEditorTextField.setFont(new Font("Consolas", Font.PLAIN, 15));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public EditorTextField getJsonEditorTextField() {
        return jsonEditorTextField;
    }


    private class JsonCollectTreeAction extends AnAction {
        public JsonCollectTreeAction() {
            super("JSON结构化", null, Icons.structure);
            ContentManager contentManager = toolWindow.getContentManager();
            JComponent component = contentManager.getComponent();
            registerCustomShortcutSet(CustomShortcutSet.fromString("ctrl alt P"), component);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            String text = StrUtil.trim(jsonEditorTextField.getText());
            String jsonStr = (CommonUtil.isJson(text)) ? text : CommonUtil.extractJsonStr(text);

            // 文档输入后检测Json数组，输出数量
            if (StrUtil.isNotBlank(jsonStr)) {
                if (CommonUtil.isJson(jsonStr)) {
                    JSON json = JSONUtil.parse(jsonStr, JSONConfig.create().setIgnoreNullValue(false));
                    new JsonCollectTreeWindow(project, json).show();
                }
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            String text = StrUtil.trim(jsonEditorTextField.getText());
            String jsonStr = (CommonUtil.isJson(text)) ? text : CommonUtil.extractJsonStr(text);
            e.getPresentation().setEnabled(StrUtil.isNotBlank(jsonStr) && CommonUtil.isJson(jsonStr));
        }
    }

}
