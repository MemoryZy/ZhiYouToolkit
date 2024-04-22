package cn.zhiyou.action.child;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.action.CreateSetterGetterMappingAction;
import cn.zhiyou.entity.FieldMethodPair;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class CreateAllGetterAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(CreateAllGetterAction.class);

    public CreateAllGetterAction() {
        super("Create Getter (列举Getter)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        createAllGetter(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(CreateSetterGetterMappingAction.isVarAvailable(e));
    }


    private void createAllGetter(AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 获取当前光标所在的变量
        PsiElement element = ActionUtil.getPsiElementByOffset(event);
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        if (Objects.isNull(localVariable)) {
            NotificationUtil.notifyApplication(CreateSetterGetterMappingAction.ACTION_TITLE, "请处于局部变量上！", NotificationType.WARNING, project);
            return;
        }

        Editor editor = ActionUtil.getEditor(event);
        Document document = editor.getDocument();
        // 获取当前选中的下一行
        int currentLine = ActionUtil.getCurrentLine(editor, document);
        // 下一行开始的偏移量
        int nextLineOffset = ActionUtil.getNextLineStartOffset(currentLine, document);
        // 当前行内容
        String currentLineContent = ActionUtil.getCurrentLineContent(currentLine, document);
        // 缩进
        String blank = CommonUtil.fillBlank(CommonUtil.startBlank(currentLineContent).length());

        try {
            String localVariableName = localVariable.getName();
            // 获取本地变量
            PsiType returnType = localVariable.getType();
            // 本地变量代表的类
            PsiClass returnReferenceType = ActionUtil.getPsiClassByReferenceType(returnType);

            // 验证出参
            if (Objects.isNull(returnReferenceType)) {
                throw new ZhiYouException("局部变量必须明确类型！");
            }

            CreateSetterGetterMappingAction.validateType(returnType, returnReferenceType, "局部变量必须是引用类型！", "局部变量必须明确类型！");
            // 获取类所有字段方法（除静态）
            PsiField[] returnAllFields = ActionUtil.getAllFieldFilterStatic(returnReferenceType);
            PsiMethod[] returnAllMethods = ActionUtil.getAllMethodFilterStatic(returnReferenceType);
            if (ArrayUtil.isEmpty(returnAllMethods) || ArrayUtil.isEmpty(returnAllFields)) {
                throw new ZhiYouException("要生成的类中必须存在合法字段及合法方法！");
            }

            List<FieldMethodPair> getterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                    returnReferenceType.getName(),
                    returnReferenceType.getQualifiedName(),
                    returnAllMethods,
                    returnAllFields,
                    false);

            StringBuilder builder = new StringBuilder();
            for (FieldMethodPair fieldMethodPair : getterFieldMethodPairList) {
                PsiMethod method = fieldMethodPair.method();
                PsiType methodReturnType = method.getReturnType();
                if (Objects.isNull(methodReturnType)) {
                    continue;
                }

                // 类型名称
                String presentableText = methodReturnType.getPresentableText();

                // xx xx = name.getYYY();
                builder.append(
                        StrUtil.format("{}{} {} = {}.{}();\n",
                                blank,
                                presentableText,
                                fieldMethodPair.fieldName(),
                                localVariableName,
                                method.getName()));
            }

            // 下一行写入
            ActionUtil.runWriteCommandAction(project, () -> document.insertString(nextLineOffset, builder.toString()));
        } catch (ZhiYouException e) {
            NotificationUtil.notifyApplication(CreateSetterGetterMappingAction.ACTION_TITLE, e.getMessage(), NotificationType.WARNING, project);
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
