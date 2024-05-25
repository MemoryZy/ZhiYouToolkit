package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.CodeNoteSetting;
import cn.zhiyou.entity.CodeNoteEntity;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import cn.zhiyou.enums.LanguageEnum;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.ui.basic.note.CodeDetailMultiRowLanguageTextField;
import cn.zhiyou.utils.*;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

/**
 * @author wcp
 * @since 2024/1/19
 */
public class CodeNoteDetailDialogWrapper extends DialogWrapper {
    private JPanel rootPanel;
    private JLabel codeNameLb;
    private EditorTextField codeNameTf;
    private EditorTextField codeContentTf;
    private ComboBox<String> codeLabelCb;
    private ActionLink codeTypeLink;
    private final Project project;
    private final String id;
    private final boolean add;
    private final boolean needFormat;
    private final String codeContent;
    private final TextFieldErrorPopupDecorator codeNameErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator codeContentErrorPopupDecorator;

    public CodeNoteDetailDialogWrapper(@Nullable Project project, String id, String codeName, String codeContent, boolean add, boolean needFormat) {
        super(project, true);
        this.project = project;
        this.id = id;
        this.add = add;
        this.needFormat = needFormat;
        this.codeContent = codeContent;

        this.codeNameErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), codeNameTf);
        this.codeContentErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), codeContentTf);

        codeName = StrUtil.trim(codeName);
        if (Objects.nonNull(codeName)) {
            codeNameTf.setText(codeName);
        }

        codeNameTf.setPlaceholder("Name");

        codeNameLb.setText(codeName);
        codeContentTf.setText(codeContent);
        codeNameTf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String nameText = codeNameTf.getText();
                // 最大字符数
                if (nameText.length() > 25) {
                    codeNameLb.setText(nameText.substring(0, 22) + "...");
                } else {
                    codeNameLb.setText(codeNameTf.getText());
                }
            }
        });

        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        if (labelList == null) {
            labelList = new ArrayList<>();
            CodeNoteSetting.getInstance().labelList = labelList;
            labelList.add(new CodeNoteLabelEntity(-1, "默认"));
        }

        if (CollUtil.isNotEmpty(labelList)) {
            for (CodeNoteLabelEntity labelEntity : labelList) {
                codeLabelCb.addItem(labelEntity.getLabel());
            }
        }

        String codeType = LanguageEnum.Text.name();
        CodeNoteEntity codeNoteEntity = CodeNoteEntity.of(id);
        if (Objects.nonNull(codeNoteEntity)) {
            CodeNoteLabelEntity codeNoteLabelEntity = CodeNoteLabelEntity.of(codeNoteEntity.getLabelId());
            if (Objects.nonNull(codeNoteLabelEntity)) {
                codeLabelCb.setSelectedItem(codeNoteLabelEntity.getLabel());
            } else {
                codeLabelCb.setSelectedItem("默认");
            }

            String codeTypeStr = codeNoteEntity.getCodeType();
            codeType = Objects.isNull(codeTypeStr) ? LanguageEnum.Text.name() : codeTypeStr;
        }

        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (add)
                    codeNameTf.requestFocusInWindow();
            }
        });

        codeTypeLink.setText(codeType);
        codeTypeLink.addActionListener(new CodeTypeAction());

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("保存并退出");
        setCancelButtonText("取消");

        setTitle("笔记详情");
        init();
    }


    private void createUIComponents() {
        Language language = PlainTextLanguage.INSTANCE;
        CodeNoteEntity codeNoteEntity = CodeNoteEntity.of(id);
        if (Objects.nonNull(codeNoteEntity)) {
            LanguageEnum languageEnum = LanguageEnum.of(codeNoteEntity.getCodeType());
            if (Objects.nonNull(languageEnum)) {
                // 将其换为Language实现类的全限定名，接着用反射获取字段值
                String languageClassQualifiedName = languageEnum.getLanguageClassQualifiedName();
                Object instance = ReflectUtil.getStaticFinalFieldValue(languageClassQualifiedName, "INSTANCE");
                if (instance instanceof Language lang) {
                    language = lang;
                }
            }
        }

        codeContentTf = new CodeDetailMultiRowLanguageTextField(language, project, "", true);
        codeContentTf.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));

        MyDocumentProvider myDocumentProvider = new MyDocumentProvider(project);
        Document document = myDocumentProvider.getDocument(codeContent, language);

        codeContentTf.setDocument(document);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
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
        String codeName = StrUtil.trim(codeNameLb.getText());
        String codeContent = StrUtil.trim(codeContentTf.getText());

        if (StrUtil.isBlank(codeName)) {
            codeNameErrorPopupDecorator.setError("笔记名称不能为空");
            return false;
        }

        if (StrUtil.isBlank(codeContent)) {
            codeContentErrorPopupDecorator.setError("笔记内容不能为空");
            return false;
        }

        // null表示默认
        CodeNoteLabelEntity codeNoteLabelEntity = getSelectLabel();
        // 类型
        String codeType = LanguageEnum.Text.name();
        String codeTypeText = codeTypeLink.getText();
        LanguageEnum languageEnum = LanguageEnum.of(codeTypeText);
        if (Objects.nonNull(languageEnum)) {
            codeType = languageEnum.name();
        }

        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
        if (add) {
            if (Objects.isNull(codeNoteEntityList)) {
                codeNoteEntityList = new ArrayList<>();
                CodeNoteSetting.getInstance().codeNoteEntityList = codeNoteEntityList;
            }

            // 判断是否存在同名的笔记
            CodeNoteEntity sameNameEntity = codeNoteEntityList.stream().filter(el -> Objects.equals(codeName, el.getCodeName())).findFirst().orElse(null);
            if (Objects.nonNull(sameNameEntity)) {
                int result = Messages.showOkCancelDialog(StrUtil.format("存在相同笔记名称[{}]，是否覆盖？", codeName), "重复笔记", "覆盖", "取消", Messages.getWarningIcon());
                if (Messages.OK == result) {
                    sameNameEntity.setCodeContent(codeContent);
                    return true;
                } else {
                    return false;
                }
            }

            String id = IdUtil.simpleUUID();
            // 获取现有最大的排序号
            int sortNum = 0;
            if (CollUtil.isNotEmpty(codeNoteEntityList)) {
                CodeNoteEntity entity = Collections.max(codeNoteEntityList, Comparator.comparingInt(CodeNoteEntity::getSortNum));
                sortNum = entity.getSortNum() + 1;
            }

            if (needFormat) {
                codeContent = CodeCreateUtil.removeShortestIndentation(codeContent);
            }

            codeNoteEntityList.add(new CodeNoteEntity(
                    id,
                    codeName,
                    codeContent,
                    codeNoteLabelEntity.getId(),
                    sortNum,
                    codeType
            ));

            // 刷新侧边栏
            CodeNoteWindow.refreshTable(project);
            NotificationUtil.notifyApplication("添加笔记成功!", NotificationType.INFORMATION, project);
        } else {
            if (ArrayUtil.isNotEmpty(codeNoteEntityList)) {
                for (CodeNoteEntity codeNoteEntity : codeNoteEntityList) {
                    if (Objects.equals(id, codeNoteEntity.getId())) {
                        codeNoteEntity.setCodeName(codeName)
                                .setCodeContent(codeContent)
                                .setLabelId(codeNoteLabelEntity.getId())
                                .setCodeType(codeType)
                        ;

                        NotificationUtil.notifyApplication("更新笔记成功!", NotificationType.INFORMATION, project);
                        break;
                    }
                }

                // 刷新侧边栏
                CodeNoteWindow.refreshTable(project);
            }
        }

        return true;
    }

    private CodeNoteLabelEntity getSelectLabel() {
        int selectedIndex = codeLabelCb.getSelectedIndex();
        Object selectedItem = codeLabelCb.getSelectedItem();
        CodeNoteLabelEntity defaultLabel = new CodeNoteLabelEntity(-1, "");
        if (-1 == selectedIndex || 0 == selectedIndex) {
            return defaultLabel;
        }

        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        if (labelList == null) {
            ArrayList<CodeNoteLabelEntity> list = new ArrayList<>();
            CodeNoteSetting.getInstance().labelList = list;
            list.add(new CodeNoteLabelEntity(-1, "默认"));
        }

        List<CodeNoteLabelEntity> list = CodeNoteSetting.getInstance().labelList;
        for (int i = 0; i < list.size(); i++) {
            CodeNoteLabelEntity codeNoteLabelEntity = list.get(selectedIndex);
            if (Objects.equals(selectedItem, codeNoteLabelEntity.getLabel())) {
                return codeNoteLabelEntity;
            }
        }

        return defaultLabel;
    }


    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(new SaveAction());
        actions.add(getOKAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
    }


    private class SaveAction extends DialogWrapperExitAction {
        public SaveAction() {
            super("保存", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            executeOkAction();
        }
    }


    private class CodeTypeAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            RelativePoint relativePoint = PopupUtil.calculateBelowPoint(codeTypeLink);
            // 组件
            DefaultListModel<LanguageEnum> model = new DefaultListModel<>();
            // 需要判断是否安装了这个插件才能添加到列表
            for (LanguageEnum value : LanguageEnum.values()) {
                String pluginId = value.getPluginId();
                if (Objects.nonNull(pluginId)) {
                    PluginId pluginIdObj = PluginId.getId(pluginId);
                    if (!PluginManagerCore.isPluginInstalled(pluginIdObj) || PluginManagerCore.isDisabled(pluginIdObj)) {
                        continue;
                    }
                }

                model.addElement(value);
            }

            JBList<LanguageEnum> list = new JBList<>(model);
            // 触发快速查找
            CompatibilityUtil.speedSearchInstallOn(list);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.setCellRenderer(new ColoredListCellRenderer<>() {
                @Override
                protected void customizeCellRenderer(@NotNull JList<? extends LanguageEnum> jList, LanguageEnum value, int index, boolean selected, boolean hasFocus) {
                    append(" " + value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);

                    Language language = null;
                    // 将其换为Language实现类的全限定名，接着用反射获取字段值
                    String languageClassQualifiedName = value.getLanguageClassQualifiedName();
                    Object instance = ReflectUtil.getStaticFinalFieldValue(languageClassQualifiedName, "INSTANCE");
                    if (instance instanceof Language lang) {
                        language = lang;
                    }

                    FileType languageFileType = LanguageUtil.getLanguageFileType(language);
                    if (Objects.nonNull(languageFileType)) {
                        Icon icon = languageFileType.getIcon();
                        if (Objects.nonNull(icon)) {
                            setIcon(icon);
                        }
                    }
                }
            });

            JBScrollPane scrollPane = new JBScrollPane(list);
            scrollPane.setBorder(JBUI.Borders.empty());
            scrollPane.setViewportBorder(JBUI.Borders.empty());

            // 现在 point 包含了根据条件计算出的坐标
            JBPopup popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(scrollPane, null)
                    // .setAdText("")
                    .setFocusable(true)
                    .setShowShadow(true)
                    .setShowBorder(true)
                    .createPopup();

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        LanguageEnum selectedValue = list.getSelectedValue();
                        // 改变link文本
                        codeTypeLink.setText(selectedValue.name());
                        // 改变类型 2024/5/25 优化，去除当场改文档类型的操作
                        // String contentStr = codeContentTf.getText();;
                        // MyDocumentProvider myDocumentProvider = new MyDocumentProvider(project);
                        // Document document = myDocumentProvider.getDocument(contentStr, selectedValue.getLanguage());
                        // codeContentTf.setDocument(document);

                        // 关闭窗口
                        popup.dispose();
                    }
                }
            });

            popup.show(relativePoint);
        }
    }
}
