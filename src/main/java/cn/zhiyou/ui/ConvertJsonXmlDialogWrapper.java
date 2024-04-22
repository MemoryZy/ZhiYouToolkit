package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.ui.basic.MultiRowTextField;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.json5.Json5FileType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
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
 * @since 2024/2/7
 */
public class ConvertJsonXmlDialogWrapper extends DialogWrapper {
    private JPanel rootPanel;
    private EditorTextField jsonXmlTf;
    private final Project project;
    private boolean isJsonToXml = true;
    private final TextFieldErrorPopupDecorator textErrorPopupDecorator;

    public ConvertJsonXmlDialogWrapper(@Nullable Project project) {
        super(project, true);
        this.project = project;
        this.textErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), jsonXmlTf);

        setModal(false);

        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                jsonXmlTf.requestFocusInWindow();
            }
        });

        jsonXmlTf.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String text = jsonXmlTf.getText();
                FileType fileType = jsonXmlTf.getFileType();
                Json5FileType json5FileType = Json5FileType.INSTANCE;
                XmlFileType xmlFileType = XmlFileType.INSTANCE;

                if (StrUtil.isNotBlank(text)) {
                    if (CommonUtil.isJson(text) && !Objects.equals(fileType, json5FileType)) {
                        jsonXmlTf.setFileType(json5FileType);
                    } else if (CommonUtil.isXML(text) && !Objects.equals(fileType, xmlFileType)) {
                        jsonXmlTf.setFileType(xmlFileType);
                    }
                }
            }
        });

        setOKButtonText("转XML");
        setCancelButtonText("取消");
        setTitle("JSON/XML转换");
        init();
    }

    private void createUIComponents() {
        jsonXmlTf = new MultiRowTextField("", project, Json5FileType.INSTANCE);
        jsonXmlTf.setFont(new Font("Consolas", Font.PLAIN, 15));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        String jsonXmlText = jsonXmlTf.getText();
        if (StrUtil.isNotBlank(jsonXmlText)) {
            String newText = null;
            boolean isFailed = false;
            if (isJsonToXml) {
                if (CommonUtil.isJson(jsonXmlText)) {
                    newText = CommonUtil.jsonToXml(jsonXmlText);
                } else {
                    textErrorPopupDecorator.setError("无效JSON");
                    isFailed = true;
                }
            } else {
                if (CommonUtil.isXML(jsonXmlText)) {
                    newText = CommonUtil.xmlToJson(jsonXmlText);
                } else {
                    textErrorPopupDecorator.setError("无效XML(纯数字不能作为XML节点名)");
                    isFailed = true;
                }
            }

            if (!isFailed) {
                jsonXmlTf.setText(newText);
            }
        }

        isJsonToXml = true;
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        actions.add(new MyJsonTransformAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
    }

    private class MyJsonTransformAction extends DialogWrapperExitAction {
        public MyJsonTransformAction() {
            super("转JSON", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            // 解密
            isJsonToXml = false;
            doOKAction();
        }
    }
}
