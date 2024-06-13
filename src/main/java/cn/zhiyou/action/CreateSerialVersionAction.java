package cn.zhiyou.action;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CodeCreateUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 生成序列化Id
 *
 * @author Memory
 * @since 2023/11/27
 */
public class CreateSerialVersionAction extends AnAction {

    public CreateSerialVersionAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(ActionBundle.message("action.create.serialVersion.text"));
        presentation.setDescription(ActionBundle.message("action.create.serialVersion.description"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 1.获取当前类
        PsiClass psiClass = ActionUtil.getPsiClass(event);

        if (Objects.nonNull(psiClass)) {
            // 2.判断是否实现 java.io.Serializable 接口
            boolean isImplementSerializable = false;
            PsiClassType[] implementsListTypes = psiClass.getImplementsListTypes();

            for (PsiClassType implementsListType : implementsListTypes) {
                // 获取全限定名
                PsiClass resolve = implementsListType.resolve();
                isImplementSerializable = Objects.nonNull(resolve)
                        && Objects.equals("java.io.Serializable", resolve.getQualifiedName());
            }

            // 3.判断是否存在 静态变量 serialVersionUID
            boolean hasSerialVersionUid =
                    Arrays.stream(psiClass.getFields())
                            .anyMatch(el -> Objects.equals("serialVersionUID", el.getName()));

            // 如果没有该变量，则生成这个静态变量
            if (!hasSerialVersionUid) {
                String qualifiedName = psiClass.getQualifiedName();
                PsiField[] allFields = psiClass.getAllFields();
                PsiMethod[] allMethods = psiClass.getAllMethods();

                String serialVersionUidFieldText = getSerialVersionUidFieldText(qualifiedName, allFields, allMethods);
                CodeCreateUtil.addSingleField(event.getProject(), psiClass, serialVersionUidFieldText);

                // 如果没有实现 java.io.Serializable 接口，那么加上一个实现
                if (!isImplementSerializable) {
                    // 根据类的全限定名查询PsiClass，下面这个方法是查询Project域
                    PsiClass refClass = ActionUtil.findClass(project, Serializable.class.getName());
                    // 添加实现
                    CodeCreateUtil.addSingleImplements(project, psiClass, refClass);
                }
            }
        }
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = false;

        if (ActionUtil.isJavaFile(e)) {
            PsiClass psiClass = ActionUtil.getPsiClass(e);
            if (Objects.nonNull(psiClass) && psiClass.isWritable()) {
                // 判断是否存在 静态变量 serialVersionUID
                enable = Arrays.stream(psiClass.getFields()).noneMatch(el -> Objects.equals("serialVersionUID", el.getName()));
            }
        }

        e.getPresentation().setEnabledAndVisible(enable);
    }


    private String getSerialVersionUidFieldText(String qualifiedName, PsiField[] allFields, PsiMethod[] allMethods) {
        String[] fieldArray = Arrays.stream(allFields).map(el -> {
            // 拼接 private java.lang.String id
            PsiType type = el.getType();
            String canonicalText = type.getCanonicalText();
            String name = el.getName();

            StringBuilder modifier = new StringBuilder();
            PsiModifierList modifierList = el.getModifierList();
            if (Objects.nonNull(modifierList)) {
                PsiElement[] children = modifierList.getChildren();
                if (ArrayUtil.isNotEmpty(children)) {
                    for (PsiElement child : children) {
                        modifier.append(child.getText());
                    }
                }
            }

            return modifier + canonicalText + name;
        }).toArray(String[]::new);

        String[] methodArray = Arrays.stream(allMethods).map(el -> {
            // 拼接 private java.lang.String idjava.lang.String param
            StringBuilder modifier = new StringBuilder();
            PsiModifierList modifierList = el.getModifierList();
            PsiElement[] children = modifierList.getChildren();
            if (ArrayUtil.isNotEmpty(children)) {
                for (PsiElement child : children) {
                    modifier.append(child.getText());
                }
            }

            // 构造器没有returnType
            String returnCanonicalText = "";
            PsiType returnType = el.getReturnType();
            if (Objects.nonNull(returnType)) {
                returnCanonicalText = returnType.getCanonicalText();
            }

            String name = el.getName();

            StringBuilder param = new StringBuilder();
            PsiParameterList parameterList = el.getParameterList();
            PsiParameter[] parameters = parameterList.getParameters();
            if (ArrayUtil.isNotEmpty(parameters)) {
                for (PsiParameter parameter : parameters) {
                    PsiType parameterType = parameter.getType();
                    String paramCanonicalText = parameterType.getCanonicalText();
                    String parameterName = parameter.getName();
                    param.append(paramCanonicalText).append(parameterName);
                }
            }

            return modifier + returnCanonicalText + name + param;
        }).toArray(String[]::new);

        long serializationId = CodeCreateUtil.generateSerializationId(qualifiedName, fieldArray, methodArray);
        return StrUtil.format("private static final long serialVersionUID = {}L;", serializationId);
    }

}
