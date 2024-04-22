package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.*;
import cn.zhiyou.ui.JsonCollectTreeWindow;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/2/27
 */
public class JsonCollectAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        // 只用于编辑器中包含Json的
        Editor editor = ActionUtil.getEditor(event);
        Document document = editor.getDocument();
        // 文本
        String text = StrUtil.trim(document.getText());
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
        // 只用于编辑器中包含Json的
        Editor editor = ActionUtil.getEditor(e);
        Document document = editor.getDocument();
        // 提取json
        String text = StrUtil.trim(document.getText());
        String jsonStr = (CommonUtil.isJson(text)) ? text : CommonUtil.extractJsonStr(text);
        e.getPresentation().setEnabledAndVisible(StrUtil.isNotBlank(jsonStr) && CommonUtil.isJson(jsonStr));
    }

}
