package cn.zhiyou.action;

import cn.zhiyou.action.child.ChineseToPinyinWithTone;
import cn.zhiyou.action.child.ChineseToPinyinWithoutTone;
import cn.zhiyou.action.child.ConvertToCamelAction;
import cn.zhiyou.action.child.ConvertToSnakeCaseAction;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 字符串操作
 *
 * @author wcp
 * @since 2023/11/27
 */
public class ConvertStringAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new HandleStringPopupGroupAction().actionPerformed(event);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(ActionUtil.isWrite(psiFile) && ActionUtil.isSelected(e));
    }

    public static class HandleStringPopupGroupAction extends DefaultActionGroup {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            if (Objects.isNull(project)) {
                return;
            }
            ListPopup popup = JBPopupFactory.getInstance()
                    .createActionGroupPopup("选择操作", this, e.getDataContext(),
                            JBPopupFactory.ActionSelectionAid.NUMBERING, true);
            popup.showCenteredInCurrentWindow(project);
        }

        @Override
        public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            return new AnAction[]{
                    new ConvertToCamelAction(),
                    new ConvertToSnakeCaseAction(),
                    new ChineseToPinyinWithTone(),
                    new ChineseToPinyinWithoutTone()
            };
        }
    }


    public static boolean containsChinese(AnActionEvent e) {
        // 获取当前光标所在的变量
        boolean enabled = false;
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (Objects.nonNull(editor)) {
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();
            if (StringUtils.isNotBlank(selectedText)) {
                enabled = CommonUtil.containsChinese(selectedText);
            }
        }
        return enabled;
    }

}
