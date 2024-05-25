package cn.zhiyou.action.child;

import cn.zhiyou.action.CreateSetterGetterMappingAction;
import cn.zhiyou.entity.FieldMethodPair;
import cn.zhiyou.enums.PropertyTreeNodeValueTypeEnum;
import cn.zhiyou.ui.MatchAttributesWindow;
import cn.zhiyou.ui.basic.PropertyMatchMutableTreeNode;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class MatchAttributesAction extends AnAction {

    public MatchAttributesAction() {
        super("Attribute Matching Analysis (类属性匹配)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        propertyCompare(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // 设置可见性
        event.getPresentation().setEnabled(ActionUtil.isJavaFile(event) && CreateSetterGetterMappingAction.hasProperty(event));
    }

    private void propertyCompare(AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 当前类
        PsiClass currentPsiClass = ActionUtil.getPsiClass(event);
        // 选择的类
        PsiClass selectPsiClass = ActionUtil.chooseClass(project, "选择映射类");

        // 验证出参
        if (Objects.isNull(selectPsiClass)) {
            return;
        }

        // 获取两个类的所有具有Get、Set的属性，并进行比较匹配
        PsiField[] currentClassFields = ActionUtil.getAllFieldFilterStatic(currentPsiClass);
        PsiMethod[] currentClassMethods = ActionUtil.getAllMethodFilterStatic(currentPsiClass);

        PsiField[] selectClassFields = ActionUtil.getAllFieldFilterStatic(selectPsiClass);
        PsiMethod[] selectClassMethods = ActionUtil.getAllMethodFilterStatic(selectPsiClass);

        // 寻找所有属性（带有Getter、Setter的属性）
        List<FieldMethodPair> currentFieldMethodPair = CreateSetterGetterMappingAction.createFieldMethodPair(null, null, currentClassMethods, currentClassFields, true);
        List<FieldMethodPair> selectFieldMethodPair = CreateSetterGetterMappingAction.createFieldMethodPair(null, null, selectClassMethods, selectClassFields, true);

        List<PropertyMatchMutableTreeNode> successNodeList = new ArrayList<>();
        List<PropertyMatchMutableTreeNode> failedNodeList = new ArrayList<>();

        for (FieldMethodPair fieldMethodPair : currentFieldMethodPair) {
            String fieldName = fieldMethodPair.fieldName();
            FieldMethodPair matchPair = selectFieldMethodPair.stream()
                    .filter(el -> CommonUtil.matchCase(fieldName, el.fieldName()))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(matchPair)) {
                successNodeList.add(new PropertyMatchMutableTreeNode(
                        fieldName,
                        fieldMethodPair.fieldType(),
                        matchPair.fieldName(),
                        matchPair.fieldType(),
                        PropertyTreeNodeValueTypeEnum.successProperty));
            } else {
                failedNodeList.add(new PropertyMatchMutableTreeNode(fieldName, fieldMethodPair.fieldType(), PropertyTreeNodeValueTypeEnum.failedProperty));
            }
        }

        new MatchAttributesWindow(project, currentPsiClass.getName(), selectPsiClass.getName(),successNodeList, failedNodeList).show();
    }
}
