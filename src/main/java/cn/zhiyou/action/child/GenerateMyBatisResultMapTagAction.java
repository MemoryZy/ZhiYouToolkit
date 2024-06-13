package cn.zhiyou.action.child;

import cn.hutool.core.util.ArrayUtil;
import cn.zhiyou.action.ExtendedFeaturesAction;
import cn.zhiyou.ui.CreateMyBatisResultMapWindow;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class GenerateMyBatisResultMapTagAction extends AnAction {

    public GenerateMyBatisResultMapTagAction() {
        super("Generate ResultMap Tag (MyBatis)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        handleResultMap(event);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        ExtendedFeaturesAction.mybatisUpdate(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void handleResultMap(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = ActionUtil.getEditor(event);
        if (Objects.isNull(project)) {
            return;
        }

        boolean needCopy;
        PsiClass psiClass;
        if (ActionUtil.isJavaFile(event)) {
            // 根据类字段生成
            needCopy = true;
            psiClass = ActionUtil.getPsiClass(event);
        } else {
            // 弹窗
            needCopy = false;
            psiClass = ActionUtil.chooseClass(project, "Select Entity");
            if (Objects.isNull(psiClass)) {
                // NotificationUtil.notifyApplication(ACTION_TITLE, "请选择类!", NotificationType.WARNING, project);
                return;
            }
        }

        // 所有属性字段
        PsiField[] fields = ActionUtil.getAllFieldFilterStatic(psiClass);

        // 判空
        if (ArrayUtil.isEmpty(fields)) {
            NotificationUtil.notifyApplication(ExtendedFeaturesAction.ACTION_TITLE, "选择类无任何字段，无法生成!", NotificationType.WARNING, project);
            return;
        }

        // 展示窗
        new CreateMyBatisResultMapWindow(event,project, editor, editor.getDocument(), psiClass, needCopy).show();
    }
}
