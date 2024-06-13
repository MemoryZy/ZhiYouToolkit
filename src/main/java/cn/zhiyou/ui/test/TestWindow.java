package cn.zhiyou.ui.test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Memory
 * @since 2024/2/4
 */
public class TestWindow extends DialogWrapper {
    private JPanel rootPanel;
    private EditorTextField testTf;
    private JButton button1;
    private final Project project;

    public TestWindow(@Nullable Project project) {
        super(project);

        this.project = project;

        button1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });

        init();
    }

    private void createUIComponents() {
        testTf = new EditorTextField();
        testTf.setFont(new Font("Consolas", Font.PLAIN, 15));




    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
