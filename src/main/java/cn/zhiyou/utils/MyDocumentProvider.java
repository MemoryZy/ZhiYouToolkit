package cn.zhiyou.utils;

import com.intellij.codeInsight.completion.BaseCompletionService;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.LanguageTextField;
import com.intellij.util.TextFieldCompletionProvider;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextCompletionUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/4/9
 */
public class MyDocumentProvider {
    private final Project project;

    public MyDocumentProvider(Project project) {
        this.project = project;
    }

    public Document getDocument(String text, Language language) {
        return createDocument(project, text, language);
    }

    private Document createDocument(Project project, @NotNull String text, Language language) {
        return LanguageTextField.createDocument(
                text,
                language,
                project,
                new DocumentWithCompletionCreator(new MyTextFieldCompletionProvider(), true));
    }

    public static class DocumentWithCompletionCreator extends LanguageTextField.SimpleDocumentCreator {
        @NotNull private final TextCompletionProvider myProvider;
        private final boolean myAutoPopup;
        private final boolean myForbidWordCompletion;

        public DocumentWithCompletionCreator(@NotNull TextCompletionProvider provider, boolean autoPopup) {
            myProvider = provider;
            myAutoPopup = autoPopup;
            myForbidWordCompletion = false;
        }

        public DocumentWithCompletionCreator(@NotNull TextCompletionProvider provider, boolean autoPopup, boolean forbidWordCompletion) {
            myProvider = provider;
            myAutoPopup = autoPopup;
            myForbidWordCompletion = forbidWordCompletion;
        }

        @Override
        public void customizePsiFile(@NotNull PsiFile file) {
            TextCompletionUtil.installProvider(file, myProvider, myAutoPopup);
            file.putUserData(BaseCompletionService.FORBID_WORD_COMPLETION, myForbidWordCompletion);
        }
    }

    private static class MyTextFieldCompletionProvider extends TextFieldCompletionProvider {
        @Override
        protected void addCompletionVariants(@NotNull String s, int i, @NotNull String s1, @NotNull CompletionResultSet completionResultSet) {

        }
    }

}
