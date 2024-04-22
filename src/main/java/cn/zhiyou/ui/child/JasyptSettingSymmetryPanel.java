package cn.zhiyou.ui.child;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.JasyptSetting;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.fields.ExpandableTextField;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/4/7
 */
public class JasyptSettingSymmetryPanel {
    private JPanel rootPanel;
    private ExpandableTextField passEtf;
    private ComboBox<String> algorithmCb;
    private ComboBox<String> ivGeneratorCb;

    // "PBEWITHHMACSHA1ANDAES_128",
    // "PBEWITHHMACSHA1ANDAES_256",
    // "PBEWITHHMACSHA224ANDAES_128",
    // "PBEWITHHMACSHA224ANDAES_256",
    // "PBEWITHHMACSHA256ANDAES_128",
    // "PBEWITHHMACSHA256ANDAES_256",
    // "PBEWITHHMACSHA384ANDAES_128",
    // "PBEWITHHMACSHA384ANDAES_256",
    // "PBEWITHHMACSHA512ANDAES_128",
    // "PBEWITHHMACSHA512ANDAES_256",

    public static final String[] algorithmArray = {
            "PBEWithMD5AndDES",
            "PBEWithMD5AndTripleDES",
            "PBEWithSHA1AndDESEDE",
            "PBEWITHSHA1ANDRC2_128",
            "PBEWITHSHA1ANDRC2_40",
            "PBEWITHSHA1ANDRC4_128",
            "PBEWITHSHA1ANDRC4_40"
    };

    public static final String[] ivGeneratorArray = {
            "org.jasypt.iv.NoIvGenerator",
            "org.jasypt.iv.RandomIvGenerator"
            // "org.jasypt.iv.ByteArrayFixedIvGenerator",
            // "org.jasypt.iv.StringFixedIvGenerator"
    };

    public JasyptSettingSymmetryPanel(JasyptSetting jasyptSetting) {
        DefaultComboBoxModel<String> algorithmModel = new DefaultComboBoxModel<>(algorithmArray);
        DefaultComboBoxModel<String> ivGeneratorModel = new DefaultComboBoxModel<>(ivGeneratorArray);

        algorithmCb.setModel(algorithmModel);
        ivGeneratorCb.setModel(ivGeneratorModel);

        passEtf.setText(StrUtil.trimToNull(jasyptSetting.password));

        String algorithm = jasyptSetting.algorithm;
        if (StrUtil.isNotBlank(algorithm)) {
            algorithmCb.setSelectedItem(algorithm);
        }

        String ivGenerator = jasyptSetting.ivGenerator;
        if (StrUtil.isNotBlank(ivGenerator)) {
            ivGeneratorCb.setSelectedItem(ivGenerator);
        }
    }

    private void createUIComponents() {
        passEtf = new ExpandableTextField();
        passEtf.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getPassword() {
        return StrUtil.trim(passEtf.getText());
    }

    public String getAlgorithm() {
        return StrUtil.trim((CharSequence) algorithmCb.getSelectedItem());
    }

    public String getIvGenerator() {
        return StrUtil.trim((CharSequence) ivGeneratorCb.getSelectedItem());
    }
}
