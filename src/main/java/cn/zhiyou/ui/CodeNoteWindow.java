package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.config.CodeNoteSetting;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.entity.CodeNoteEntity;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import cn.zhiyou.ui.basic.note.CodeNotePanel;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.NotificationUtil;
import cn.zhiyou.utils.PopupUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wcp
 * @since 2024/1/15
 */
public class CodeNoteWindow {
    private DefaultTableModel model;
    private JBTable showTable;
    private CodeNotePanel rootPanel;
    private final Project project;
    private final ToolWindowEx toolWindow;
    private final Map<Integer, String> rowMap = new HashMap<>();
    private static final List<Integer> selectIdList = new ArrayList<>();

    /**
     * 初始化的时候没有过滤这些标签，为true；而如果未选中任何一个标签，那么为false
     */
    private static boolean isDefault = true;

    /**
     * 创建表头（一维数组）
     */
    private static final String[] columnNames = {"标签", "名称", "代码"};


    public CodeNoteWindow(Project project, ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        // 添加操作（setTitleActions显示右边，setTabActions显示左边）
        toolWindow.setTitleActions(List.of(new OpenLabelButtonAction()));
        // toolWindow.setTabActions(new OpenLabelButtonAction());
        // 这个是窗口那里的三个点省略号那里
        // toolWindow.setAdditionalGearActions();
    }


