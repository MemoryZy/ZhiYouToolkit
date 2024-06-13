package cn.zhiyou.ui;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.entity.template.ColumnEntity;
import cn.zhiyou.enums.MyBatisAnnotationEnum;
import cn.zhiyou.ui.basic.ColorTableCellRenderer;
import cn.zhiyou.utils.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Memory
 * @since 2024/1/10
 */
public class CreateMyBatisWhereWindow extends DialogWrapper {
    private JPanel rootPanel;
    private JTextField aliasTextField;
    private JTextField paramTextField;
    private JRadioButton noneRb;
    private JRadioButton snakeCaseRb;
    private JRadioButton camelRb;
    private JBTable showTable;

    private final Project project;
    private final Editor editor;
    private final Document document;
    private final boolean needCopy;
    private final AnActionEvent event;

    /**
     * 行类型详情，每行的属性类型都不一定一致(1.行号 2.类型[非全限定名])
     */
    private final List<RowType> rowTypeList = new ArrayList<>();

    /**
     * 创建表头（一维数组）
     */
    private static final String[] columnNames = {"生成", "属性名", "数据列名"};

    /**
     * 是否生成所在的列位置
     */
    public static final int IS_CREATE_COLUMN = 0;

    /**
     * 属性名名所在的列位置
     */
    public static final int PROPERTY_COLUMN = 1;

    /**
     * 数据列名所在的列位置
     */
    public static final int COLUMN_COLUMN = 2;

