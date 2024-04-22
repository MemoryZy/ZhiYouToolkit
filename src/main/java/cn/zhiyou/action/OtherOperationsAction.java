package cn.zhiyou.action;

import cn.hutool.core.util.ArrayUtil;
import cn.zhiyou.action.child.*;
import cn.zhiyou.utils.ActionUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author wcp
 * @since 2024/1/22
 */
public class OtherOperationsAction extends AnAction {
    public static final String ACTION_TITLE = "MyBatis操作";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new HandlePopupGroupAction().actionPerformed(event);
    }

    public static class HandlePopupGroupAction extends DefaultActionGroup {
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
                    new CreateWhereAction(),
                    new CreateResultMapAction(),
                    new CreateNowAction(),
                    new ConvertTimestampAction(),
                    new JasyptOperationsAction(),
                    new ConvertJsonXmlAction(),
                    new TextCompareAction()
            };
        }
    }

    public static void mybatisUpdate(AnActionEvent event) {
        boolean enabled = false;
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        boolean javaFile = Objects.nonNull(psiFile) && (psiFile.getFileType() instanceof JavaFileType);
        boolean xmlFile = psiFile instanceof XmlFile;
        boolean writable = Objects.nonNull(psiFile) && psiFile.isWritable();

        if ((javaFile || xmlFile) && writable) {
            enabled = true;
            if (javaFile) {
                PsiClass psiClass = ActionUtil.getPsiClass(event);
                PsiField[] fields = ActionUtil.getAllFieldFilterStatic(psiClass);
                enabled = ArrayUtil.isNotEmpty(fields);
            }
        }

        event.getPresentation().setEnabled(enabled);
    }
}
