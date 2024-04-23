package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 用于编辑器中有Json格式化的文本
 *
 * @author wcp
 * @since 2023/12/18
 */
@SuppressWarnings("DuplicatedCode")
public class JsonFormatAction extends AnAction {

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

        // 格式化json
        String formatJsonStr = JSONUtil.formatJsonStr(jsonStr);
        // 获取当前文档内的psiFile
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);

        // 替换文本
        ActionUtil.runWriteCommandAction(project, () -> {
            document.setText(formatJsonStr);
            // 格式化
            if (Objects.nonNull(psiFile)) {
                CodeStyleManager.getInstance(project).reformatText(psiFile, 0, document.getTextLength());
            }
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
        String text = StrUtil.trim(document.getText());

        String jsonStr = null;
        if (StrUtil.isNotBlank(text)) {
            if (CommonUtil.isJson(text)) {
                jsonStr = text;
            } else {
                // 提取json
                jsonStr = CommonUtil.extractJsonStr(text);
            }
        }

        e.getPresentation().setEnabledAndVisible(StrUtil.isNotBlank(jsonStr));
    }
}
