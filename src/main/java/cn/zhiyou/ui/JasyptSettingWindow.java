package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.JasyptSetting;
import cn.zhiyou.ui.child.JasyptSettingAsymmetricPanel;
import cn.zhiyou.ui.child.JasyptSettingSymmetryPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/4/7
 */
public class JasyptSettingWindow extends DialogWrapper {
    private JPanel northPanel;
    private JBRadioButton symmetryRb;
    private JBRadioButton asymmetricRb;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JasyptSettingSymmetryPanel symmetryWindow;
    private JasyptSettingAsymmetricPanel asymmetricWindow;
    private final Project project;
    private String componentName;
    private final JasyptOperationsWindow jasyptOperationsWindow;

    public JasyptSettingWindow(@Nullable Project project, JasyptOperationsWindow jasyptOperationsWindow) {
        super(project, true);
        this.project = project;
        this.jasyptOperationsWindow = jasyptOperationsWindow;

        initRb();

        setSize(600, 500);
        setOKButtonText("保存");
        setCancelButtonText("取消");
        setModal(false);
        setTitle("配置Jasypt");
        init();
    }

    private void initRb() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(symmetryRb);
        buttonGroup.add(asymmetricRb);

        symmetryRb.addActionListener(new TransformCard());
        asymmetricRb.addActionListener(new TransformCard());
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        return northPanel;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JasyptSetting jasyptSetting = JasyptSetting.getInstance(project);
        symmetryWindow = new JasyptSettingSymmetryPanel(jasyptSetting);
        asymmetricWindow = new JasyptSettingAsymmetricPanel(project, jasyptSetting);

        // 对称加密面板
        cardPanel.add(symmetryWindow.getRootPanel(), JasyptSetting.symmetrical);
        // 非对称加密面板
        cardPanel.add(asymmetricWindow.getRootPanel(), JasyptSetting.asymmetric);

        String encryption = jasyptSetting.encryption;
        if (StrUtil.isBlank(encryption)) {
            // 默认对称
            componentName = JasyptSetting.symmetrical;
            symmetryRb.setSelected(true);
        } else {
            if (Objects.equals(JasyptSetting.symmetrical, encryption)) {
                componentName = JasyptSetting.symmetrical;
                symmetryRb.setSelected(true);
            } else {
                componentName = JasyptSetting.asymmetric;
                asymmetricRb.setSelected(true);
            }
        }

        // 这里要根据配置判断先显示哪个
        cardLayout.show(cardPanel, componentName);

        return cardPanel;
    }

    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            if (executeOkAction()) {
                applyFields();
                close(OK_EXIT_CODE);
            }
        }
    }

    private boolean executeOkAction() {
        JasyptSetting jasyptSetting = JasyptSetting.getInstance(project);
        // 对称
        if (Objects.equals(componentName, JasyptSetting.symmetrical)) {
            jasyptSetting.encryption = JasyptSetting.symmetrical;

            String password = StrUtil.trimToNull(symmetryWindow.getPassword());
            String algorithm = StrUtil.trimToNull(symmetryWindow.getAlgorithm());
            String ivGenerator = StrUtil.trimToNull(symmetryWindow.getIvGenerator());

            String oriPassword = StrUtil.trimToNull(jasyptSetting.password);
            String oriAlgorithm = StrUtil.trimToNull(jasyptSetting.algorithm);
            String oriIvGenerator = StrUtil.trimToNull(jasyptSetting.ivGenerator);

            // 不等于就赋值
            if (!Objects.equals(password, oriPassword)) {
                jasyptSetting.password = password;
            }
            if (!Objects.equals(algorithm, oriAlgorithm)) {
                jasyptSetting.algorithm = algorithm;
            }
            if (!Objects.equals(ivGenerator, oriIvGenerator)) {
                jasyptSetting.ivGenerator = ivGenerator;
            }
        } else {
            // 非对称
            jasyptSetting.encryption = JasyptSetting.asymmetric;

            String publicKey = StrUtil.trimToNull(asymmetricWindow.getPublicKey());
            String privateKey = StrUtil.trimToNull(asymmetricWindow.getPrivateKey());

            String oriPublicKey = StrUtil.trimToNull(jasyptSetting.publicKey);
            String oriPrivateKey = StrUtil.trimToNull(jasyptSetting.privateKey);

            if (!Objects.equals(publicKey, oriPublicKey)) {
                jasyptSetting.publicKey = publicKey;
            }
            if (!Objects.equals(privateKey, oriPrivateKey)) {
                jasyptSetting.privateKey = privateKey;
            }

            // 私钥格式
            jasyptSetting.privateKeyFormat = asymmetricWindow.getSelectFormat();
        }

        jasyptOperationsWindow.validateConfigAndSyncPanel();
        return true;
    }


    private class TransformCard extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (Objects.equals(source, symmetryRb)) {
                cardLayout.show(cardPanel, JasyptSetting.symmetrical);
                componentName = JasyptSetting.symmetrical;
            } else {
                cardLayout.show(cardPanel, JasyptSetting.asymmetric);
                componentName = JasyptSetting.asymmetric;
            }
        }
    }
}
