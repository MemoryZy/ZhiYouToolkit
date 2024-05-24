package cn.zhiyou.action;

import cn.hutool.core.util.ArrayUtil;
import cn.zhiyou.action.child.CreateFastJsonAnnotationAction;
import cn.zhiyou.action.child.CreateJacksonAnnotationAction;
import cn.zhiyou.action.child.CreateMyBatisPlusAnnotationAction;
import cn.zhiyou.action.child.CreateSwaggerAnnotationAction;
import cn.zhiyou.ui.CreateAnnotationDialogWrapper;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/1/5
 */
public class CreateAnnotationOnFieldAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new CreateAnnotationPopupGroupAction().actionPerformed(event);
    }

    public static class CreateAnnotationPopupGroupAction extends DefaultActionGroup {

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
            List<AnAction> actions = new ArrayList<>();
            if (CompatibilityUtil.existDatabasePlugin()) {
                actions.add(new CreateMyBatisPlusAnnotationAction());
            }

            actions.add(new CreateSwaggerAnnotationAction());
            actions.add(new CreateFastJsonAnnotationAction());
            actions.add(new CreateJacksonAnnotationAction());

            return actions.toArray(new AnAction[0]);
        }
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = false;
        // 当前工程
        Project project = e.getProject();
        PsiFile psiFile = ActionUtil.getPsiFile(e);

        if (Objects.nonNull(project) && ActionUtil.isJavaFile(e) && ActionUtil.isWrite(psiFile)) {
            PsiClass psiClass = ActionUtil.getPsiClass(e);
            PsiField[] fields = ActionUtil.getAllFieldFilterStaticAndUnWrite(psiClass);
            enable = ArrayUtil.isNotEmpty(fields);
        }

        // 设置可见性
        e.getPresentation().setVisible(enable);
    }


    public static void createAnnotation(AnActionEvent event, String title, String memberName, String... annotationQualifiedNames) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 当前类
        PsiClass psiClass = ActionUtil.getPsiClass(event);
        PsiField[] fields = ActionUtil.getAllFieldFilterStaticAndUnWrite(psiClass);
        List<PsiField> fieldList = Arrays.stream(fields).filter(el -> {
            for (String annotationQualifiedName : annotationQualifiedNames) {
                if (el.hasAnnotation(annotationQualifiedName)) {
                    return false;
                }
            }

            return true;
        }).toList();
        new CreateAnnotationDialogWrapper(project, psiClass, title, fieldList, annotationQualifiedNames[0], memberName).show();
    }

}
