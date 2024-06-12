package cn.zhiyou.ui;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.enums.JavaDocumentEnum;
import cn.zhiyou.ui.basic.ColorTableCellRenderer;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * @author wcp
 * @since 2024/1/16
 */
public class CreateAnnotationWindow extends DialogWrapper {

    private JBTable showTable;
    private final Project project;
    private final PsiClass psiClass;
    private final String annotationQualifiedName;
    private final String memberName;
    private final List<PsiField> fields;
    private final List<PropertyInfo> propertyInfos = new ArrayList<>();

    /**
     * 创建表头（一维数组）
     */
    private static final String[] columnNames = {"生成", "属性名", "注解值(必填)", "注释"};

    /**
     * 是否生成所在的列位置
     */
    public static final int IS_CREATE_COLUMN = 0;

    /**
     * 属性名名所在的列位置
     */
    public static final int PROPERTY_COLUMN = 1;

    /**
     * 注解值所在的列位置
     */
    public static final int ANNOTATION_COLUMN = 2;

    /**
     * 注释所在的列位置
     */
    public static final int COMMENT_COLUMN = 3;

    private boolean onlyComment = false;

    public CreateAnnotationWindow(Project project, PsiClass psiClass, String title, List<PsiField> fields, String annotationQualifiedName, String memberName) {
        super(project, true);
        this.project = project;
        this.psiClass = psiClass;
        this.annotationQualifiedName = annotationQualifiedName;
        this.memberName = memberName;
        this.fields = fields;

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("生成");
        setCancelButtonText("取消");

        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // 二维数组，第一层是行数量、第二层是数据列数量
        Object[][] data = new Object[fields.size()][4];
        // 数据填充
        for (int i = 0; i < fields.size(); i++) {
            PsiField psiField = fields.get(i);
            // 获取字段上的注释
            String doc = getDocComment(psiField);

            // 填充数据
            data[i] = new Object[]{
                    Boolean.TRUE,
                    psiField.getName(),
                    "",
                    doc};

            propertyInfos.add(new PropertyInfo(i, psiField));
        }

        // 设置第2个列(属性名不可编辑)
        // 属于1的列返回false来禁止编辑
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 属于1的列返回false来禁止编辑
                return column != 1;
            }
        };

        model.setDataVector(data, columnNames);

        JPanel rootPanel = new JPanel(new BorderLayout());
        showTable = new JBTable(model);

        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(showTable);

        // 设置自定义的渲染器
        showTable.setDefaultRenderer(Object.class, new ColorTableCellRenderer());

        // 设置第0列的编辑器和渲染器为布尔类型的编辑器和渲染器
        TableColumnModel columnModel = showTable.getColumnModel();
        TableColumn isCreateColumn = columnModel.getColumn(IS_CREATE_COLUMN);
        isCreateColumn.setCellEditor(showTable.getDefaultEditor(Boolean.class));
        isCreateColumn.setCellRenderer(showTable.getDefaultRenderer(Boolean.class));

        isCreateColumn.setPreferredWidth(10);
        columnModel.getColumn(PROPERTY_COLUMN).setPreferredWidth(125);
        columnModel.getColumn(ANNOTATION_COLUMN).setPreferredWidth(125);
        columnModel.getColumn(COMMENT_COLUMN).setPreferredWidth(125);

        // 增加工具栏（新增按钮、删除按钮、上移按钮、下移按钮）
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(showTable)
                .addExtraAction(new CamelButtonAction(model))
                .addExtraAction(new SnakeCaseButtonAction(model))
                .addExtraAction(new CommentButtonAction(model));

        decorator.setPreferredSize(new Dimension(540, 370));

        // 添加位置
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return rootPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        actions.add(new MyExitAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
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
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        // 收集数据模型
        DefaultTableModel model = (DefaultTableModel) showTable.getModel();
        // Java元素构建器
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        CountDownLatch latch = new CountDownLatch(1);
        ActionUtil.runWriteCommandAction(project, () -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                // 第i行第0列（是否生成）的值
                Boolean isCreate = (Boolean) model.getValueAt(i, IS_CREATE_COLUMN);
                // 第i行第1列（注解值）的值
                String propertyName = StrUtil.trimToEmpty((String) model.getValueAt(i, PROPERTY_COLUMN));
                // 第i行第2列（属性名）的值
                String annotationName = StrUtil.trimToEmpty((String) model.getValueAt(i, ANNOTATION_COLUMN));
                // 第i行第3列（注释）的值
                String comment = StrUtil.trimToEmpty((String) model.getValueAt(i, COMMENT_COLUMN));

                // 不生成、列名、属性名为空的，则跳过
                if (!isCreate || (StrUtil.isBlank(annotationName) && StrUtil.isBlank(comment)) || StrUtil.isBlank(propertyName)) {
                    continue;
                }

                int finalI = i;
                PropertyInfo propertyInfo = propertyInfos.stream().filter(el -> el.rowNum == finalI).findFirst().orElse(null);
                if (Objects.nonNull(propertyInfo)) {
                    PsiField psiField = propertyInfo.psiField;

                    PsiDocComment docComment = psiField.getDocComment();
                    if (StrUtil.isNotBlank(annotationName) && !onlyComment) {
                        String annotationText = StrUtil.format("@{}({} = \"{}\")\n",
                                CommonUtil.qualifiedNameToClassName(annotationQualifiedName),
                                memberName,
                                annotationName);

                        // 导入类
                        ActionUtil.importClassesInClass(project, psiClass, annotationQualifiedName);
                        // 创建注解
                        PsiAnnotation annotation = factory.createAnnotationFromText(annotationText, psiField);

                        PsiAnnotation[] annotations = psiField.getAnnotations();
                        if (Objects.nonNull(docComment)) {
                            psiField.addAfter(annotation, docComment);
                        } else {
                            if (ArrayUtil.isNotEmpty(annotations)) {
                                PsiAnnotation endAnnotation = annotations[annotations.length - 1];
                                psiField.addAfter(annotation, endAnnotation);
                            } else {
                                PsiElement firstChild = psiField.getFirstChild();
                                psiField.addBefore(annotation, firstChild);
                                // 这样添加会使注解和字段在同一行，加个换行
                                PsiAnnotation nowAnnotation = getEndAnnotation(psiField);

                                PsiWhiteSpace psiWhiteSpace = ActionUtil.createPsiWhiteSpace(project, "\n ");
                                psiField.addAfter(psiWhiteSpace, nowAnnotation);
                            }
                        }
                    }

                    // 添加注释
                    if (StrUtil.isNotBlank(comment)) {
                        if (Objects.nonNull(docComment)) {
                            // 去除注释，换成新注释
                            docComment.delete();
                        }
                        // 需要去除\r\n，因为idea解析不了，会报错
                        comment = comment.replace("\r\n", "\n");
                        PsiDocComment newDocComment = factory.createDocCommentFromText(StrUtil.format(JavaDocumentEnum.FIELD_DOC.getValue(), comment), psiField);
                        psiField.addBefore(newDocComment, psiField.getFirstChild());
                    }
                }
            }

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ActionUtil.runWriteCommandAction(project, () -> {
            PsiElement reformat = codeStyleManager.reformat(psiClass);
        });

        return true;
    }


    private PsiAnnotation getEndAnnotation(PsiField psiField) {
        PsiAnnotation annotation = null;
        PsiElement[] children = psiField.getChildren();
        for (int i = children.length - 1; i >= 0; i--) {
            PsiElement child = children[i];
            if (child instanceof PsiAnnotation psiAnnotation) {
                annotation = psiAnnotation;
                break;
            }
        }

        return annotation;
    }


    private String getDocComment(PsiField psiField) {
        PsiDocComment docComment = psiField.getDocComment();
        if (Objects.isNull(docComment)) {
            return "";
        }

        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        if (ArrayUtil.isEmpty(descriptionElements)) {
            return "";
        }

        List<String> list = new ArrayList<>();
        for (PsiElement descriptionElement : descriptionElements) {
            if (descriptionElement instanceof PsiDocToken psiDocToken) {
                list.add(StrUtil.trimToEmpty(psiDocToken.getText()));
            }
        }

        return StrUtil.join("\n", list);
    }


    private static void changeAnnotationColumn(DefaultTableModel model, Function<String, String> convert) {
        // 先清除
        clearAnnotationColumn(model);

        for (int i = 0; i < model.getRowCount(); i++) {
            // 第i行第2列（注解值）的值
            String propertyName = StrUtil.trimToEmpty((String) model.getValueAt(i, PROPERTY_COLUMN));
            String annotationValue = StrUtil.trimToEmpty((String) model.getValueAt(i, ANNOTATION_COLUMN));
            // 注解值为空，则从属性名取过来并转驼峰
            annotationValue = StrUtil.isBlank(annotationValue)
                    ? StrUtil.lowerFirst(convert.apply(propertyName))
                    : StrUtil.lowerFirst(convert.apply(annotationValue));

            // 设置新值
            model.setValueAt(annotationValue, i, ANNOTATION_COLUMN);
        }
    }


    private static void clearAnnotationColumn(DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            // 设置新值
            model.setValueAt("", i, ANNOTATION_COLUMN);
        }
    }


    private record PropertyInfo(int rowNum, PsiField psiField) {
    }

    public static class CamelButtonAction extends AnAction {

        private final DefaultTableModel model;

        public CamelButtonAction(DefaultTableModel model) {
            super("驼峰(属性名框 > 注解值框)", null, Icons.transfer);
            this.model = model;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            changeAnnotationColumn(model, CommonUtil::toCamel);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public static class SnakeCaseButtonAction extends AnAction {
        private final DefaultTableModel model;

        public SnakeCaseButtonAction(DefaultTableModel model) {
            super("下划线(属性名框 > 注解值框)", null, Icons.smartRecord);
            this.model = model;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            changeAnnotationColumn(model, CommonUtil::toSnakeCase);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    public static class CommentButtonAction extends AnAction {
        private final DefaultTableModel model;

        public CommentButtonAction(DefaultTableModel model) {
            super("注释框 > 注解值框", null, Icons.penTool);
            this.model = model;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            clearAnnotationColumn(model);
            for (int i = 0; i < model.getRowCount(); i++) {
                // 第i行第2列（注释）的值
                String comment = StrUtil.trimToEmpty((String) model.getValueAt(i, COMMENT_COLUMN));
                String annotationValue = StrUtil.trimToEmpty((String) model.getValueAt(i, ANNOTATION_COLUMN));
                if (StrUtil.isBlank(annotationValue)) {
                    // 设置新值
                    model.setValueAt(comment, i, ANNOTATION_COLUMN);
                }
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }


    private class MyExitAction extends DialogWrapperExitAction {
        public MyExitAction() {
            super("生成注释", 2);
        }

        @Override
        protected void doAction(ActionEvent e) {
            onlyComment = true;
            doOKAction();
        }
    }

}
