package cn.zhiyou.action.child;

import cn.hutool.core.collection.CollUtil;
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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class GenerateMappingAction extends AnAction {

    public GenerateMappingAction() {
        super("Generate Attributes Mapping (类属性映射)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        createMapping(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(ActionUtil.isJavaFile(e) && isParamOrMethod(e));
    }

    /**
     * 创建映射
     *
     * @param event AnActionEvent对象
     */
    private void createMapping(AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        PsiClass psiClass = ActionUtil.getPsiClass(event);
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);

        // 获取当前光标所在的方法
        PsiElement element = ActionUtil.getPsiElementByOffset(event);
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);

        if (Objects.isNull(psiMethod) && Objects.isNull(localVariable)) {
            NotificationUtil.notifyApplication(CreateSetterGetterMappingAction.ACTION_TITLE, "请处于方法区域或局部变量上！", NotificationType.WARNING, project);
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
        String blank;

        // 未匹配到的属性
        List<String> unMatchPropertyList = new ArrayList<>();

        String codeSnippet;
        try {
            // 当前处于局部变量
            if (Objects.nonNull(localVariable)) {
                blank = CommonUtil.fillBlank(CommonUtil.startBlank(currentLineContent).length());
                codeSnippet = handleLocalVariableLogic(project, localVariable, blank, unMatchPropertyList);

            } else {
                // 获取当前行的缩进，并加上4位缩进
                blank = CommonUtil.fillBlank(CommonUtil.startBlank(currentLineContent).length() + 4);

                // 获取方法形参
                PsiParameterList parameterList = psiMethod.getParameterList();
                PsiParameter[] parameters = parameterList.getParameters();
                codeSnippet = (ArrayUtil.isNotEmpty(parameters) && parameters.length == 1)
                        // ------------------------- 第一种，方法有形参，且只有一个
                        ? handleMethodHasOneParamLogic(psiMethod, parameters[0], blank, unMatchPropertyList)
                        // ------------------------- 第二种，方法无形参或有多个形参
                        : handleMethodOtherLogic(project, psiMethod, blank, unMatchPropertyList);
            }
        } catch (ZhiYouException e) {
            if (e.isTip()) {
                NotificationUtil.notifyApplication(CreateSetterGetterMappingAction.ACTION_TITLE, e.getMessage(), NotificationType.WARNING, project);
            }

            return;
        }

        if (StrUtil.isBlank(codeSnippet)) {
            NotificationUtil.notifyApplication(CreateSetterGetterMappingAction.ACTION_TITLE, "类之间没有符合条件的字段映射！", NotificationType.WARNING, project);
            return;
        }

        // 写入
        String finalCodeSnippet = codeSnippet;
        ActionUtil.runWriteCommandAction(project, () -> {
            document.insertString(nextLineOffset, finalCodeSnippet);
            codeStyleManager.reformatText(psiClass.getContainingFile(), 0, document.getTextLength());
        });

        if (unMatchPropertyList.isEmpty()) {
            NotificationUtil.notifyApplication("已成功生成映射代码", NotificationType.INFORMATION, project);
        } else {
            String tip;
            if (unMatchPropertyList.size() <= 3) {
                String format = """
                            未匹配属性数: {}
                            <br/>
                            [ {} ]
                            """;
                tip = StrUtil.format(format,
                        unMatchPropertyList.size(),
                        StrUtil.join(" , ", unMatchPropertyList));
            } else {
                tip = "未匹配属性数: " + unMatchPropertyList.size() + "<br/> - " + StrUtil.join("<br/> - ", unMatchPropertyList);
            }

            NotificationUtil.notifyWithLog("成功", tip, NotificationType.INFORMATION, project);
        }
    }


    private String handleLocalVariableLogic(Project project, PsiLocalVariable localVariable, String blank, List<String> unMatchPropertyList) {
        // ------------------------ 出参判断
        // 获取本地变量
        PsiType returnType = localVariable.getType();
        // 本地变量代表的类
        PsiClass returnReferenceType = ActionUtil.getPsiClassByReferenceType(returnType);

        // 验证出参
        if (Objects.isNull(returnReferenceType)) {
            throw new ZhiYouException("局部变量必须明确类型！");
        }

        // 选择的类
        PsiClass selectClass = ActionUtil.chooseClass(project, "选择映射类");

        // 验证出参
        if (Objects.isNull(selectClass)) {
            throw new ZhiYouException("请选择类用于映射返回类的Setter方法！", false);
        }

        // 验证是否为引用类型及是否为泛型
        CreateSetterGetterMappingAction.validateType(returnType, returnReferenceType, "局部变量必须是引用类型！", "局部变量必须明确类型！");

        // 获取形参、出参所有字段（除静态）
        PsiField[] parameterAllFields = ActionUtil.getAllFieldFilterStatic(selectClass);
        PsiField[] returnAllFields = ActionUtil.getAllFieldFilterStatic(returnReferenceType);

        PsiMethod[] parameterAllMethods = ActionUtil.getAllMethodFilterStatic(selectClass);
        PsiMethod[] returnAllMethods = ActionUtil.getAllMethodFilterStatic(returnReferenceType);

        validateField(parameterAllFields, returnAllFields);
        validateMethod(parameterAllMethods, returnAllMethods);

        // 构建字段与方法之间的关系（出参-set，入参-get）
        List<FieldMethodPair> setterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                returnReferenceType.getName(),
                returnReferenceType.getQualifiedName(),
                returnAllMethods,
                returnAllFields,
                true);

        List<FieldMethodPair> getterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                selectClass.getQualifiedName(),
                selectClass.getQualifiedName(),
                parameterAllMethods,
                parameterAllFields,
                false);

        // 最终生成的代码片段，生成Getter、Setter的映射
        return createMappingGetSet(blank, localVariable.getName(), setterFieldMethodPairList, getterFieldMethodPairList, unMatchPropertyList);
    }


    @SuppressWarnings("DuplicatedCode")
    private String handleMethodHasOneParamLogic(PsiMethod psiMethod, PsiParameter parameter, String blank, List<String> unMatchPropertyList) {
        // ------------------------ 形参判断
        // 形参名
        String parameterName = parameter.getName();
        // 形参类型
        PsiType parameterType = parameter.getType();
        // 将类型转为Class
        PsiClass parameterReferenceType = ActionUtil.getPsiClassByReferenceType(parameterType);

        // ------------------------ 出参判断
        // 获取方法出参
        PsiType returnType = psiMethod.getReturnType();
        // 将出参类型转为Class
        PsiClass returnReferenceType = ActionUtil.getPsiClassByReferenceType(returnType);

        if (Objects.isNull(parameterReferenceType) || Objects.isNull(returnReferenceType)) {
            throw new ZhiYouException("方法返回或方法形参必须明确类型！");
        }

        // 验证是否为引用类型及是否为泛型
        CreateSetterGetterMappingAction.validateType(parameterType, parameterReferenceType, "方法形参必须是引用类型！", "方法形参必须明确类型！");
        CreateSetterGetterMappingAction.validateType(returnType, returnReferenceType, "方法必须有返回，且必须是引用类型！", "方法返回必须明确类型！");

        // 获取形参、出参所有字段（除静态）
        PsiField[] parameterAllFields = ActionUtil.getAllFieldFilterStatic(parameterReferenceType);
        PsiField[] returnAllFields = ActionUtil.getAllFieldFilterStatic(returnReferenceType);

        PsiMethod[] parameterAllMethods = ActionUtil.getAllMethodFilterStatic(parameterReferenceType);
        PsiMethod[] returnAllMethods = ActionUtil.getAllMethodFilterStatic(returnReferenceType);

        validateField(parameterAllFields, returnAllFields);
        validateMethod(parameterAllMethods, returnAllMethods);
        // endregion

        // 构建字段与方法之间的关系（出参-set，入参-get）
        List<FieldMethodPair> setterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                returnReferenceType.getName(),
                returnReferenceType.getQualifiedName(),
                returnAllMethods,
                returnAllFields,
                true);

        List<FieldMethodPair> getterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                parameterName,
                parameterReferenceType.getQualifiedName(),
                parameterAllMethods,
                parameterAllFields,
                false);

        // 最终生成的代码片段，生成Getter、Setter的映射
        return createMappingGetSet(blank, null, setterFieldMethodPairList, getterFieldMethodPairList, unMatchPropertyList);
    }


    private String handleMethodOtherLogic(Project project, PsiMethod psiMethod, String blank, List<String> unMatchPropertyList) {
        // ------------------------ 出参判断
        // 获取方法出参
        PsiType returnType = psiMethod.getReturnType();
        // 将出参类型转为Class
        PsiClass returnReferenceType = ActionUtil.getPsiClassByReferenceType(returnType);

        // 验证出参
        if (Objects.isNull(returnReferenceType)) {
            throw new ZhiYouException("方法返回或方法形参必须明确类型！");
        }

        // 选择的类
        PsiClass selectClass = ActionUtil.chooseClass(project, "Select Class");

        // 验证出参
        if (Objects.isNull(selectClass)) {
            throw new ZhiYouException("请选择类用于映射返回类的Setter方法！", false);
        }

        // 验证是否为引用类型及是否为泛型
        CreateSetterGetterMappingAction.validateType(returnType, returnReferenceType, "方法必须有返回，且必须是引用类型！", "方法返回必须明确类型！");

        // 获取形参、出参所有字段（除静态）
        PsiField[] parameterAllFields = ActionUtil.getAllFieldFilterStatic(selectClass);
        PsiField[] returnAllFields = ActionUtil.getAllFieldFilterStatic(returnReferenceType);

        PsiMethod[] parameterAllMethods = ActionUtil.getAllMethodFilterStatic(selectClass);
        PsiMethod[] returnAllMethods = ActionUtil.getAllMethodFilterStatic(returnReferenceType);

        validateField(parameterAllFields, returnAllFields);
        validateMethod(parameterAllMethods, returnAllMethods);

        // 构建字段与方法之间的关系（出参-set，入参-get）
        List<FieldMethodPair> setterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                returnReferenceType.getName(),
                returnReferenceType.getQualifiedName(),
                returnAllMethods,
                returnAllFields,
                true);

        List<FieldMethodPair> getterFieldMethodPairList = CreateSetterGetterMappingAction.createFieldMethodPair(
                selectClass.getQualifiedName(),
                selectClass.getQualifiedName(),
                parameterAllMethods,
                parameterAllFields,
                false);

        // 最终生成的代码片段，生成Getter、Setter的映射
        return createMappingGetSet(blank, null, setterFieldMethodPairList, getterFieldMethodPairList, unMatchPropertyList);
    }


    /**
     * 生成Getter、Setter的映射
     *
     * @param localVarName              局部变量名
     * @param setterFieldMethodPairList Setter方法
     * @param getterFieldMethodPairList Getter方法
     * @return 代码片段
     */
    private String createMappingGetSet(String blank, String localVarName, List<FieldMethodPair> setterFieldMethodPairList, List<FieldMethodPair> getterFieldMethodPairList, List<String> unMatchPropertyList) {
        StringBuilder builder = new StringBuilder();
        if (CollUtil.isEmpty(setterFieldMethodPairList) || CollUtil.isEmpty(getterFieldMethodPairList)) {
            return builder.toString();
        }

        FieldMethodPair setterMethodPair = setterFieldMethodPairList.get(0);
        FieldMethodPair getterMethodPair = getterFieldMethodPairList.get(0);

        // 是否有局部变量
        boolean noLocalVar = StrUtil.isBlank(localVarName);

        // Set类的类名
        String className = setterMethodPair.classOrParamName();
        // 方法形参名
        String methodParamName = getterMethodPair.classOrParamName();
        // 返回局部变量名
        String returnParamName = noLocalVar ? StrUtil.lowerFirst(className) : localVarName;
        // 链式的空格数
        int chainBlankLength = blank.length() + 8;

        // 在new返回类型的上面构建new选择的对象
        String selectNewCode = "";

        // 有.表示是全限定名，也就是方法无参或多参数的逻辑（如果没有包，这个判断就不成立）
        if (StrUtil.contains(methodParamName, '.')) {
            // 截取全限定名，例如: cn.zhiyou.User得到User
            String methodParamClassName = CommonUtil.qualifiedNameToClassName(methodParamName);
            // 截取最后一个，也就是类名
            methodParamName = methodParamClassName;
            // 如果和出参是同一对象，或同名称，那就改名，否则用选择的类小写头部做名称
            methodParamName = (StringUtils.equalsIgnoreCase(returnParamName, methodParamName)) ? returnParamName + "2" : StrUtil.lowerFirst(methodParamName);
            // new对象代码，new的是选择的对象，例如: User user = new User();
            selectNewCode = StrUtil.format("{}{} {} = new {}();\n", blank, methodParamClassName, methodParamName, methodParamClassName);
        }

        // Setter方法是否链式
        boolean isChain = isChain(setterFieldMethodPairList);

        for (FieldMethodPair setterPair : setterFieldMethodPairList) {
            String fieldName = setterPair.fieldName();

            // 匹配具体的字段，字段名以忽略大小写、驼峰、下划线匹配
            FieldMethodPair getterPair = getterFieldMethodPairList.stream()
                    .filter(el -> CommonUtil.matchCase(el.fieldName(), fieldName))
                    .findFirst()
                    .orElse(null);

            // 没匹配到，说明字段字段不符合set(get)条件
            if (Objects.isNull(getterPair)) {
                unMatchPropertyList.add(fieldName);
                continue;
            }

            // Set方法名
            String setMethodName = setterPair.method().getName();
            // Get方法名
            String getMethodName = getterPair.method().getName();
            // 局部变量，若是链式，则也可能是tab空格
            String preContent = blank + returnParamName;

            if (isChain) {
                // 每次都添加一行set代码，接着在下一次遍历中删除上一次添加的末尾;
                // 判断前面是否声明变量
                if (StrUtil.endWith(builder, ";")) {
                    // 去除末尾的;号
                    int length = builder.length();
                    builder.delete(length - 1, length).append("\n");
                }

                // 前面是否已经声明了局部变量.setXxx，如果是，则不再声明，而是增加空格，并链式调用
                // set代码 例如: user.setName(userDto.getName()).setAge(userDto.getAge());
                boolean containsReturnParamName = StrUtil.contains(builder, returnParamName + ".");
                preContent = containsReturnParamName ? CommonUtil.fillBlank(chainBlankLength) : returnParamName;
            }

            builder.append(
                    StrUtil.format("{}.{}({}.{}());{}",
                            preContent,
                            setMethodName,
                            methodParamName,
                            getMethodName,
                            isChain ? "" : "\n"));
        }

        // 说明没有匹配到相同字段
        if (StrUtil.isBlank(builder)) {
            return builder.toString();
        }

        String newCodeSnippet = blank + localVarName;
        // 已经有局部变量，就不需要new变量
        if (noLocalVar) {
            // new对象代码 例如: User user = new User();
            newCodeSnippet = StrUtil.format("{}{} {} = new {}();", blank, className, returnParamName, className);
        } else {
            builder.insert(0, blank);
        }

        // 如果是链路，则将set片段的局部变量及new代码片段最后的;去掉
        if (isChain && noLocalVar) {
            // 去除;
            newCodeSnippet = newCodeSnippet.substring(0, newCodeSnippet.length() - 1);
            // 去除局部变量名（获取【局部变量名.】的开始和末尾索引位置）
            int startIndex = builder.indexOf(returnParamName + ".");
            int endIndex = startIndex + returnParamName.length();
            builder.delete(startIndex, endIndex);
            // 因为在上面的循环中没有为其set的第一行补充空格，在这里补充
            builder.insert(0, CommonUtil.fillBlank(chainBlankLength));
        }

        return selectNewCode + (noLocalVar ? newCodeSnippet + "\n" : "") + builder;
    }


    /**
     * 判断类的Setter方法是否为链式
     *
     * @param setterFieldMethodPairList Setter方法
     * @return true，链式；false，非链式
     */
    private boolean isChain(List<FieldMethodPair> setterFieldMethodPairList) {
        boolean isChain = true;
        for (FieldMethodPair setterPair : setterFieldMethodPairList) {
            PsiType returnType = setterPair.method().getReturnType();
            // 比较方法返回值是否与本类是同一个，只要有一个Setter方法返回值不是本类，那么表示非链式
            if (Objects.nonNull(returnType) && (!Objects.equals(setterPair.qualifiedName(), returnType.getCanonicalText()))) {
                isChain = false;
                break;
            }
        }

        return isChain;
    }


    private void validateMethod(PsiMethod[] parameterAllMethods, PsiMethod[] returnAllMethods) {
        if (ArrayUtil.isEmpty(parameterAllMethods) || ArrayUtil.isEmpty(returnAllMethods)) {
            throw new ZhiYouException("要生成的类中必须存在合法方法！");
        }
    }

    private void validateField(PsiField[] parameterAllFields, PsiField[] returnAllFields) {
        if (ArrayUtil.isEmpty(parameterAllFields) || ArrayUtil.isEmpty(returnAllFields)) {
            throw new ZhiYouException("要生成的类中必须存在合法字段！");
        }
    }

    public static boolean isParamOrMethod(AnActionEvent e){
        PsiElement element = ActionUtil.getPsiElementByOffset(e);
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        return !(Objects.isNull(psiMethod) && Objects.isNull(localVariable));
    }

}
