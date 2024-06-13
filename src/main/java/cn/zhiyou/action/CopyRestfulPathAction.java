package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Method;
import cn.zhiyou.enums.SpringRequestAnnotationEnum;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 拷贝完整SpringBoot项目接口路径
 *
 * @author Memory
 * @since 2023/12/1
 */
public class CopyRestfulPathAction extends AnAction {

    private static final String ACTION_TITLE = "提取接口路径";

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 获取当前光标所在的方法
        PsiElement element = ActionUtil.getPsiElementByOffset(event);
        // 这些已经在update方法中做了判断，这里就不需要再判断了
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        // 获取类上的@RequestMapping注解
        PsiClass containingClass = psiMethod.getContainingClass();

        // 匹配成功的注解
        PsiAnnotation urlAnnotation = null;
        SpringRequestAnnotationEnum requestAnnotationEnum = null;

        // 再判断是否是接口方法
        PsiAnnotation[] annotations = psiMethod.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            // 轮流与请求注解匹配
            for (SpringRequestAnnotationEnum annotationEnum : SpringRequestAnnotationEnum.values()) {
                if (Objects.equals(annotationEnum.getValue(), annotation.getQualifiedName())) {
                    urlAnnotation = annotation;
                    requestAnnotationEnum = annotationEnum;
                    break;
                }
            }
        }

        if (Objects.isNull(urlAnnotation)) {
            NotificationUtil.notifyApplication(ACTION_TITLE, "您选择的方法并非接口方法！", NotificationType.WARNING, project);
            return;
        }

        // 最终路径
        String requestMappingUrl = "";
        // 类上加的RequestMapping注解
        PsiAnnotation requestMappingAnnotation = containingClass.getAnnotation(SpringRequestAnnotationEnum.REQUEST_MAPPING.getValue());
        if (Objects.nonNull(requestMappingAnnotation)) {
            requestMappingUrl = ActionUtil.getMemberValue(requestMappingAnnotation, "value");
            if (StrUtil.isNotBlank(requestMappingUrl) && !StrUtil.startWith(requestMappingUrl, "/")) {
                requestMappingUrl = "/" + requestMappingUrl;
            }
        }

        // 先获取注解上的value路径值
        String mappingUrl = ActionUtil.getMemberValue(urlAnnotation, "value");
        if (Objects.equals("{}", mappingUrl)) {
            mappingUrl = "";
        } else {
            if (StrUtil.isNotBlank(mappingUrl) && !StrUtil.startWith(mappingUrl, "/")) {
                mappingUrl = "/" + mappingUrl;
            }
        }

        // 在最后校验，因为不管是控制器那边还是接口方法这边都可以没有值，但是不能都没有，必须有一方有
        if (StrUtil.isBlank(requestMappingUrl) && StrUtil.isBlank(mappingUrl)) {
            NotificationUtil.notifyApplication(ACTION_TITLE, "注解上没有声明接口路径值！", NotificationType.WARNING, project);
            return;
        }

        Method method = requestAnnotationEnum.getMethod();

        // 添加至剪贴板
        ActionUtil.setClipboard(StrUtil.format("{}{}{}",
                (Objects.isNull(method)) ? "" : method + " ", requestMappingUrl, mappingUrl));
        // 给通知
        NotificationUtil.notifyApplication(ACTION_TITLE, "已拷贝到剪贴板！", NotificationType.INFORMATION, project);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean visible = false;

        if (ActionUtil.isJavaFile(event)) {
            // 获取当前光标所在的方法
            PsiElement element = ActionUtil.getPsiElementByOffset(event);
            PsiMethod psiMethod = null;
            if (null != element) {
                psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            }

            if (Objects.nonNull(psiMethod) && Objects.nonNull(psiMethod.getContainingClass())) {
                for (PsiAnnotation annotation : psiMethod.getAnnotations()) {
                    // 轮流与请求注解匹配
                    for (SpringRequestAnnotationEnum annotationEnum : SpringRequestAnnotationEnum.values()) {
                        if (Objects.equals(annotationEnum.getValue(), annotation.getQualifiedName())) {
                            visible = true;
                            break;
                        }
                    }
                }
            }
        }

        // 设置可见性
        event.getPresentation().setEnabledAndVisible(visible);
    }

}
