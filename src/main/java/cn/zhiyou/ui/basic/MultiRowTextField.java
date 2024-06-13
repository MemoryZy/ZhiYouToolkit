package cn.zhiyou.ui.basic;

import cn.zhiyou.utils.ActionUtil;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Memory
 * @since 2024/1/19
 */
public class MultiRowTextField extends EditorTextField {

    public MultiRowTextField(@NotNull String text, Project project, FileType fileType) {
        super(text, project, fileType);
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        EditorEx editor = super.createEditor();

        ActionUtil.customize(editor);

        editor.getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 开始时横向滚动条定位到初始位置
        LogicalPosition logicalPosition = editor.offsetToLogicalPosition(0);
        editor.getScrollingModel().scrollTo(logicalPosition, ScrollType.RELATIVE);

        return editor;
    }
}
