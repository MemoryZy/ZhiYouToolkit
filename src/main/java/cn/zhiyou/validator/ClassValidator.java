package cn.zhiyou.validator;

import cn.hutool.core.util.StrUtil;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightClassUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.util.PsiUtil;

/**
 * @author wcp
 * @since 2024/1/26
 */
public class ClassValidator implements InputValidatorEx {
    private final Project project;
    private final LanguageLevel level;

    public ClassValidator(Project project, PsiDirectory directory) {
        this.project = project;
        level = PsiUtil.getLanguageLevel(directory);
    }

    @Override
    public String getErrorText(String inputString) {
        if (!inputString.isEmpty() && !PsiNameHelper.getInstance(project).isQualifiedName(inputString)) {
            // return JavaErrorBundle.message("create.class.action.this.not.valid.java.qualified.name");
            return "非法类名";
        }
        String shortName = StringUtil.getShortName(inputString);
        if (HighlightClassUtil.isRestrictedIdentifier(shortName, level)) {
            // return JavaErrorBundle.message("restricted.identifier", shortName);
            return StrUtil.format("{} 不能用于类名", shortName);
        }
        return null;
    }

    @Override
    public boolean checkInput(String inputString) {
        return true;
    }

    @Override
    public boolean canClose(String inputString) {
        return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
    }
}
