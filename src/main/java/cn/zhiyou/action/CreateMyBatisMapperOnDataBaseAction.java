package cn.zhiyou.action;

import cn.hutool.core.util.ArrayUtil;
import cn.zhiyou.ui.CreateMyBatisMapperOnDataBaseWindow;
import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author wcp
 * @since 2024/2/4
 */
public class CreateMyBatisMapperOnDataBaseAction extends AnAction {

    private static final Predicate<PsiElement> checkClass = el -> {
        try {
            return DbTable.class.isAssignableFrom(el.getClass());
        } catch (Exception e) {
            return false;
        }
    };

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        List<DbTable> dbTables = Arrays.stream(psiElements).map(el -> (DbTable) el).toList();
        new CreateMyBatisMapperOnDataBaseWindow(event, project, dbTables).show();
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        // 用户选择的所有元素
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        // 选择了元素
        boolean visible = ArrayUtil.isNotEmpty(psiElements)
                // 安装了Database插件
                && CompatibilityUtil.existDatabasePlugin()
                // 选择的所有元素都是DbTable
                && Arrays.stream(psiElements).allMatch(checkClass);

        e.getPresentation().setEnabledAndVisible(visible);
    }

}