    public CreateMyBatisWhereWindow(AnActionEvent event,
                                    @Nullable Project project,
                                    Editor editor,
                                    @NotNull Document document,
                                    PsiField[] fields,
                                    boolean needCopy) {
        super(project, true);
        this.project = project;
        this.editor = editor;
        this.document = document;
        this.needCopy = needCopy;
        this.event = event;

        // 初始化控件
        initElement(fields);

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("生成");
        setCancelButtonText("取消");

        setTitle("生成Where条件");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    private void initElement(PsiField[] fields) {
        // 初始化表格
        initTable(fields);

        // 初始化输入过滤器，只允许输入英文
        AbstractDocument aliasDocument = (AbstractDocument) aliasTextField.getDocument();
        aliasDocument.setDocumentFilter(new EnglishInputFilter());
        AbstractDocument paramDocument = (AbstractDocument) paramTextField.getDocument();
        paramDocument.setDocumentFilter(new EnglishInputFilter());

        // 创建单选按钮组
        ButtonGroup group = new ButtonGroup();
        group.add(noneRb);
        group.add(snakeCaseRb);
        group.add(camelRb);
        // 默认选中noneRb
        group.setSelected(noneRb.getModel(), true);

        // 绑定表别名的焦点离开事件
        aliasTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // 获取焦点时，啥也不做
            }

            @Override
            public void focusLost(FocusEvent e) {
                // 丢失焦点时
                resetDataModel(StrUtil.trimToNull(aliasTextField.getText()), COLUMN_COLUMN, null);
            }
        });

        // 绑定参数名的焦点离开事件
        paramTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // 获取焦点时，啥也不做
            }

            @Override
            public void focusLost(FocusEvent e) {
                // 丢失焦点时
                resetDataModel(StrUtil.trimToNull(paramTextField.getText()), PROPERTY_COLUMN, null);
            }
        });

        // 绑定noneRb的选中事件
        noneRb.addActionListener(e -> {
            // 恢复到下划线
            resetDataModel(null, COLUMN_COLUMN, CommonUtil::toSnakeCase);
        });

        // 绑定snakeCaseRb的选中事件
        snakeCaseRb.addActionListener(e -> {
            // 替换为下划线
            resetDataModel(null, COLUMN_COLUMN, CommonUtil::toSnakeCase);
        });

        // 绑定camelRb的选中事件
        camelRb.addActionListener(e -> {
            // 替换为驼峰
            resetDataModel(null, COLUMN_COLUMN, CommonUtil::toCamel);
        });
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
        List<Map<String, Object>> columnMapList = new ArrayList<>();

        // 收集数据模型
        DefaultTableModel model = (DefaultTableModel) showTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            // 第i行第0列（是否生成）的值
            Boolean isCreate = (Boolean) model.getValueAt(i, IS_CREATE_COLUMN);
            // 第i行第1列（列名）的值
            String propertyName = StrUtil.trimToEmpty((String) model.getValueAt(i, PROPERTY_COLUMN));
            // 第i行第1列（属性名）的值
            String fieldName = StrUtil.trimToEmpty((String) model.getValueAt(i, COLUMN_COLUMN));
            // 获取行类型
            int finalI = i;
            RowType rowType = rowTypeList.stream()
                    .filter(el -> Objects.equals(finalI, el.rowNum()))
                    .findFirst()
                    .orElse(null);

            // 不生成、列名、属性名为空的，则跳过
            if (!isCreate || StrUtil.isBlank(fieldName) || StrUtil.isBlank(propertyName) || Objects.isNull(rowType)) {
                continue;
            }

            Map<String, Object> columnMap = new ColumnEntity()
                    .setPropertyName(propertyName)
                    .setColumnName(fieldName)
                    .setPropertyType(rowType.type())
                    .toMap();

            columnMapList.add(columnMap);
        }

        // 生成where代码
        String whereText = ActionUtil.createTextWithTemplate(project, "Where", MapUtil.of("COLUMNS", columnMapList));

        if (StrUtil.isBlank(whereText)) {
            Component component = event.getInputEvent().getComponent();
            RelativePoint relativePoint = PopupUtil.calculateAbovePoint(component);
            PopupUtil.showHTmlTextBalloon("生成失败...", MessageType.ERROR, relativePoint, Balloon.Position.below);

            // ActionUtil.showGotItTip(
            //         "zhiYou.mybatis.condition.listener.tip.id",
            //         "Failed",
            //         "生成失败...",
            //         Messages.getErrorIcon(),
            //         3000,
            //         showTable,
            //         GotItTooltip.BOTTOM_MIDDLE);

            // tip("生成失败", NotificationType.WARNING);
            return false;
        }

        if (needCopy) {
            // 拷贝到剪贴板
            ActionUtil.setClipboard(whereText);
            NotificationUtil.notifyApplication("已复制到剪贴板！", NotificationType.INFORMATION, project);
        } else {
            // --> 获取光标所在的当前行
            int currentLine = ActionUtil.getCurrentLine(editor, document);
            // 当前行开始结束的偏移量
            ImmutablePair<Integer, Integer> offset = ActionUtil.getCurrentLineOffset(currentLine, document);
            // 替换到原来光标所在的位置
            ActionUtil.runWriteCommandAction(project, () -> document.replaceString(offset.getLeft(), offset.getRight(), whereText));
        }

        return true;
    }


    /**
     * 初始化表格
     *
     * @param fields 字段数组
     */
    private void initTable(PsiField[] fields) {
        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(showTable);

        // 二维数组，第一层是行数量、第二层是数据列数量
        Object[][] data = new Object[fields.length][3];

        // 默认转成下划线
        for (int i = 0; i < fields.length; i++) {
            PsiField field = fields[i];
            // 获取注解值
            String keyName = this.getAnnotationKeyName(field);
            // 如果加了忽略，则忽略该属性
            if (Objects.equals(MyBatisAnnotationEnum.MYBATIS_IGNORE.getValue(), keyName)) {
                continue;
            }

            // 属性名
            String propertyName = field.getName();
            // 属性类型（非全限定名）
            String presentableText = field.getType().getPresentableText();
            // 下划线格式的列名
            String fieldName = (StrUtil.isBlank(keyName)) ? CommonUtil.toSnakeCase(propertyName) : keyName;
            // 行类型详情(1.行号 2.类型[非全限定名])
            rowTypeList.add(new RowType(i, presentableText));
            // 填充数据
            data[i] = new Object[]{Boolean.TRUE, propertyName, fieldName};
        }

        // 初始化JTable
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        // 设置数据模型
        showTable.setModel(model);

        // 设置第0列的编辑器和渲染器为布尔类型的编辑器和渲染器
        showTable.getColumnModel().getColumn(IS_CREATE_COLUMN).setCellEditor(showTable.getDefaultEditor(Boolean.class));
        showTable.getColumnModel().getColumn(IS_CREATE_COLUMN).setCellRenderer(showTable.getDefaultRenderer(Boolean.class));

        TableColumnModel columnModel = showTable.getColumnModel();
        columnModel.getColumn(IS_CREATE_COLUMN).setPreferredWidth(10);
        columnModel.getColumn(PROPERTY_COLUMN).setPreferredWidth(200);
        columnModel.getColumn(COLUMN_COLUMN).setPreferredWidth(200);

        // 设置自定义的渲染器
        showTable.setDefaultRenderer(Object.class, new ColorTableCellRenderer());
    }

    /**
     * 获取字段上的注解值
     *
     * @param field 字段
     * @return 注解值
     */
    private String getAnnotationKeyName(PsiField field) {
        // 检测是否含有 MyBatis-Plus 注解
        PsiAnnotation mpId = field.getAnnotation(MyBatisAnnotationEnum.MP_TABLE_ID.getValue());
        PsiAnnotation mpField = field.getAnnotation(MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue());
        // 检测是否含有 Jpa 注解
        PsiAnnotation jpaColumn = field.getAnnotation(MyBatisAnnotationEnum.JPA_COLUMN.getValue());

        // JPA 通过 @Transient 注解标记是否为数据库字段
        PsiAnnotation jpaTransient = field.getAnnotation(MyBatisAnnotationEnum.JPA_TRANSIENT.getValue());
        if (Objects.nonNull(jpaTransient)) {
            // 该字段需要忽略
            return MyBatisAnnotationEnum.MYBATIS_IGNORE.getValue();
        }

        String annotationValue = "";
        if (Objects.nonNull(mpId) || Objects.nonNull(mpField)) {
            // MyBatis-Plus 通过注解内的 exist 属性判断是否为数据库字段
            String exist = ActionUtil.getMemberValue(mpId, "exist");
            // 不存在
            if ("false".equals(exist)) {
                return MyBatisAnnotationEnum.MYBATIS_IGNORE.getValue();
            }

            exist = ActionUtil.getMemberValue(mpField, "exist");
            if ("false".equals(exist)) {
                return MyBatisAnnotationEnum.MYBATIS_IGNORE.getValue();
            }

            annotationValue = ActionUtil.getMemberValue(mpId, "value");
            if (StrUtil.isBlank(annotationValue)) {
                annotationValue = ActionUtil.getMemberValue(mpField, "value");
            }

        } else if (Objects.nonNull(jpaColumn)) {
            annotationValue = ActionUtil.getMemberValue(jpaColumn, "name");
        }

        return annotationValue;
    }


    /**
     * 重置数据模型
     *
     * @param text        文本
     * @param columnIndex 列索引
     */
    private void resetDataModel(String text, int columnIndex, Function<String, String> convert) {
        boolean isBlank = StrUtil.isBlank(text);
        DefaultTableModel model = (DefaultTableModel) showTable.getModel();

        // 遍历所有行，并替换所有列名，给所有列名加上表别名，并更新table展示
        for (int i = 0; i < model.getRowCount(); i++) {
            // 第i行第1列（列名）的值
            String rowValue = StrUtil.trimToEmpty((String) model.getValueAt(i, columnIndex));

            // 有转换函数的是单选按钮的操作，没有的话就是文本框的操作
            if (Objects.isNull(convert)) {
                if (isBlank) {
                    // ............. 如果是空，执行去表别名操作
                    // 判断是否有.有的话表示有表别名，需要去除
                    if (rowValue.contains(".")) {
                        // t.id -> id
                        rowValue = rowValue.substring(rowValue.indexOf(".") + 1);
                    }
                } else {
                    // ............. 如果有值，则执行添加表别名操作
                    // 判断是否已经存在表别名，且已存在的表别名与设置的别名一致，有的话直接跳过
                    if (StrUtil.isBlank(rowValue) || checkExistName(rowValue, text)) {
                        continue;
                    }

                    // 判断是否有.有的话表示有表别名，需要去除
                    if (rowValue.contains(".")) {
                        // t.id -> id
                        rowValue = rowValue.substring(rowValue.indexOf(".") + 1);
                    }

                    rowValue = text + "." + rowValue;
                }
            } else {
                if (StrUtil.isBlank(rowValue)) {
                    // 空的不处理
                    continue;
                }

                // 截取掉表别名，将完整的列名截取出来
                if (rowValue.contains(".")) {
                    // 带上小数点.
                    int pointIndex = rowValue.indexOf(".") + 1;
                    String fieldName = rowValue.substring(pointIndex);
                    String aliasNameIncludePoint = rowValue.substring(0, pointIndex);
                    // 转换列名
                    fieldName = convert.apply(fieldName);
                    // 赋值
                    rowValue = aliasNameIncludePoint + fieldName;
                } else {
                    // 转换列名
                    rowValue = convert.apply(rowValue);
                }
            }

            // 设置新值
            model.setValueAt(rowValue, i, columnIndex);
        }
    }


    /**
     * 检查列原有的名称中是否与新列名相同
     *
     * @param existName 已存在的名称
     * @param newName   新名称
     * @return 如果新名称与已存在的名称相同，则返回true，否则返回false
     */
    private boolean checkExistName(String existName, String newName) {
        if (existName.contains(".")) {
            // 存在.的话，表示有别名已经在列中，则需要判断该别名与新名称是否相同
            String name = existName.substring(0, existName.indexOf("."));
            return StrUtil.equals(name, newName);
        }

        return false;
    }


    private static class EnglishInputFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            // 只接受英文字母和数字
            if (text == null || text.matches("[0-9a-zA-Z]")) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            // 只接受英文字母和数字
            if (text == null || text.matches("[0-9a-zA-Z]")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private record RowType(int rowNum, String type) {
    }

}
