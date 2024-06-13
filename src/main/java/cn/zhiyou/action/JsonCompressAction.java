package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2023/12/21
 */
@SuppressWarnings("DuplicatedCode")
public class JsonCompressAction extends AnAction {

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
        String text = document.getText();
        // 格式化json
        String formatJsonStr = JSONUtil.formatJsonStr(text);

        String compressedJsonString;
        try {
            // 压缩JSON字符串为一行
            compressedJsonString = CommonUtil.compressJson(formatJsonStr);
        } catch (JsonProcessingException e) {
            compressedJsonString = formatJsonStr;
            NotificationUtil.notifyApplication("JSON格式错误！", NotificationType.ERROR, project);
        }

        // 替换文本
        String finalCompressedJsonString = compressedJsonString;
        ActionUtil.runWriteCommandAction(project, () -> {
            document.setText(finalCompressedJsonString);
        });
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
        // 文本
        String text = document.getText();

        e.getPresentation().setEnabledAndVisible(StrUtil.isNotBlank(text) && CommonUtil.isJson(text));
    }
}
