package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.enums.JavaDocumentEnum;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 单行注释转多行注释
 *
 * @author wcp
 * @since 2023/11/28
 */
public class CommentLineToDocumentAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(CommentLineToDocumentAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = ActionUtil.getEditor(event);
        Document document = editor.getDocument();

        // --> 获取光标所在的当前行的文本
        int currentLine = ActionUtil.getCurrentLine(editor, document);

        // 当前行开始结束的偏移量
        int startOffset = document.getLineStartOffset(currentLine);
        int endOffset = document.getLineEndOffset(currentLine);

        // 当前行内容
        String line = ActionUtil.getCurrentLineContent(currentLine, document);

        // 判空
        if (StringUtils.isBlank(line)) {
            return;
        }

        // --> 获取缩进
        String whiteString = CommonUtil.startBlank(line);

        // --> 截取真正的注释内容
        line = line.replace("/", "")
                .replace("/*", "")
                .replace("*/", "")
                .trim();

        // --> 拼接文档注释
        String doc = StrUtil.format(JavaDocumentEnum.FIELD_DOC.getValue(), line);

        // --> 增加缩进
        StringBuilder builder = new StringBuilder();
        String[] docLines = doc.split("\n");
        for (int i = 0; i < docLines.length; i++) {
            builder.append(whiteString)
                    .append(docLines[i])
                    .append((i == docLines.length - 1) ? "" : "\n");
        }

        // ---> 替换原本的注释
        ActionUtil.runWriteCommandAction(event.getProject(), () -> {
            document.replaceString(startOffset, endOffset, builder.toString());
        });
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        PsiFile psiFile = ActionUtil.getPsiFile(event);
        Editor editor = ActionUtil.getEditor(event);
        Document document = editor.getDocument();
        SelectionModel selectionModel = editor.getSelectionModel();

        // --> 获取光标所在的当前行的文本
        int leadSelectionOffset = selectionModel.getLeadSelectionOffset();
        int endLine = document.getLineNumber(leadSelectionOffset);

        // 当前行内容
        String line = document.getText().substring(document.getLineStartOffset(endLine), document.getLineEndOffset(endLine));

        event.getPresentation().setEnabledAndVisible(
                ActionUtil.isWrite(psiFile)
                        && StrUtil.isNotBlank(line)
                        && (StrUtil.startWith(line.trim(), "//")
                        || (StrUtil.startWith(line.trim(), "/*") && !StrUtil.startWith(line.trim(), "/**"))));
    }

}
