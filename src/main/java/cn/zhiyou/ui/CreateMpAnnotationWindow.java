package cn.zhiyou.ui;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.entity.FieldMappedEntity;
import cn.zhiyou.enums.JavaDocumentEnum;
import cn.zhiyou.enums.MyBatisAnnotationEnum;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author wcp
 * @since 2024/1/5
 */
public class CreateMpAnnotationWindow extends DialogWrapper {
    private JPanel rootPanel;
    private JBTable showTable;

    private final Project project;
    private final String tableName;
    private final PsiClass psiClass;

    private final List<PropertyInfo> propertyInfos = new ArrayList<>();

    /**
     * 创建表头（一维数组）
     */
    private static final String[] columnNames = {"生成", "属性名", "注解值", "注释"};

    /**
     * 是否生成所在的列位置
     */
    public static final int IS_CREATE_COLUMN = 0;

    /**
     * 属性名所在的列位置
     */
    public static final int PROPERTY_COLUMN = 1;

    /**
     * 注解列所在的列位置
     */
    public static final int ANNOTATION_COLUMN = 2;

    /**
     * 注释所在的列位置
     */
    public static final int COMMENT_COLUMN = 3;

    private boolean onlyComment = false;

    public CreateMpAnnotationWindow(@Nullable Project project, PsiClass psiClass, String title, String tableName, List<FieldMappedEntity> fieldMappedEntityList) {
        super(project, true);
        this.project = project;
        this.tableName = tableName;
        this.psiClass = psiClass;

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("生成");
        setCancelButtonText("取消");

        initTable(fieldMappedEntityList);
        setTitle(title);
        init();
    }

