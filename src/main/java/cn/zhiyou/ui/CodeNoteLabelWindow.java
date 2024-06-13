package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.config.CodeNoteSetting;
import cn.zhiyou.entity.CodeNoteEntity;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

/**
 * @author Memory
 * @since 2024/3/11
 */
public class CodeNoteLabelWindow extends DialogWrapper {
    private static final Logger LOG = Logger.getInstance(CodeNoteLabelWindow.class);
    private JBList<CodeNoteLabelEntity> showList;
    private DefaultListModel<CodeNoteLabelEntity> listModel;
    private final Project project;

    public CodeNoteLabelWindow(@Nullable Project project) {
        super(project, true);
        this.project = project;

        setModal(false);
        setOKButtonText("确定");
        setTitle("标签列表");
        init();
    }

    @Override
    public @Nullable JComponent createCenterPanel() {
        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        listModel = new DefaultListModel<>();
        if (labelList == null) {
            List<CodeNoteLabelEntity> list = new ArrayList<>();
            CodeNoteSetting.getInstance().labelList = list;
            list.add(new CodeNoteLabelEntity(-1, "默认"));
            labelList = list;
        }

        if (CollUtil.isNotEmpty(labelList)) {
            for (CodeNoteLabelEntity label : labelList) {
                // 不把默认的展示出来
                if (!Objects.equals(-1, label.getId())) {
                    listModel.addElement(label);
                }
            }
        }

        showList = new JBList<>(listModel);

        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(showList);
        showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        showList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CodeNoteLabelEntity selectedValue = showList.getSelectedValue();
                    if (selectedValue != null) {
                        CodeNoteLabelDetailWindow codeNoteLabelDetailWindow = new CodeNoteLabelDetailWindow(project, selectedValue.getLabel(), selectedValue.getId());
                        if (codeNoteLabelDetailWindow.showAndGet()) {
                            refreshList();
                        }
                    }
                }
            }
        });

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(showList)
                .setAddAction(actionButton -> this.addAction())
                .setEditAction(actionButton -> this.editAction())
                .setRemoveAction(actionButton -> this.removeAction())
                .addExtraAction(new ImportAction())
                .addExtraAction(new ExportAction())
                .disableUpDownActions();

        JPanel decoratorPanel = decorator.createPanel();
        decoratorPanel.setPreferredSize(new Dimension(300, 200));

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(decoratorPanel, BorderLayout.CENTER);

        return jPanel;
    }

    private void removeAction() {
        CodeNoteLabelEntity selectedValue = showList.getSelectedValue();
        int result = Messages.showOkCancelDialog(
                "是否删除标签：" + selectedValue.getLabel(),
                "删除",
                "确定",
                "取消",
                Messages.getQuestionIcon());

        if (Objects.equals(MessageConstants.OK, result)) {
            List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
            if (labelList == null) {
                ArrayList<CodeNoteLabelEntity> list = new ArrayList<>();
                CodeNoteSetting.getInstance().labelList = list;
                list.add(new CodeNoteLabelEntity(-1, "默认"));
            }

            Integer id = selectedValue.getId();
            CodeNoteSetting.getInstance().labelList.removeIf(el -> Objects.equals(id, el.getId()));
            refreshList();

            // 去除属于该标签id的笔记
            List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
            codeNoteEntityList.removeIf(el -> Objects.equals(el.getLabelId(), id));
        }
    }

    private void editAction() {
        CodeNoteLabelEntity selectedValue = showList.getSelectedValue();
        CodeNoteLabelDetailWindow codeNoteLabelDetailWindow = new CodeNoteLabelDetailWindow(project, selectedValue.getLabel(), selectedValue.getId());
        if (codeNoteLabelDetailWindow.showAndGet()) {
            refreshList();
        }
    }

    private void addAction() {
        CodeNoteLabelDetailWindow codeNoteLabelDetailWindow = new CodeNoteLabelDetailWindow(project, null, null);
        if (codeNoteLabelDetailWindow.showAndGet()) {
            refreshList();
        }
    }

    private void refreshList() {
        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        listModel.clear();
        for (CodeNoteLabelEntity label : labelList) {
            // 不把默认的展示出来
            if (!Objects.equals(-1, label.getId())) {
                listModel.addElement(label);
            }
        }

        // 刷新笔记列表
        CodeNoteWindow.refreshTable(project);
    }


    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        return actions.toArray(new Action[0]);
    }


    private static class ExportAction extends AnAction {
        public ExportAction() {
            super("导出", null, AllIcons.ToolbarDecorator.Export);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            Project project = event.getProject();
            List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
            if (CollUtil.isEmpty(labelList)) {
                NotificationUtil.notifyApplication("无数据导出！", NotificationType.WARNING, project);
                return;
            }

            FileChooserFactory chooserFactory = FileChooserFactory.getInstance();
            FileSaverDescriptor saverDescriptor = new FileSaverDescriptor("导出标签", "", "json");
            FileSaverDialog saverDialog = chooserFactory.createSaveFileDialog(saverDescriptor, project);
            VirtualFileWrapper virtualFileWrapper = saverDialog.save("exportLabel.json");
            if (Objects.nonNull(virtualFileWrapper)) {
                File file = virtualFileWrapper.getFile();
                String fileName = file.getName();
                String fileContent = "";

                if (fileName.endsWith(".json")) {
                    fileContent = JSONUtil.toJsonStr(labelList);
                    // 格式化Json
                    fileContent = CommonUtil.formatJson(fileContent);
                }

                FileUtil.writeUtf8String(fileContent, file);
                NotificationUtil.notifyApplication("", "导出标签成功！", NotificationType.INFORMATION, project);
            }
        }
    }

    private class ImportAction extends AnAction {
        public ImportAction() {
            super("导入", null, AllIcons.ToolbarDecorator.Import);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            Project project = event.getProject();
            FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, project, null);
            if (virtualFile != null) {
                String extension = virtualFile.getExtension();
                if (!Objects.equals("json", extension)) {
                    Messages.showWarningDialog("请选择Json类型文件！", "导入标签");
                    return;
                }

                try {
                    String name = virtualFile.getName();
                    byte[] bytes = virtualFile.contentsToByteArray();
                    if (ArrayUtil.isEmpty(bytes)) {
                        Messages.showWarningDialog("[" + name + "]文件无内容！", "导入标签");
                        return;
                    }

                    String importStr = StrUtil.trim(StrUtil.str(bytes, StandardCharsets.UTF_8));
                    List<CodeNoteLabelEntity> newCodeNoteLabelEntities;
                    try {
                        newCodeNoteLabelEntities = JSONUtil.toBean(importStr, new TypeReference<>() {
                        }, false);
                    } catch (Exception ex) {
                        Messages.showWarningDialog("导入失败：Json内容不合法！", "导入标签");
                        return;
                    }

                    if (CollUtil.isNotEmpty(newCodeNoteLabelEntities)) {
                        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
                        if (labelList == null) {
                            labelList = new ArrayList<>();
                            CodeNoteSetting.getInstance().labelList = labelList;
                            labelList.add(new CodeNoteLabelEntity(-1, "默认"));
                        }

                        // 同名的标签直接不管
                        for (CodeNoteLabelEntity newEntity : newCodeNoteLabelEntities) {
                            Integer id = newEntity.getId();
                            String label = StrUtil.trim(newEntity.getLabel());
                            if (StrUtil.isBlank(label)) {
                                continue;
                            }

                            // 重复名字也跳过
                            List<CodeNoteLabelEntity> sameNameList = labelList.stream().filter(el -> StrUtil.equals(el.getLabel(), label)).toList();
                            if (CollUtil.isNotEmpty(sameNameList)) {
                                continue;
                            }

                            // 计算最大id
                            int maxId = 1;
                            if (CollUtil.isNotEmpty(CodeNoteSetting.getInstance().labelList)) {
                                CodeNoteLabelEntity labelEntity = Collections.max(CodeNoteSetting.getInstance().labelList, Comparator.comparingInt(CodeNoteLabelEntity::getId));
                                maxId = labelEntity.getId() + 1;
                            }

                            // 相同id的，就不用导入的id，把他顺延下去
                            int currentId;
                            if (Objects.nonNull(id)) {
                                List<CodeNoteLabelEntity> idList = labelList.stream().filter(el -> Objects.equals(el.getId(), id)).toList();
                                currentId = (idList.isEmpty()) ? id : maxId;
                            } else {
                                currentId = maxId;
                            }

                            CodeNoteSetting.getInstance().labelList.add(new CodeNoteLabelEntity(currentId, label));
                        }

                        refreshList();
                        NotificationUtil.notifyApplication("", "导入标签成功！", NotificationType.INFORMATION, project);
                    }
                } catch (IOException ex) {
                    LOG.error(ex);
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
