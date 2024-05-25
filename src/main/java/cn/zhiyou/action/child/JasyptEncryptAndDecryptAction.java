package cn.zhiyou.action.child;

import cn.zhiyou.ui.JasyptEncryptAndDecryptWindow;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class JasyptEncryptAndDecryptAction extends AnAction {
    public JasyptEncryptAndDecryptAction() {
        // Convert Jasypt Cipher
        super("Jasypt Encrypt/Decrypt (加解密)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project))
            return;

        new JasyptEncryptAndDecryptWindow(project).show();
    }
}