    private void initTable(List<FieldMappedEntity> fieldMappedEntityList) {
        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(showTable);

        // 二维数组，第一层是行数量、第二层是数据列数量
        Object[][] data = new Object[fieldMappedEntityList.size()][4];
        // 数据填充
        for (int i = 0; i < fieldMappedEntityList.size(); i++) {
            FieldMappedEntity fieldMappedEntity = fieldMappedEntityList.get(i);
            // 填充数据
            data[i] = new Object[]{
                    Boolean.TRUE,
                    fieldMappedEntity.propertyName(),
                    fieldMappedEntity.columnName(),
                    StrUtil.trimToEmpty(fieldMappedEntity.comment())};

            propertyInfos.add(new PropertyInfo(i, fieldMappedEntity.isPk(), fieldMappedEntity.isAutoIncrement(), fieldMappedEntity.psiField()));
        }


        // 设置第2个列(属性名不可编辑)
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 属于1的列返回false来禁止编辑
                return column != 1;
            }
        };

        // 初始化JTable
        model.setDataVector(data, columnNames);
        // 设置数据模型
        showTable.setModel(model);

        // 设置第0列的编辑器和渲染器为布尔类型的编辑器和渲染器
        showTable.getColumnModel().getColumn(IS_CREATE_COLUMN).setCellEditor(showTable.getDefaultEditor(Boolean.class));
        showTable.getColumnModel().getColumn(IS_CREATE_COLUMN).setCellRenderer(showTable.getDefaultRenderer(Boolean.class));

        TableColumnModel columnModel = showTable.getColumnModel();
        columnModel.getColumn(IS_CREATE_COLUMN).setPreferredWidth(10);
        columnModel.getColumn(PROPERTY_COLUMN).setPreferredWidth(125);
        columnModel.getColumn(ANNOTATION_COLUMN).setPreferredWidth(125);
        columnModel.getColumn(COMMENT_COLUMN).setPreferredWidth(125);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
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

        List<FieldMappedEntity> fieldMappedEntityList = new ArrayList<>();

        // 收集数据模型
        DefaultTableModel model = (DefaultTableModel) showTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            // 第i行第0列（是否生成）的值
            Boolean isCreate = (Boolean) model.getValueAt(i, IS_CREATE_COLUMN);
            // 第i行第1列（属性名）的值
            String propertyName = StrUtil.trimToEmpty((String) model.getValueAt(i, PROPERTY_COLUMN));
            // 第i行第2列（注解值）的值
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
                fieldMappedEntityList.add(new FieldMappedEntity(annotationName, propertyName, comment, propertyInfo.isPk, propertyInfo.isAutoIncrement, propertyInfo.psiField));
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        ActionUtil.runWriteCommandAction(project, () -> {
            // Java元素构建器
            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            // 有tableName表示要生成 @TableName注解，没有表示不用
            if (StrUtil.isNotBlank(tableName)) {
                // 导入类
                ActionUtil.importClassesInClass(project, psiClass, MyBatisAnnotationEnum.MP_TABLE_NAME.getValue());

                String annotationText = StrUtil.format("@{}(value = \"{}\")",
                        CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_NAME.getValue()), tableName);

                PsiAnnotation annotation = factory.createAnnotationFromText(annotationText, psiClass);

                PsiElement psiElement = null;
                PsiElement[] children = psiClass.getChildren();
                PsiAnnotation[] annotations = psiClass.getAnnotations();

                // 先判断有无注解，有就加在注解后，没有就加在文档后；没注解没文档，就加在firstChild前
                if (ArrayUtil.isNotEmpty(annotations)) {
                    PsiAnnotation endAnnotation = annotations[annotations.length - 1];
                    psiClass.addAfter(annotation, endAnnotation);
                } else {
                    for (PsiElement child : children) {
                        if (child instanceof PsiDocComment) {
                            psiElement = child;
                            break;
                        }
                    }

                    if (Objects.nonNull(psiElement)) {
                        psiClass.addAfter(annotation, psiElement);
                    } else {
                        psiClass.addBefore(annotation, psiClass.getFirstChild());
                    }
                }
            }

            for (FieldMappedEntity fieldMappedEntity : fieldMappedEntityList) {
                String columnName = fieldMappedEntity.columnName();
                PsiField psiField = fieldMappedEntity.psiField();
                String comment = fieldMappedEntity.comment();

                if (StrUtil.isNotBlank(columnName) && !onlyComment) {
                    String annotationText;
                    if (fieldMappedEntity.isPk()) {
                        // 判断是否为自增主键
                        annotationText = StrUtil.format("@{}(value = \"{}\"{})",
                                CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_ID.getValue()),
                                columnName,
                                fieldMappedEntity.isAutoIncrement() ? ", type = IdType.AUTO" : "");
                        // 导入类
                        ActionUtil.importClassesInClass(project, psiClass, MyBatisAnnotationEnum.MP_TABLE_ID.getValue());
                    } else {
                        annotationText = StrUtil.format("@{}(value = \"{}\")",
                                CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue()), columnName);
                        // 导入类
                        ActionUtil.importClassesInClass(project, psiClass, MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue());
                    }

                    PsiAnnotation annotation = factory.createAnnotationFromText(annotationText, psiField);

                    PsiElement psiElement = null;
                    PsiElement[] children = psiField.getChildren();
                    // 添加到文档后，没有文档，则添加到first
                    for (int i = 0; i < children.length; i++) {
                        PsiElement child = children[i];
                        if (child instanceof PsiDocComment) {
                            psiElement = children[i + 1];
                            break;
                        }
                    }

                    if (Objects.nonNull(psiElement)) {
                        psiField.addAfter(annotation, psiElement);
                        PsiElement annotationEl = null;
                        // 再添加一个换行符
                        PsiElement[] children2 = psiField.getChildren();
                        // 倒序找最后一个注解
                        for (int i = children2.length - 1; i >= 0; i--) {
                            // 你的代码逻辑
                            PsiElement element = children2[i];
                            if (element instanceof PsiAnnotation) {
                                annotationEl = element;
                                break;
                            }
                        }

                        PsiWhiteSpace psiWhiteSpace = ActionUtil.createPsiWhiteSpace(project, "\n ");
                        psiField.addAfter(psiWhiteSpace, annotationEl);

                    } else {
                        PsiElement firstChild = psiField.getFirstChild();
                        psiField.addBefore(annotation, firstChild);
                    }
                }

                // 添加注释
                if (StrUtil.isNotBlank(comment)) {
                    PsiDocComment docComment = psiField.getDocComment();
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

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ActionUtil.runWriteCommandAction(project, () -> codeStyleManager.reformat(psiClass));

        return true;
    }


    private record PropertyInfo(int rowNum, boolean isPk, boolean isAutoIncrement, PsiField psiField) {
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
