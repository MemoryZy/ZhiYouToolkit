package cn.zhiyou.action;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.action.child.*;
import cn.zhiyou.entity.FieldMethodPair;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author wcp
 * @since 2023/12/7
 */
@SuppressWarnings("DuplicatedCode")
public class CreateSetterGetterMappingAction extends AnAction {

    public static final String ACTION_TITLE = "类属性操作";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new CreateSetterPopupGroupAction().actionPerformed(event);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = ActionUtil.getPsiFile(e);

        boolean enabled = false;
        if (ActionUtil.isWrite(psiFile) && ActionUtil.isJavaFile(psiFile)) {
            enabled = isVarAvailable(e) || hasProperty(e) || CreateMappingAction.isParamOrMethod(e);
        }

        e.getPresentation().setEnabledAndVisible(enabled);
    }

    public static class CreateSetterPopupGroupAction extends DefaultActionGroup {
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
                    new CreateAllGetterAction(),
                    new CreateAllSetterWithoutDefaultValueAction(),
                    new CreateAllSetterWithDefaultValueAction(),
                    new PropertyMatchingAction(),
                    new CreateMappingAction()
            };
        }
    }


    /**
     * 引用类型及泛型验证
     *
     * @param psiType              类型
     * @param referenceType        引用类型
     * @param referenceTypeMessage 引用类型提示
     * @param typeParameterMessage 泛型提示
     */
    public static void validateType(PsiType psiType, PsiClass referenceType, String referenceTypeMessage, String typeParameterMessage) {
        // 引用类型判断
        if (!ActionUtil.isReferenceType(psiType)) {
            throw new ZhiYouException(referenceTypeMessage);
        }

        // 泛型判断
        if (Objects.isNull(referenceType) || (referenceType instanceof PsiTypeParameter)) {
            throw new ZhiYouException(typeParameterMessage);
        }
    }

    /**
     * 生成字段与方法的映射
     *
     * @param classOrParamName 类名或形参名（Setter是类名，Getter是形参名）
     * @param qualifiedName    类全限定名名
     * @param psiMethods       所有方法
     * @param psiFields        所有字段
     * @param isSetter         是否获取Setter方法
     * @return 映射集
     */
    public static List<FieldMethodPair> createFieldMethodPair(String classOrParamName, String qualifiedName, PsiMethod[] psiMethods, PsiField[] psiFields, boolean isSetter) {
        List<FieldMethodPair> fieldMethodPairList = new ArrayList<>();

        // 遍历字段，匹配方法
        for (PsiField psiField : psiFields) {
            // 字段名
            String fieldName = psiField.getName();
            // 字段类型
            PsiType type = psiField.getType();

            // 获取方法
            PsiMethod method = isSetter
                    ? matchSetter(fieldName, psiMethods)
                    : matchGetter(fieldName, type.getPresentableText(), psiMethods);

            if (Objects.nonNull(method)) {
                fieldMethodPairList.add(new FieldMethodPair(classOrParamName, qualifiedName, fieldName, method, type.getPresentableText()));
            }
        }

        return fieldMethodPairList;
    }

    /**
     * 字段名匹配Getter方法
     *
     * @param fieldName           字段名
     * @param typePresentableText 类型简单名称（非全限定名）
     * @param psiMethods          方法列表
     * @return 匹配成功的方法
     */
    public static PsiMethod matchGetter(String fieldName, String typePresentableText, PsiMethod[] psiMethods) {
        PsiMethod method;
        // 方法入参限制为空
        Predicate<PsiMethod> checkEmptyParameter = el -> ArrayUtil.isEmpty(el.getParameterList().getParameters());

        // ------------------------------------- 基本类型boolean处理
        // 对于基本类型boolean类型处理，boolean类型有几种可能性：字段名: xx，方法名：getXx、isXx、xx
        if (Objects.equals(boolean.class.getName(), typePresentableText)) {
            // --------------------- 1.验证getXx
            // 首字母改为大写，先尝试用 get 能否获取，不排除有些非布尔类型也用is开头
            String getterName = StrUtil.genGetter(fieldName);
            // Getter方法，也就是字段名 isXxx，方法名 getIsXxx
            method = Arrays.stream(psiMethods)
                    // 匹配该字段的Getter方法，要求方法形参为空
                    .filter(el -> Objects.equals(getterName, el.getName()) && checkEmptyParameter.test(el))
                    .findFirst()
                    .orElse(null);

            // --------------------- 2.验证isXx
            // 如果getXx方法不存在，则验证isXx方法
            if (Objects.isNull(method)) {
                method = Arrays.stream(psiMethods)
                        .filter(el -> Objects.equals(StrUtil.upperFirstAndAddPre(fieldName, "is"), el.getName()) && checkEmptyParameter.test(el))
                        .findFirst()
                        .orElse(null);
            }

            // --------------------- 3.验证xx
            // 如果还是空，则用无参数且方法名相等的方法处理，例如字段名 isXxx，方法名 isXxx
            if (Objects.isNull(method)) {
                method = Arrays.stream(psiMethods)
                        .filter(el -> Objects.equals(fieldName, el.getName()) && checkEmptyParameter.test(el))
                        .findFirst()
                        .orElse(null);
            }
        } else {
            // ------------------------------------- 其余类型处理
            method = Arrays.stream(psiMethods)
                    .filter(el -> Objects.equals(StrUtil.genGetter(fieldName), el.getName()) && checkEmptyParameter.test(el))
                    .findFirst()
                    .orElse(null);
        }

        return method;
    }


    /**
     * 字段名匹配Setter方法
     *
     * @param fieldName  字段名
     * @param psiMethods 方法列表
     * @return 匹配成功的方法
     */
    public static PsiMethod matchSetter(String fieldName, PsiMethod[] psiMethods) {
        PsiMethod method;
        // 方法入参限制不为空
        Predicate<PsiMethod> checkNotEmptyParameter = el -> ArrayUtil.isNotEmpty(el.getParameterList().getParameters());

        // 对于 is 开头的特殊处理
        if (fieldName.startsWith("is")) {
            // 新版是这样 isXxx -> setXxx，忽略 is 前缀
            String setterName = StrUtil.genSetter(fieldName.substring(2));
            // 有参数且方法名相等的方法
            method = Arrays.stream(psiMethods)
                    .filter(el -> Objects.equals(setterName, el.getName()) && checkNotEmptyParameter.test(el))
                    .findFirst()
                    .orElse(null);

            // 如果还是空，则用老版处理
            if (Objects.isNull(method)) {
                method = Arrays.stream(psiMethods)
                        .filter(el -> Objects.equals(fieldName, el.getName()) && checkNotEmptyParameter.test(el))
                        .findFirst()
                        .orElse(null);
            }

        } else {
            method = Arrays.stream(psiMethods)
                    .filter(el -> Objects.equals(StrUtil.genSetter(fieldName), el.getName()) && checkNotEmptyParameter.test(el))
                    .findFirst()
                    .orElse(null);
        }

        return method;
    }

    public static boolean isVarAvailable(AnActionEvent e) {
        boolean enabled = false;
        // 获取当前光标所在的变量
        PsiElement element = ActionUtil.getPsiElementByOffset(e);
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);

        if (Objects.nonNull(localVariable)) {
            // 获取本地变量
            PsiType returnType = localVariable.getType();
            // 本地变量代表的类
            PsiClass returnReferenceType = ActionUtil.getPsiClassByReferenceType(returnType);
            // 验证出参
            if (Objects.nonNull(returnReferenceType)) {
                // 引用类型判断、泛型判断
                enabled = (ActionUtil.isReferenceType(returnType)
                        && !(returnReferenceType instanceof PsiTypeParameter))
                        && ArrayUtil.isNotEmpty(ActionUtil.getAllFieldFilterStatic(returnReferenceType));
            }
        }

        return enabled;
    }


    /**
     * 生成所有 setter 方法
     *
     * @param event      AnActionEvent 对象
     * @param hasDefault 是否有默认值
     */
    public static void createAllSetter(AnActionEvent event, boolean hasDefault) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 获取当前光标所在的变量
        PsiElement element = ActionUtil.getPsiElementByOffset(event);
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        if (Objects.isNull(localVariable)) {
            NotificationUtil.notifyApplication(ACTION_TITLE, "请处于局部变量上！", NotificationType.WARNING, project);
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

            validateType(returnType, returnReferenceType, "局部变量必须是引用类型！", "局部变量必须明确类型！");
            // 获取类所有字段方法（除静态）
            PsiField[] returnAllFields = ActionUtil.getAllFieldFilterStatic(returnReferenceType);
            PsiMethod[] returnAllMethods = ActionUtil.getAllMethodFilterStatic(returnReferenceType);
            if (ArrayUtil.isEmpty(returnAllMethods) || ArrayUtil.isEmpty(returnAllFields)) {
                throw new ZhiYouException("要生成的类中必须存在合法字段及合法方法！");
            }

            List<FieldMethodPair> setterFieldMethodPairList = createFieldMethodPair(
                    returnReferenceType.getName(),
                    returnReferenceType.getQualifiedName(),
                    returnAllMethods,
                    returnAllFields,
                    true);

            StringBuilder builder = new StringBuilder();
            for (FieldMethodPair fieldMethodPair : setterFieldMethodPairList) {
                PsiMethod method = fieldMethodPair.getMethod();
                String defaultByType = "";
                if (hasDefault) {
                    PsiParameter parameter = method.getParameterList().getParameter(0);
                    if (Objects.nonNull(parameter)) {
                        // 默认值
                        defaultByType = getDefaultByType(parameter.getType());
                    }
                }

                // name.setYYY();
                builder.append(
                        StrUtil.format("{}{}.{}({});\n",
                                blank,
                                localVariableName,
                                method.getName(),
                                defaultByType));
            }

            // 下一行写入
            ActionUtil.runWriteCommandAction(project, () -> document.insertString(nextLineOffset, builder.toString()));
        } catch (ZhiYouException e) {
            NotificationUtil.notifyApplication(ACTION_TITLE, e.getMessage(), NotificationType.WARNING, project);
        }
    }


    /**
     * 获取PsiType类型的方法
     *
     * @param type 指定的PsiType类型
     * @return 返回PsiType类型的对象
     */
    public static String getDefaultByType(PsiType type) {
        // 为空或用户自定义的类型
        if (Objects.isNull(type)) {
            return null;
        }

        String result;
        switch (type.getCanonicalText()) {
            case "boolean":
            case "java.lang.Boolean":
                result = Boolean.FALSE.toString();
                break;
            case "byte":
            case "java.lang.Byte":
            case "int":
            case "java.lang.Integer":
            case "short":
            case "java.lang.Short":
                result = "0";
                break;
            case "char":
            case "java.lang.Character":
                result = "'a'";
                break;
            case "long":
            case "java.lang.Long":
                result = "0L";
                break;
            case "float":
            case "java.lang.Float":
                result = "0.0F";
                break;
            case "double":
            case "java.lang.Double":
                result = "0.0D";
                break;
            case "java.math.BigDecimal":
                result = "new BigDecimal(\"0\")";
                break;
            case "String":
            case "java.lang.String":
                result = StrUtil.wrap("test", "\"");
                break;
            case "java.util.Date":
                result = "new Date()";
                break;
            case "java.sql.Date":
                result = "new java.sql.Date(" + new Date().getTime() + ")";
                break;
            case "java.sql.Time":
                result = "new java.sql.Time(" + new Date().getTime() + ")";
                break;
            case "java.sql.Timestamp":
                result = "java.sql.Timestamp.valueOf(" + DateUtil.formatDateTime(new Date()) + ")";
                break;
            case "byte[]":
                result = "new byte[]";
                break;
            case "java.util.Set":
            case "java.util.HashSet":
                result = "new HashSet<>()";
                break;
            case "java.util.LinkedHashSet":
                result = "new LinkedHashSet<>()";
                break;
            case "java.time.LocalDateTime":
                result = "LocalDateTime.now()";
                break;
            case "java.time.LocalDate":
                result = "LocalDate.now()";
                break;
            case "java.util.Collection":
            case "java.util.List":
            case "java.util.ArrayList":
                result = "new ArrayList<>()";
                break;
            case "java.util.LinkedList":
                result = "new LinkedList<>()";
                break;
            case "java.time.LocalTime":
                result = "LocalTime.now()";
                break;
            case "java.util.concurrent.ConcurrentHashMap":
                result = "new ConcurrentHashMap<>()";
                break;
            case "java.util.LinkedHashMap":
                result = "new LinkedHashMap<>()";
                break;
            default:
                result = null;
        }

        return result;
    }


    public static boolean hasProperty(AnActionEvent event) {
        boolean enabled = false;
        PsiClass psiClass = ActionUtil.getPsiClass(event);
        if (Objects.nonNull(psiClass)) {
            PsiField[] fields = ActionUtil.getAllFieldFilterStatic(psiClass);
            enabled = ArrayUtil.isNotEmpty(fields);
        }

        return enabled;
    }

}