    public JPanel getRootPanel() {
        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
        if (Objects.isNull(codeNoteEntityList)) {
            codeNoteEntityList = new ArrayList<>();
        }

        // 二维数组，第一层是行数量、第二层是数据列数量
        List<String[]> dataList = new ArrayList<>();
        codeNoteEntityList.sort(Comparator.comparingInt(CodeNoteEntity::getSortNum));

        for (CodeNoteEntity codeNoteEntity : codeNoteEntityList) {
            Integer labelId = codeNoteEntity.getLabelId();
            CodeNoteLabelEntity codeNoteLabelEntity = CodeNoteLabelEntity.of(labelId);
            String label = codeNoteLabelEntity.getLabel();
            // 拼凑数据
            String[] dataObj = {label, codeNoteEntity.getCodeName(), codeNoteEntity.getCodeContent(), codeNoteEntity.getId()};

            // 选中为空，且是默认的，那表示还没过滤，全部展示
            if (CollUtil.isEmpty(selectIdList) && isDefault) {
                dataList.add(dataObj);
            } else if (CollUtil.isEmpty(selectIdList) && !isDefault) {
                // 选中为空，且不是默认的，那表示已全部过滤，全部不展示

            } else if (CollUtil.isNotEmpty(selectIdList)) {
                // 选中不为空，那就真的按照选中的来过滤
                if (selectIdList.contains(labelId)) {
                    dataList.add(dataObj);
                }
            }
        }

        // 填充表格填充值，rowMap
        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            String[] strings = dataList.get(i);
            data[i] = new String[]{strings[0], strings[1], strings[2]};
            rowMap.put(i, strings[3]);
        }

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 返回false来禁止整行编辑
                return false;
            }
        };

        // 初始化Model
        model.setDataVector(data, columnNames);
        // 初始化控件
        rootPanel = new CodeNotePanel(new BorderLayout(), this);
        showTable = new JBTable(model);

        TableColumnModel columnModel = showTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(130);

        // 触发快速查找
        TableSpeedSearch.installOn(showTable);

        // 增加工具栏（新增按钮、删除按钮、上移按钮、下移按钮）
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(showTable)
                // 自定义扩展按钮
                .addExtraAction(new ClearButtonAction())
                .addExtraAction(new SyncButtonAction())
                .addExtraAction(new ImportButtonAction())
                .addExtraAction(new ExportButtonAction())
                .addExtraAction(new FilterLabelAction())
                // 新增元素动作
                .setAddAction(actionButton -> this.addAction())
                .setEditAction(actionButton -> this.editAction())
                .setMoveUpAction(actionButton -> this.moveUpAction())
                .setMoveDownAction(actionButton -> this.moveDownAction())
                .setRemoveAction(actionButton -> this.removeAction());

        // 添加位置
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);

        // 设置鼠标双击事件
        initMouseListener();

        return rootPanel;
    }

    private void initMouseListener() {
        showTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = showTable.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        // 双击事件处理代码
                        // 取消当前选中
                        // showTable.removeRowSelectionInterval(row, row);
                        openWindow(row);
                    }
                }
            }
        });
    }

    private void addAction() {
        openWindow(null);
    }

    private void editAction() {
        int row = showTable.getSelectedRow();
        openWindow(row);
    }

    /**
     * 下移动作
     */
    private void moveDownAction() {
        // 当前选择的行
        int row = showTable.getSelectedRow();
        // 下一行
        int nextRow = row + 1;
        // 移动
        move(row, nextRow);
    }

    /**
     * 上移动作
     */
    private void moveUpAction() {
        // 当前选择的行
        int row = showTable.getSelectedRow();
        // 下一行
        int nextRow = row - 1;
        // 移动
        move(row, nextRow);
    }

    private void removeAction() {
        int[] selectedRows = showTable.getSelectedRows();
        if (ArrayUtil.isEmpty(selectedRows)) {
            return;
        }

        List<String> idList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (int selectedRow : selectedRows) {
            idList.add(rowMap.get(selectedRow));
            nameList.add((String) model.getValueAt(selectedRow, 1));
        }

        int result = Messages.showOkCancelDialog(
                "是否删除笔记：\n " + joinNameList(nameList),
                "删除",
                "确定",
                "取消",
                Messages.getQuestionIcon());

        if (Objects.equals(MessageConstants.OK, result)) {
            List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
            codeNoteEntityList.removeIf(el -> idList.contains(el.getId()));
            reloadTable();
            NotificationUtil.notifyApplication("", "删除成功！", NotificationType.INFORMATION, project);
        }
    }

    private void openWindow(Integer row) {
        String codeName;
        String codeContent;
        String id;
        boolean add = false;

        if (Objects.isNull(row)) {
            codeName = "";
            codeContent = "";
            id = null;
            add = true;
        } else {
            codeName = (String) model.getValueAt(row, 1);
            codeContent = (String) model.getValueAt(row, 2);
            id = rowMap.get(row);
        }

        new CodeNoteDetailDialogWrapper(project, id, codeName, codeContent, add, true).show();
    }

    private void move(int row, int nextRow) {
        // 将当前行的序号和下一行的序号调换
        String id = rowMap.get(row);
        String nextId = rowMap.get(nextRow);

        CodeNoteEntity current = null;
        CodeNoteEntity next = null;

        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
        if (ArrayUtil.isNotEmpty(codeNoteEntityList)) {
            for (CodeNoteEntity codeNoteEntity : codeNoteEntityList) {
                if (Objects.equals(id, codeNoteEntity.getId())) {
                    current = codeNoteEntity;
                } else if (Objects.equals(nextId, codeNoteEntity.getId())) {
                    next = codeNoteEntity;
                }
            }
        }

        if (Objects.nonNull(current) && Objects.nonNull(next)) {
            int currentSortNum = current.getSortNum();
            int nextSortNum = next.getSortNum();

            current.setSortNum(nextSortNum);
            next.setSortNum(currentSortNum);

            reloadTable();
            // 移动后继续选中当前行
            showTable.setRowSelectionInterval(nextRow, nextRow);
        }
    }

    public void reloadTable() {
        rowMap.clear();
        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
        if (Objects.isNull(codeNoteEntityList)) {
            codeNoteEntityList = new ArrayList<>();
            CodeNoteSetting.getInstance().codeNoteEntityList = codeNoteEntityList;
        }

        List<String[]> dataList = new ArrayList<>();
        codeNoteEntityList.sort(Comparator.comparingInt(CodeNoteEntity::getSortNum));

        for (CodeNoteEntity codeNoteEntity : codeNoteEntityList) {
            Integer labelId = codeNoteEntity.getLabelId();
            CodeNoteLabelEntity codeNoteLabelEntity = CodeNoteLabelEntity.of(labelId);
            String label = codeNoteLabelEntity.getLabel();
            // 拼凑数据
            String[] dataObj = {label, codeNoteEntity.getCodeName(), codeNoteEntity.getCodeContent(), codeNoteEntity.getId()};

            // 选中为空，且是默认的，那表示还没过滤，全部展示
            if (CollUtil.isEmpty(selectIdList) && isDefault) {
                dataList.add(dataObj);
            } else if (CollUtil.isEmpty(selectIdList) && !isDefault) {
                // 选中为空，且不是默认的，那表示已全部过滤，全部不展示

            } else if (CollUtil.isNotEmpty(selectIdList)) {
                // 选中不为空，那就真的按照选中的来过滤
                if (selectIdList.contains(labelId)) {
                    dataList.add(dataObj);
                }
            }
        }

        // 填充表格填充值，rowMap
        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            String[] strings = dataList.get(i);
            data[i] = new String[]{strings[0], strings[1], strings[2]};
            rowMap.put(i, strings[3]);
        }

        model.setDataVector(data, columnNames);

        TableColumnModel columnModel = showTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(130);
    }

    public static void refreshTable(Project project) {
        // 刷新侧边栏
        CodeNotePanel page = (CodeNotePanel) ActionUtil.getPageInToolWindow(project, "Code Note", 0);
        if (Objects.nonNull(page)) {
            CodeNoteWindow codeNoteWindow = page.getCodeNoteWindow();
            codeNoteWindow.reloadTable();
        }
    }


    private static String joinNameList(List<String> nameList) {
        StringBuilder builder = new StringBuilder();
        for (String name : nameList) {
            builder.append("    -    ").append(name).append("\n    ");
        }

        return builder.toString();
    }


    public JBTable getShowTable() {
        return showTable;
    }


    public class ClearButtonAction extends AnAction {

        public ClearButtonAction() {
            super("取消所有选中", null, Icons.invalid);
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt C"), rootPanel);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            int[] selectedRows = showTable.getSelectedRows();
            e.getPresentation().setEnabled(ArrayUtil.isNotEmpty(selectedRows));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            showTable.clearSelection();
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public class SyncButtonAction extends AnAction {

        public SyncButtonAction() {
            super("刷新", null, AllIcons.Actions.Refresh);
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt S"), rootPanel);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            // 刷新侧边栏
            refreshTable(e.getProject());
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public class ImportButtonAction extends AnAction {

        public ImportButtonAction() {
            super("导入", null, AllIcons.ToolbarDecorator.Import);
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt I"), rootPanel);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, project, null);
            if (virtualFile != null) {
                String extension = virtualFile.getExtension();
                if (!Objects.equals("json", extension)) {
                    Messages.showWarningDialog("请选择Json类型文件！", "导入");
                    return;
                }

                try {
                    String name = virtualFile.getName();
                    byte[] bytes = virtualFile.contentsToByteArray();
                    if (ArrayUtil.isEmpty(bytes)) {
                        Messages.showWarningDialog("[" + name + "]文件无内容！", "导入");
                        return;
                    }

                    String importStr = StrUtil.trim(StrUtil.str(bytes, StandardCharsets.UTF_8));
                    List<CodeNoteEntity> newCodeNoteEntities;
                    try {
                        newCodeNoteEntities = JSONUtil.toBean(importStr, new TypeReference<>() {
                        }, false);
                    } catch (Exception ex) {
                        Messages.showWarningDialog("导入失败：Json内容不合法！", "导入");
                        return;
                    }

                    if (CollUtil.isNotEmpty(newCodeNoteEntities)) {
                        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
                        if (Objects.isNull(codeNoteEntityList)) {
                            codeNoteEntityList = new ArrayList<>();
                            CodeNoteSetting.getInstance().codeNoteEntityList = codeNoteEntityList;
                        }

                        // 判断是否存在同名的笔记
                        List<CodeNoteEntity> sameNameList = codeNoteEntityList.stream().filter(el -> {
                            String codeName = el.getCodeName();
                            for (CodeNoteEntity newCodeNoteEntity : newCodeNoteEntities) {
                                if (Objects.equals(newCodeNoteEntity.getCodeName(), codeName)) {
                                    return true;
                                }
                            }

                            return false;
                        }).toList();

                        if (CollUtil.isNotEmpty(sameNameList)) {
                            List<String> nameList = sameNameList.stream().map(CodeNoteEntity::getCodeName).toList();

                            int result = Messages.showOkCancelDialog(
                                    "存在相同笔记名称： \n\t" + joinNameList(nameList),
                                    "重复笔记",
                                    "覆盖",
                                    "取消",
                                    Messages.getWarningIcon());

                            if (Messages.OK == result) {
                                for (CodeNoteEntity codeNoteEntity : sameNameList) {
                                    CodeNoteEntity entity = newCodeNoteEntities.stream()
                                            .filter(el -> Objects.equals(codeNoteEntity.getCodeName(), el.getCodeName()))
                                            .findFirst()
                                            .orElse(null);

                                    if (Objects.nonNull(entity)) {
                                        codeNoteEntity.setCodeContent(entity.getCodeContent());
                                    }
                                }

                                refreshTable(project);
                            }

                            return;
                        }

                        // 获取现有最大的排序号
                        int sortNum = 0;
                        if (CollUtil.isNotEmpty(codeNoteEntityList)) {
                            CodeNoteEntity entity = Collections.max(codeNoteEntityList, Comparator.comparingInt(CodeNoteEntity::getSortNum));
                            sortNum = entity.getSortNum() + 1;
                        }

                        if (sortNum == 0) {
                            for (int i = 0; i < newCodeNoteEntities.size(); i++) {
                                CodeNoteEntity codeNoteEntity = newCodeNoteEntities.get(i);
                                codeNoteEntityList.add(codeNoteEntity.setId(IdUtil.simpleUUID()).setSortNum(i));
                            }
                        } else {
                            for (CodeNoteEntity codeNoteEntity : newCodeNoteEntities) {
                                codeNoteEntityList.add(codeNoteEntity.setId(IdUtil.simpleUUID()).setSortNum(sortNum++));
                            }
                        }
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                // 刷新侧边栏
                refreshTable(project);

                NotificationUtil.notifyApplication("", "导入成功！", NotificationType.INFORMATION, project);
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public class ExportButtonAction extends AnAction {

        public ExportButtonAction() {
            super("导出", null, AllIcons.ToolbarDecorator.Export);
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt E"), rootPanel);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;

            int[] selectedRows = showTable.getSelectedRows();
            if (ArrayUtil.isNotEmpty(selectedRows)) {
                List<CodeNoteEntity> tmpCodeNoteEntityList = new ArrayList<>();
                for (int selectedRow : selectedRows) {
                    String id = rowMap.get(selectedRow);
                    CodeNoteEntity codeNoteEntity = codeNoteEntityList.stream().filter(el -> Objects.equals(id, el.getId())).findFirst().orElse(null);
                    tmpCodeNoteEntityList.add(codeNoteEntity);
                }

                List<String> list = tmpCodeNoteEntityList.stream().map(CodeNoteEntity::getCodeName).toList();

                int result = Messages.showYesNoCancelDialog(
                        "即将导出选中： \n    " + joinNameList(list),
                        "导出笔记",
                        "导出选中",
                        "导出全部",
                        "取消",
                        Messages.getInformationIcon());

                if (Messages.YES == result) {
                    codeNoteEntityList = tmpCodeNoteEntityList;
                } else if (Messages.CANCEL == result) {
                    return;
                }
            }

            FileChooserFactory chooserFactory = FileChooserFactory.getInstance();
            FileSaverDescriptor saverDescriptor = new FileSaverDescriptor("导出", "", "json");
            FileSaverDialog saverDialog = chooserFactory.createSaveFileDialog(saverDescriptor, project);
            VirtualFileWrapper virtualFileWrapper = saverDialog.save("export.json");
            if (Objects.nonNull(virtualFileWrapper) && CollUtil.isNotEmpty(codeNoteEntityList)) {
                File file = virtualFileWrapper.getFile();
                String fileName = file.getName();
                String fileContent = "";

                if (fileName.endsWith(".json")) {
                    List<Map<String, Object>> jsonMapList = new ArrayList<>();
                    for (CodeNoteEntity codeNoteEntity : codeNoteEntityList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("codeName", codeNoteEntity.getCodeName());
                        map.put("codeContent", codeNoteEntity.getCodeContent());
                        map.put("labelId", codeNoteEntity.getLabelId());
                        jsonMapList.add(map);
                    }

                    fileContent = JSONUtil.toJsonStr(jsonMapList);
                    // 格式化Json
                    fileContent = CommonUtil.formatJson(fileContent);
                }

                FileUtil.writeUtf8String(fileContent, file);
                NotificationUtil.notifyApplication("", "导出成功！", NotificationType.INFORMATION, project);
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public class OpenLabelButtonAction extends AnAction {

        public OpenLabelButtonAction() {
            super("编辑标签列表", null, Icons.label);
            ContentManager contentManager = toolWindow.getContentManager();
            JComponent component = contentManager.getComponent();
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt L"), component);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            new CodeNoteLabelWindow(project).show();
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    private class FilterLabelAction extends AnAction {
        public FilterLabelAction() {
            super("筛选", null, AllIcons.General.Filter);
            registerCustomShortcutSet(CustomShortcutSet.fromString("alt F"), rootPanel);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent event) {
            // 当前触发该事件的组件
            Component component = event.getInputEvent().getComponent();
            RelativePoint relativePoint = PopupUtil.calculateBelowPoint(component);

            // 前面是设定坐标，这里是写页面
            DefaultListModel<JCheckBox> listModel = new DefaultListModel<>();
            List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
            List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
            // 获取当前所有展示的笔记，存在的标签就选中，未存在的标签不选中
            Collection<String> ids = rowMap.values();
            // 获取在笔记列表页面展示的所有标签id
            Set<Integer> labelIds = codeNoteEntityList.stream().filter(el -> ids.contains(el.getId())).map(CodeNoteEntity::getLabelId).collect(Collectors.toSet());

            Map<Integer, JBCheckBox> checkBoxMap = new HashMap<>();
            for (CodeNoteLabelEntity labelEntity : labelList) {
                // 这里还需要加一个判断，如果笔记中已经没有了这个标签的笔记，置为选中；如果还有这个标签的笔记，但是笔记页面展示没有，那才是未选中
                Integer labelId = labelEntity.getId();
                boolean selected;
                // 还有该标签的笔记
                boolean hasLabelNote = codeNoteEntityList.stream().anyMatch(el -> Objects.equals(labelId, el.getLabelId()));
                if (hasLabelNote) {
                    // 还有属于该标签的笔记，直接判断页面上是否包含该标签
                    selected = labelIds.contains(labelId);
                } else {
                    // 已经没有了属于该标签的笔记，不应该置为未选中
                    selected = true;
                }

                JBCheckBox checkBox = new JBCheckBox(labelEntity.getLabel(), selected);
                listModel.addElement(checkBox);
                checkBoxMap.put(labelEntity.getId(), checkBox);
            }

            CheckBoxList<String> checkBoxList = new CheckBoxList<>(listModel);
            // 现在 point 包含了根据条件计算出的坐标
            JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(checkBoxList, null)
                    .addListener(new JBPopupListener() {
                        @Override
                        public void onClosed(@NotNull LightweightWindowEvent event) {
                            selectIdList.clear();
                            // 检测所有checkbox的状态，如果是未选中，则获取id，并将结果映射到table中
                            for (Map.Entry<Integer, JBCheckBox> entry : checkBoxMap.entrySet()) {
                                Integer key = entry.getKey();
                                JBCheckBox value = entry.getValue();

                                if (value.isSelected()) {
                                    selectIdList.add(key);
                                }
                            }

                            // 如果selectIdList中没有元素，表示都不选中，那就表示非默认状态
                            if (selectIdList.isEmpty())
                                isDefault = false;

                            // 重新加载table，并将选中的标签id保存到 static修饰的变量，重启了idea就不作数
                            reloadTable();
                        }
                    })
                    // .setAdText("")
                    .setFocusable(true)
                    .setShowShadow(true)
                    .setShowBorder(true)
                    .createPopup()
                    .show(relativePoint);
        }


    }
}
