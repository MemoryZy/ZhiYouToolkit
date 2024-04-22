package cn.zhiyou.action;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.enums.JsonAnnotationEnum;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Java类转Json（递归找嵌套属性）
 *
 * @author wcp
 * @since 2023/11/27
 */
public class JavaBeanConvertToJsonAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(JavaBeanConvertToJsonAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 获取当前类
        PsiClass psiClass = ActionUtil.getPsiClass(event);
        // JsonMap
        Map<String, Object> jsonMap = new TreeMap<>();

        try {
            // 递归添加所有属性，包括嵌套属性
            recursionAddProperty(psiClass, jsonMap);
        } catch (Error e) {
            LOG.error(e);
            // 给通知
            NotificationUtil.notifyApplication("该类属性递归时深度过大，无法继续！", NotificationType.ERROR, project);
            return;
        }

        // 将Map转换为Json
        String jsonStr = JSONUtil.toJsonStr(jsonMap);
        // 添加至剪贴板
        ActionUtil.setClipboard(jsonStr);
        // 给通知
        NotificationUtil.notifyApplication("已拷贝到剪贴板！", NotificationType.INFORMATION, project);
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean enabled = false;
        PsiClass psiClass = ActionUtil.getPsiClass(event);
        if (Objects.nonNull(psiClass)) {
            PsiField[] fields = ActionUtil.getAllFieldFilterStatic(psiClass);
            enabled = ArrayUtil.isNotEmpty(fields);
        }

        // 设置可见性
        event.getPresentation().setEnabledAndVisible(enabled);
    }


    /**
     * 递归将属性转成Map元素
     *
     * @param psiClass class
     * @param jsonMap  Map
     */
    private void recursionAddProperty(PsiClass psiClass, Map<String, Object> jsonMap) {
        // 获取该类所有字段
        PsiField[] allFields = ActionUtil.getAllFieldFilterStatic(psiClass);
        for (PsiField psiField : allFields) {
            // -------------------------- 注解支持
            // 获取Json键名
            String jsonKeyName = getAnnotationJsonKeyName(psiField);

            // 如果加了忽略，则忽略该属性
            if (Objects.equals(JsonAnnotationEnum.JSON_IGNORE.getValue(), jsonKeyName)) {
                continue;
            }

            // 字段名
            String propertyName = (StrUtil.isBlank(jsonKeyName)) ? psiField.getName() : jsonKeyName;

            // 字段类型
            PsiType psiType = psiField.getType();

            // 是否为引用类型
            if (ActionUtil.isReferenceType(psiType)) {
                // 嵌套Map（为了实现嵌套属性）
                Map<String, Object> nestedJsonMap = new HashMap<>();
                // 递归
                recursionAddProperty(ActionUtil.getPsiClassByReferenceType(psiType), nestedJsonMap);
                // 添加至主Map
                jsonMap.put(propertyName, nestedJsonMap);
            } else {
                // key，名称；value，根据全限定名判断生成具体的内容
                jsonMap.put(propertyName, getDefaultValue(psiType.getCanonicalText()));
            }
        }
    }


    /**
     * 获取Json注解中的键名称
     *
     * @param psiField 字段属性
     * @return 键名（如果是{@link JsonAnnotationEnum#JSON_IGNORE}）则表示忽略该字段
     */
    private String getAnnotationJsonKeyName(PsiField psiField) {
        // ---------------------------------- 获取注解判断是否忽略序列化
        // jackson 通过 @JsonIgnore 注解标记是否忽略序列化字段
        PsiAnnotation jacksonIgnore = psiField.getAnnotation(JsonAnnotationEnum.JACKSON_JSON_IGNORE.getValue());
        if (Objects.nonNull(jacksonIgnore)) {
            // 该字段需要忽略
            return JsonAnnotationEnum.JSON_IGNORE.getValue();
        }

        // 检测是否含有 fastjson 注解
        PsiAnnotation fastJsonJsonField = psiField.getAnnotation(JsonAnnotationEnum.FAST_JSON_JSON_FIELD.getValue());
        PsiAnnotation fastJson2JsonField = psiField.getAnnotation(JsonAnnotationEnum.FAST_JSON2_JSON_FIELD.getValue());
        // 检测是否含有 Jackson 注解
        PsiAnnotation jacksonJsonProperty = psiField.getAnnotation(JsonAnnotationEnum.JACKSON_JSON_PROPERTY.getValue());

        String annotationValue = "";
        if (Objects.nonNull(fastJsonJsonField) || Objects.nonNull(fastJson2JsonField)) {
            // 是否忽略序列化
            String serialize = ActionUtil.getMemberValue(fastJsonJsonField, "serialize");
            String serialize2 = ActionUtil.getMemberValue(fastJson2JsonField, "serialize");
            if (Objects.equals(Boolean.FALSE.toString(), serialize) || Objects.equals(Boolean.FALSE.toString(), serialize2)) {
                return JsonAnnotationEnum.JSON_IGNORE.getValue();
            }

            // 获取值
            annotationValue = ActionUtil.getMemberValue(fastJsonJsonField, "name");
            if (StrUtil.isBlank(annotationValue)) {
                annotationValue = ActionUtil.getMemberValue(fastJson2JsonField, "name");
            }

        } else if (Objects.nonNull(jacksonJsonProperty)) {
            annotationValue = ActionUtil.getMemberValue(jacksonJsonProperty, "value");
        }

        return annotationValue;
    }


    /**
     * 根据类全限定名获取其类型的默认值
     *
     * @param canonicalText 全限定名
     * @return 默认值
     */
    private Object getDefaultValue(String canonicalText) {
        Object result;

        if ("boolean".equals(canonicalText) || "java.lang.Boolean".equals(canonicalText)) {
            result = false;

        } else if ("char".equals(canonicalText) || "java.lang.Character".equals(canonicalText) || "java.lang.String".equals(canonicalText)) {
            result = "";

        } else if ("byte".equals(canonicalText) || "java.lang.Byte".equals(canonicalText)
                || "short".equals(canonicalText) || "java.lang.Short".equals(canonicalText)
                || "int".equals(canonicalText) || "java.lang.Integer".equals(canonicalText)
                || "long".equals(canonicalText) || "java.lang.Long".equals(canonicalText)) {
            result = 0;

        } else if ("float".equals(canonicalText) || "java.lang.Float".equals(canonicalText)
                || "double".equals(canonicalText) || "java.lang.Double".equals(canonicalText)
                || "java.math.BigDecimal".equals(canonicalText)) {
            result = 0.0;

        } else if (canonicalText.contains("Date")
                || canonicalText.contains("DateTime")
                || canonicalText.contains("Timestamp")) {
            result = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);

        } else if (canonicalText.contains("List")
                || canonicalText.contains("ArrayList")
                || canonicalText.contains("Collection")) {
            result = new ArrayList<>();

        } else {
            result = null;
        }

        return result;
    }

}
