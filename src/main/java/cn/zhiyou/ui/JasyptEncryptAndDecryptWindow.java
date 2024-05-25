package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.JasyptSetting;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.ui.basic.MultiRowTextField;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.ui.child.JasyptSettingSymmetryPanel;
import cn.zhiyou.utils.AsymmetricStringEncryptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.ActionLink;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/1/23
 */
public class JasyptEncryptAndDecryptWindow extends DialogWrapper {
    private JPanel rootPanel;
    private EditorTextField textTf;
    private JPanel warningConfigPanel;
    private JLabel warningTip;
    private ActionLink configLink;
    private final Project project;
    private boolean isEncrypt = true;
    private final TextFieldErrorPopupDecorator textErrorPopupDecorator;
    private final JasyptEncryptAndDecryptWindow jasyptEncryptAndDecryptWindow;

    public JasyptEncryptAndDecryptWindow(@Nullable Project project) {
        super(project, true);
        this.project = project;
        this.textErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), textTf);

        setModal(false);
        setTitle("Jasypt加解密");

        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                textTf.requestFocusInWindow();
            }
        });

        setOKButtonText("加密");
        setCancelButtonText("取消");
        init();

        warningTip.setIcon(Icons.waring);
        warningTip.setText("缺少关键配置，请");

        if (JasyptSetting.validateFullConfig(project)) {
            warningConfigPanel.setVisible(false);
        }

        jasyptEncryptAndDecryptWindow = this;

        configLink.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JasyptSettingWindow(project, jasyptEncryptAndDecryptWindow).show();
            }
        });
        // textErrorPopupDecorator.setError("缺失配置，无法加解密");
    }

    private void createUIComponents() {
        textTf = new MultiRowTextField("", project, null);
        textTf.setFont(new Font("JetBrains Mono", Font.PLAIN, 15));
        textTf.setPreferredSize(new Dimension(480, 450));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        actions.add(new MyDecryptAction());
        actions.add(new MyChangeSettingAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
    }

    @Override
    protected void doOKAction() {
        // 加密
        String text = StrUtil.trim(textTf.getText());
        if (StrUtil.isNotBlank(text)) {
            if (!JasyptSetting.validateFullConfig(project)) {
                textErrorPopupDecorator.setError("缺失配置，无法加解密");
                return;
            }

            if (text.startsWith("ENC(") && text.endsWith(")")) {
                text = text.substring(4);
                text = text.substring(0, text.length() - 1);
            }

            // 加解密
            String newText;
            if (isEncrypt) {
                newText = handleEncrypt(text);
            } else {
                newText = handleDecrypt(text);
            }

            textTf.setText(newText);
        }

        isEncrypt = true;
    }

    public void validateConfigAndSyncPanel() {
        boolean visible = warningConfigPanel.isVisible();
        if (JasyptSetting.validateFullConfig(project)) {
            // 配置完整（窗口设置不可见）
            if (visible) {
                warningConfigPanel.setVisible(false);
            }
        } else {
            // 配置不完整（窗口设置可见）
            if (!visible) {
                warningConfigPanel.setVisible(true);
            }
        }
    }


    /**
     * 处理加密
     *
     * @return 加密后的文本
     */
    private String handleEncrypt(String text) {
        // 根据加密类型判断用哪个类处理
        JasyptSetting setting = JasyptSetting.getInstance(project);
        String encryption = setting.encryption;

        // 对称加密
        if (Objects.equals(encryption, JasyptSetting.symmetrical)) {
            IvGenerator ivGenerator = getIvGenerator(setting.ivGenerator);

            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(setting.algorithm);
            encryptor.setPassword(setting.password);
            if (Objects.nonNull(ivGenerator)) {
                encryptor.setIvGenerator(ivGenerator);
            }

            return encryptor.encrypt(text);
        } else {
            String privateKeyFormat = setting.privateKeyFormat;
            String publicKey = setting.publicKey;
            String privateKey = setting.privateKey;

            AsymmetricStringEncryptor encryptor = AsymmetricStringEncryptor.of(publicKey, privateKey,
                    Objects.equals(privateKeyFormat, JasyptSetting.DER)
                            ? AsymmetricStringEncryptor.KeyFormat.DER
                            : AsymmetricStringEncryptor.KeyFormat.PEM);

            return encryptor.encrypt(text);
        }
    }


    private String handleDecrypt(String text) {
        // 根据加密类型判断用哪个类处理
        JasyptSetting setting = JasyptSetting.getInstance(project);
        String encryption = setting.encryption;

        // 对称加密
        if (Objects.equals(encryption, JasyptSetting.symmetrical)) {
            IvGenerator ivGenerator = getIvGenerator(setting.ivGenerator);

            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setAlgorithm(setting.algorithm);
            encryptor.setPassword(setting.password);
            if (Objects.nonNull(ivGenerator)) {
                encryptor.setIvGenerator(ivGenerator);
            }

            return encryptor.decrypt(text);
        } else {
            String privateKeyFormat = setting.privateKeyFormat;
            String publicKey = setting.publicKey;
            String privateKey = setting.privateKey;

            AsymmetricStringEncryptor encryptor = AsymmetricStringEncryptor.of(publicKey, privateKey,
                    Objects.equals(privateKeyFormat, JasyptSetting.DER)
                            ? AsymmetricStringEncryptor.KeyFormat.DER
                            : AsymmetricStringEncryptor.KeyFormat.PEM);

            return encryptor.decrypt(text);
        }
    }


    private IvGenerator getIvGenerator(String ivGenerator) {
        if (Objects.equals(ivGenerator, JasyptSettingSymmetryPanel.ivGeneratorArray[0])) {
            return new NoIvGenerator();
        } else if (Objects.equals(ivGenerator, JasyptSettingSymmetryPanel.ivGeneratorArray[1])) {
            return new RandomIvGenerator();
        }

        return null;
    }


    private class MyDecryptAction extends DialogWrapperExitAction {
        public MyDecryptAction() {
            super("解密", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            // 解密
            isEncrypt = false;
            doOKAction();
        }
    }

    private class MyChangeSettingAction extends DialogWrapperExitAction {
        public MyChangeSettingAction() {
            super("修改配置", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            new JasyptSettingWindow(project, jasyptEncryptAndDecryptWindow).show();
        }
    }

}
