package cn.zhiyou.ui.child;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.JasyptSetting;
import cn.zhiyou.ui.basic.MultiRowTextField;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBRadioButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/4/8
 */
public class JasyptSettingAsymmetricPanel extends AbstractAction {
    private static final Logger LOG = Logger.getInstance(JasyptSettingAsymmetricPanel.class);
    private JPanel rootPanel;
    private EditorTextField publicKeyEtf;
    private EditorTextField privateKeyEtf;
    private ActionLink importPublicKeyLink;
    private ActionLink importPrivateKeyLink;
    private JBRadioButton DERRadioButton;
    private JBRadioButton PEMRadioButton;
    private final Project project;

    public JasyptSettingAsymmetricPanel(Project project, JasyptSetting jasyptSetting) {
        this.project = project;

        String publicKey = jasyptSetting.publicKey;
        String privateKey = jasyptSetting.privateKey;

        publicKeyEtf.setText(StrUtil.trimToNull(publicKey));
        privateKeyEtf.setText(StrUtil.trimToNull(privateKey));

        importPublicKeyLink.addActionListener(this);
        importPrivateKeyLink.addActionListener(this);

        initRb();

        String privateKeyFormat = jasyptSetting.privateKeyFormat;
        if (StrUtil.isBlank(privateKeyFormat)) {
            DERRadioButton.setSelected(true);
        } else {
            if (Objects.equals(privateKeyFormat, JasyptSetting.DER)) {
                DERRadioButton.setSelected(true);
            } else {
                PEMRadioButton.setSelected(true);
            }
        }
    }

    private void createUIComponents() {
        publicKeyEtf = new MultiRowTextField("", project, PlainTextFileType.INSTANCE);
        publicKeyEtf.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));

        privateKeyEtf = new MultiRowTextField("", project, PlainTextFileType.INSTANCE);
        privateKeyEtf.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getPublicKey() {
        return StrUtil.trim(publicKeyEtf.getText());
    }

    public String getPrivateKey() {
        return StrUtil.trim(privateKeyEtf.getText());
    }

    public String getSelectFormat() {
        return DERRadioButton.isSelected() ? JasyptSetting.DER : JasyptSetting.PEM;
    }

    private void initRb() {
        ButtonGroup rbButtonGroup = new ButtonGroup();
        rbButtonGroup.add(DERRadioButton);
        rbButtonGroup.add(PEMRadioButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        FileChooserDescriptor chooserDescriptor =
                new FileChooserDescriptor(true, false, false, false, false, false);
        VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, project, null);
        if (virtualFile != null) {
            try {
                String name = virtualFile.getName();
                byte[] bytes = virtualFile.contentsToByteArray();
                if (ArrayUtil.isEmpty(bytes)) {
                    Messages.showWarningDialog("[" + name + "]文件无内容！", "导入");
                    return;
                }

                String importStr = StrUtil.trim(StrUtil.str(bytes, StandardCharsets.UTF_8));
                if (Objects.equals(source, importPublicKeyLink)) {
                    publicKeyEtf.setText("");
                    publicKeyEtf.setText(importStr);
                } else {
                    privateKeyEtf.setText("");
                    privateKeyEtf.setText(importStr);
                }
            } catch (Throwable ex) {
                LOG.error(ex);
            }
        }
    }
}
