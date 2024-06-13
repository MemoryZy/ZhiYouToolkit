package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.JdbcType;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.zhiyou.entity.PackageAndPath;
import cn.zhiyou.entity.template.ColumnEntity;
import cn.zhiyou.entity.template.MapperEntity;
import cn.zhiyou.entity.template.MapperInterfaceEntity;
import cn.zhiyou.enums.*;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.utils.*;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DataType;
import com.intellij.database.util.DasUtil;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.*;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

/**
 * @author Memory
 * @since 2023/12/22
 */
@SuppressWarnings("DuplicatedCode")
public class CreateMyBatisMapperWindow extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(CreateMyBatisMapperWindow.class);

    private JPanel rootPanel;

    /**
     * mapper路径编辑器
     */
    public TextFieldWithBrowseButton mapperBtn;

    /**
     * 实体类路径编辑器
     */
    public TextFieldWithBrowseButton classBtn;

    /**
     * xml路径编辑器
     */
    public TextFieldWithBrowseButton xmlBtn;

    /**
     * 驼峰选项
     */
    public JBCheckBox camelCb;

    /**
     * 下划线选项
     */
    public JBCheckBox snakeCaseCb;

    /**
     * 字段名与列名一致选项
     */
    public JBCheckBox nameFitCb;

    /**
     * lombok注解选项
     */
    public JBCheckBox lombokCb;

    /**
     * mybatis-plus注解选项
     */
    public JBCheckBox mpCb;

    /**
     * swagger注解选项
     */
    public JBCheckBox swaggerCb;

    /**
     * 新日期api注解选项
     */
    public JBCheckBox dateApiCb;

    /**
     * 注释选项
     */
    public JBCheckBox commentCb;

    /**
     * 数据库表名列表
     */
    private JBList<String> tableJbList;
    public JBRadioButton defaultRb;
    public JBRadioButton mpRb;
    public JBRadioButton entityOnlyRb;
    public JBTextField tablePrefixTf;
    public JBCheckBox addEntityCb;
    private JScrollPane listScrollPane;

    private final Project project;
    private final Module module;

    private final SimpleDataSource simpleDataSource;
    private final JBIterable<? extends DasTable> dasTables;
    private final DasDataSource dasDataSource;
    private final AnActionEvent event;

    private final TextFieldErrorPopupDecorator mapperErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator classErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator xmlErrorPopupDecorator;

    public CreateMyBatisMapperWindow(@NotNull AnActionEvent event, @Nullable Project project, Module module,
                                     PackageAndPath packageAndPath, SimpleDataSource simpleDataSource,
                                     JBIterable<? extends DasTable> dasTables, DasDataSource dasDataSource) {

        super(project, true);
        this.project = project;
        this.module = module;
        this.simpleDataSource = simpleDataSource;
        this.dasTables = dasTables;
        this.dasDataSource = dasDataSource;
        this.event = event;

        JRootPane rootPane = getRootPane();
        mapperErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, mapperBtn.getTextField());
        classErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, classBtn.getTextField());
        xmlErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, xmlBtn.getTextField());

        // 设置多选按钮唯一
        setPropertyListener();
        // 初始化数据库表名列表
        initJbList();
        // 初始化输入框
        initBtn(packageAndPath);

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("生成");
        setCancelButtonText("取消");

        setTitle("MyBatis生成");
        init();
    }


    public CreateMyBatisMapperWindow(AnActionEvent event, @Nullable Project project, JBIterable<? extends DasTable> dasTables, DasDataSource dasDataSource) {
        super(project, true);
        this.project = project;
        this.module = null;
        this.simpleDataSource = null;
        this.dasTables = dasTables;
        this.dasDataSource = dasDataSource;
        this.event = event;

        JRootPane rootPane = getRootPane();
        mapperErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, mapperBtn.getTextField());
        classErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, classBtn.getTextField());
        xmlErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, xmlBtn.getTextField());

        // 设置多选按钮唯一
        setPropertyListener();
        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        setOKButtonText("生成");
        setCancelButtonText("取消");

        setTitle("MyBatis生成");
        init();
    }


    /**
     * 设置多选按钮唯一
     */
    private void setPropertyListener() {
        // 唯一选项
        ButtonGroup cbButtonGroup = new ButtonGroup();
        cbButtonGroup.add(camelCb);
        cbButtonGroup.add(snakeCaseCb);
        cbButtonGroup.add(nameFitCb);

        // 唯一选项
        ButtonGroup rbButtonGroup = new ButtonGroup();
        rbButtonGroup.add(defaultRb);
        rbButtonGroup.add(mpRb);
        rbButtonGroup.add(entityOnlyRb);

        mpRb.addActionListener(e -> {
            if (mpRb.isSelected()) {
                mpCb.setSelected(true);
            }
        });
    }


    /**
     * 初始化输入框
     *
     * @param packageAndPath 包和路径对象
     */
    private void initBtn(PackageAndPath packageAndPath) {
        String mapperPackage = packageAndPath.mapperPackage();
        String entityPackage = packageAndPath.entityPackage();
        String xmlPath = packageAndPath.xmlPath();

        // 包选择框
        mapperBtn.addBrowseFolderListener(new PackageTextBrowseFolderListener(mapperBtn, mapperPackage));
        classBtn.addBrowseFolderListener(new PackageTextBrowseFolderListener(classBtn, entityPackage));
        xmlBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()));

        if (StrUtil.isNotBlank(mapperPackage) && StrUtil.isBlank(mapperBtn.getText())) {
            mapperBtn.setText(mapperPackage);
        }
        if (StrUtil.isNotBlank(entityPackage) && StrUtil.isBlank(classBtn.getText())) {
            classBtn.setText(entityPackage);
        }
        if (StrUtil.isNotBlank(xmlPath) && StrUtil.isBlank(xmlBtn.getText())) {
            xmlBtn.setText(xmlPath);
        }
    }

    /**
     * 初始化数据库表名列表
     */
    private void initJbList() {
        List<String> tables;

        Map<String, String> tableCommentMap = new HashMap<>();
        if (dasTables != null) {
            tables = new ArrayList<>();
            for (DasTable dasTable : dasTables) {
                if (!dasTable.isSystem()) {
                    String tableName = dasTable.getName();
                    String comment = dasTable.getComment();
                    tables.add(tableName);
                    tableCommentMap.put(tableName, comment);
                }
            }

            // tables = dasTables.toStream().filter(el -> !el.isSystem()).map(DasTable::getName).toList();
        } else {
            try {
                tables = MetaUtil.getTables(simpleDataSource);
            } catch (DbRuntimeException e) {
                Messages.showErrorDialog("数据库连接失败，请检查连接配置!", "Error");
                LOG.error(e.getMessage(), e);
                throw e;
            }
        }

        // 注入表名列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addAll(tables);
        tableJbList.setModel(listModel);

        if (!tableCommentMap.isEmpty()) {
            tableJbList.setCellRenderer(new ColoredListCellRenderer<>() {
                @Override
                protected void customizeCellRenderer(@NotNull JList<? extends String> jList, String value, int index, boolean selected, boolean hasFocus) {
                    SimpleTextAttributes simpleTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                    SimpleTextAttributes lightAttributes = SimpleTextAttributes.merge(simpleTextAttributes, SimpleTextAttributes.GRAYED_ATTRIBUTES);

                    append(value, simpleTextAttributes);
                    String tableComment = tableCommentMap.get(value);
                    if (StrUtil.isNotBlank(tableComment)) {
                        append("     " + tableComment, lightAttributes, false);
                    }
                }
            });
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        List<String> selectedValuesList = tableJbList.getSelectedValuesList();
        if (CollUtil.isEmpty(selectedValuesList)) {
            return new ValidationInfo("请选择表！", tableJbList);
        }

        return null;
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
        List<String> selectedValuesList = tableJbList.getSelectedValuesList();
        // 获取mapper接口地址，并检测是否存在
        String mapperPath = mapperBtn.getText();
        PsiPackage mapperPackage = ActionUtil.findPackage(project, mapperPath);
        if (Objects.isNull(mapperPackage)) {
            // 弹出错误提示框
            mapperErrorPopupDecorator.setError("Mapper接口所在包路径为空或不存在");
            return false;
        }

        // 获取实体类地址，并检测是否存在
        String classPath = classBtn.getText();
        PsiPackage entityPackage = ActionUtil.findPackage(project, classPath);
        if (Objects.isNull(entityPackage)) {
            // 弹出错误提示框
            classErrorPopupDecorator.setError("实体类所在包路径为空或不存在");
            return false;
        }

        // 获取xml文件地址，并检测是否存在
        String xmlPath = xmlBtn.getText();
        File xmlFilePath = null;
        if (StrUtil.isNotBlank(xmlPath)) {
            xmlFilePath = FileUtil.file(xmlPath);
        }

        if (Objects.isNull(xmlFilePath) || !xmlFilePath.exists()) {
            // 弹出错误提示框
            xmlErrorPopupDecorator.setError("xml文件所在路径为空或不存在");
            return false;
        }

        try {
            // 开始生成文件
            for (String tableName : selectedValuesList) {
                Table tableMeta = null;
                DasTable dasTable = null;
                if (dasTables != null) {
                    dasTable = dasTables.toStream().filter(el -> Objects.equals(el.getName(), tableName)).findFirst().orElse(null);
                } else {
                    tableMeta = MetaUtil.getTableMeta(simpleDataSource, tableName);
                }

                // 转驼峰
                String className = CommonUtil.toCamel(tableName);
                // 要去除的表前缀
                String tablePrefixText = StrUtil.trimToNull(tablePrefixTf.getText());
                if (StrUtil.isNotBlank(tablePrefixText)) {
                    int length = tablePrefixText.length();
                    String prefix = className.substring(0, length);
                    // 如果表的前缀和输入的前缀符合，那就去除该前缀
                    if (StrUtil.equalsIgnoreCase(tablePrefixText, prefix)) {
                        className = className.substring(length);
                    }
                }

                // 是否加上Entity后缀
                boolean addEntity = addEntityCb.isSelected();

                className = StrUtil.upperFirst(className);
                // mapper名称
                String mapperQualifiedName = StrUtil.format("{}.{}Mapper", mapperPath, className);
                // 实体名称
                String entityQualifiedName = StrUtil.format("{}.{}{}", classPath, className, addEntity ? "Entity" : "");
                // xml文件名
                String xmlFileName = StrUtil.format("{}/{}Mapper", xmlPath, className);

                // 生成
                createMapper(tableMeta, dasTable, tableName, mapperQualifiedName, entityQualifiedName, xmlFileName, mapperPackage, entityPackage);
            }
        } catch (DbRuntimeException e) {
            LOG.error(e.getMessage(), e);
            NotificationUtil.notifyApplication("数据库连接失败，请检查连接配置!", NotificationType.ERROR, project);
            return true;
        } catch (ZhiYouException e) {
            LOG.error(e.getMessage(), e);
            CreateMapperTextFieldEnum createMapperTextFieldEnum = e.getCreateMapperTextFieldEnum();
            if (null != createMapperTextFieldEnum) {
                switch (createMapperTextFieldEnum) {
                    case mapper -> {
                        mapperErrorPopupDecorator.setError(e.getMessage());
                    }
                    case class_ -> {
                        classErrorPopupDecorator.setError(e.getMessage());
                    }
                    case xml -> {
                        xmlErrorPopupDecorator.setError(e.getMessage());
                    }
                }
            } else {
                Component component = event.getInputEvent().getComponent();
                RelativePoint relativePoint = PopupUtil.calculateAbovePoint(component);
                PopupUtil.showHTmlTextBalloon(e.getMessage(), MessageType.ERROR, relativePoint, Balloon.Position.above);

                // ActionUtil.showGotItTip(
                //         "zhiYou.mybatis.error.listener.tip.id",
                //         "Failed",
                //         e.getMessage(),
                //         Icons.error,
                //         3000,
                //         commentCb,
                //         GotItTooltip.BOTTOM_MIDDLE);
            }

            // errorPopupDecorator.setWindowError(e.getMessage());
            // tip(e.getMessage(), NotificationType.WARNING, true);
            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            NotificationUtil.notifyApplication("系统异常!", NotificationType.ERROR, project);
            return true;
        }

        NotificationUtil.notifyApplication("", "Mapper生成成功", NotificationType.INFORMATION, project);

        return true;
    }


    public void executeOkActionOnDataBase() {
        // 获取mapper接口地址，并检测是否存在
        String mapperPath = mapperBtn.getText();
        PsiPackage mapperPackage = ActionUtil.findPackage(project, mapperPath);
        // 获取实体类地址，并检测是否存在
        String classPath = classBtn.getText();
        PsiPackage entityPackage = ActionUtil.findPackage(project, classPath);
        // 获取xml文件地址，并检测是否存在
        String xmlPath = xmlBtn.getText();

        // 开始生成文件
        for (DasTable dasTable : dasTables) {
            String tableName = dasTable.getName();
            // 转驼峰
            String className = CommonUtil.toCamel(tableName);
            // 要去除的表前缀
            String tablePrefixText = StrUtil.trimToNull(tablePrefixTf.getText());
            if (StrUtil.isNotBlank(tablePrefixText)) {
                int length = tablePrefixText.length();
                String prefix = className.substring(0, length);
                // 如果表的前缀和输入的前缀符合，那就去除该前缀
                if (StrUtil.equalsIgnoreCase(tablePrefixText, prefix)) {
                    className = className.substring(length);
                }
            }

            // 是否加上Entity后缀
            boolean addEntity = addEntityCb.isSelected();

            className = StrUtil.upperFirst(className);
            // mapper名称
            String mapperQualifiedName = StrUtil.format("{}.{}Mapper", mapperPath, className);
            // 实体名称
            String entityQualifiedName = StrUtil.format("{}.{}{}", classPath, className, addEntity ? "Entity" : "");
            // xml文件名
            String xmlFileName = StrUtil.format("{}/{}Mapper", xmlPath, className);

            // 生成
            createMapper(null, dasTable, tableName, mapperQualifiedName, entityQualifiedName, xmlFileName, mapperPackage, entityPackage);
        }
    }


    private String getJavaFilePath(PsiPackage psiPackage, String qualifiedName) {
        PsiDirectory directory = psiPackage.getDirectories()[0];
        VirtualFile virtualFile = directory.getVirtualFile();
        String virtualFilePath = virtualFile.getPath();
        return virtualFilePath + "/" + CommonUtil.qualifiedNameToClassName(qualifiedName) + ".java";
    }

    private void clearFiles(List<String> filePathList) {
        ActionUtil.runWriteCommandAction(project, () -> {
            for (String filePath : filePathList) {
                File file = FileUtil.file(filePath);
                if (file.exists()) {
                    FileUtil.del(file);
                }
            }

            ActionUtil.refreshFileSystem();
        });
    }


    /**
     * 生成Mapper等文件
     *
     * @param tableMeta           数据库表信息
     * @param dasTable            数据表
     * @param mapperQualifiedName mapper接口全限定名
     * @param entityQualifiedName 实体类全限定名
     * @param xmlFileName         xml文件全限定名
     * @param mapperPackage       mapper包对象
     * @param entityPackage       entity包对象
     */
    private void createMapper(Table tableMeta, DasTable dasTable, String tableName, String mapperQualifiedName,
                              String entityQualifiedName, String xmlFileName, PsiPackage mapperPackage, PsiPackage entityPackage) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "File generation") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(false);
                progressIndicator.setText("File generation in progress...");
                progressIndicator.setFraction(0);

                // 是否存在主键
                boolean hasPk;
                // 是否为唯一主键
                boolean singlePk;
                // 是否自增主键
                boolean autoIncrement;
                // 表注释
                String tableComment;
                // 列信息
                List<ColumnEntity> columnEntityList = new ArrayList<>();
                // 主键列表信息
                List<ColumnEntity> primaryKeyEntityList = new ArrayList<>();

                if (dasTable != null) {
                    Set<DasColumn> pkSet = new HashSet<>();
                    // 遍历列
                    JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dasTable);
                    // 表注释
                    tableComment = dasTable.getComment();
                    // 是否自增主键
                    autoIncrement = fillColumnList(columns, pkSet, primaryKeyEntityList, columnEntityList);
                    // 是否存在主键
                    hasPk = CollUtil.isNotEmpty(pkSet);
                    // 是否为唯一主键
                    singlePk = hasPk && pkSet.size() == 1;
                } else {
                    // 主键列表
                    Set<String> pkNames = tableMeta.getPkNames();
                    // 所有列
                    Collection<Column> columns = tableMeta.getColumns();
                    // 表注释
                    tableComment = tableMeta.getComment();
                    // 是否存在主键
                    hasPk = CollUtil.isNotEmpty(pkNames);
                    // 是否为唯一主键
                    singlePk = hasPk && pkNames.size() == 1;
                    // 是否自增主键
                    autoIncrement = fillColumnList(columns, pkNames, primaryKeyEntityList, columnEntityList);
                }

                if (defaultRb.isSelected()) {
                    // 默认
                    createEntityTemplate(
                            entityPackage,
                            columnEntityList,
                            primaryKeyEntityList,
                            entityQualifiedName,
                            tableName,
                            tableComment,
                            autoIncrement);

                    createDefaultTemplate(
                            mapperPackage,
                            mapperQualifiedName,
                            entityQualifiedName,
                            xmlFileName,
                            tableName,
                            hasPk,
                            singlePk,
                            autoIncrement,
                            columnEntityList,
                            primaryKeyEntityList);

                } else if (mpRb.isSelected()) {
                    // mybatis-plus
                    // 生成实体
                    createEntityTemplate(entityPackage, columnEntityList, primaryKeyEntityList, entityQualifiedName, tableName, tableComment, autoIncrement);
                    // 生成mapper接口及xml
                    createMpTemplate(mapperPackage, mapperQualifiedName, entityQualifiedName, xmlFileName, columnEntityList, primaryKeyEntityList);
                } else {
                    // 只生成实体类
                    createEntityTemplate(
                            entityPackage,
                            columnEntityList,
                            primaryKeyEntityList,
                            entityQualifiedName,
                            tableName,
                            tableComment,
                            autoIncrement);
                }

                progressIndicator.setFraction(1.0);
                progressIndicator.setText("Finished");
            }
        });
    }

    /**
     * 仅创建实体类
     *
     * @param entityPackage        实体包
     * @param columnEntityList     列实体列表
     * @param primaryKeyEntityList 主键列实体列表
     * @param entityQualifiedName  实体全限定名
     * @param tableComment         表注释
     * @param autoIncrement        是否自增主键
     */
    private void createEntityTemplate(PsiPackage entityPackage,
                                      List<ColumnEntity> columnEntityList,
                                      List<ColumnEntity> primaryKeyEntityList,
                                      String entityQualifiedName,
                                      String tableName,
                                      String tableComment,
                                      boolean autoIncrement) {

        PsiClass[] classes = entityPackage.getClasses();
        String className = CommonUtil.qualifiedNameToClassName(entityQualifiedName);
        if (Arrays.stream(classes).anyMatch(el -> Objects.equals(el.getName(), className))) {
            throw new ZhiYouException(StrUtil.format("'{}'类已存在", className), CreateMapperTextFieldEnum.class_);
        }

        PsiDirectory[] directories = entityPackage.getDirectories();
        if (ArrayUtil.isEmpty(directories)) {
            throw new ZhiYouException(StrUtil.format("'{}'包对应目录不存在", entityPackage.getQualifiedName()), CreateMapperTextFieldEnum.class_);
        }

        // 执行写入
        ActionUtil.runWriteCommandAction(project, () -> {
            try {
                // 先生成Java文件
                JavaDirectoryService directoryService = JavaDirectoryService.getInstance();
                PsiClass newClass = directoryService.createClass(directories[0], className);
                // 用拷贝的对象接收
                List<ColumnEntity> columnEntities = CollUtil.list(false, columnEntityList);
                // 把主键加到前面
                columnEntities.addAll(0, primaryKeyEntityList);
                // Java元素构建器
                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

                // 判断用户注解选择
                boolean selectLomBok = lombokCb.isSelected();
                boolean selectMp = mpCb.isSelected();
                boolean selectSwagger = swaggerCb.isSelected();

                // 导入注解并添加到类
                importAnnotationInClass(selectLomBok, selectMp, selectSwagger, factory, newClass, tableName, tableComment);

                // 循环字段
                for (ColumnEntity columnEntity : columnEntities) {
                    // 类型全限定名
                    String propertyType = columnEntity.getPropertyType();
                    // 导入，java.lang的不会导入
                    ActionUtil.importClassesInClass(project, newClass, propertyType);
                    // 定义字段文本
                    String fieldText = StrUtil.format("private {} {};", CommonUtil.qualifiedNameToClassName(propertyType), columnEntity.getPropertyName());
                    // 构建字段对象
                    PsiField psiField = factory.createFieldFromText(fieldText, newClass);

                    // 导入注解并添加到字段
                    importAnnotationInField(selectMp, selectSwagger, factory, columnEntity, newClass, psiField, autoIncrement);

                    // 判断用户选择
                    if (commentCb.isSelected()) {
                        // 添加注释
                        String comment = columnEntity.getComment();
                        if (StrUtil.isBlank(comment) && columnEntity.isPk()) {
                            // 自动填充主键注释
                            comment = "主键";
                        }

                        if (StrUtil.isNotBlank(comment)) {
                            // 需要去除\r\n，因为idea解析不了，会报错
                            comment = comment.replace("\r\n", "\n");
                            PsiDocComment docComment = factory.createDocCommentFromText(
                                    StrUtil.format(JavaDocumentEnum.FIELD_DOC.getValue(), comment), psiField);

                            psiField.addBefore(docComment, psiField.getFirstChild());
                        }
                    }

                    // 创建换行
                    PsiWhiteSpace psiWhiteSpace = ActionUtil.createPsiWhiteSpace(project);

                    // 有文档注释就加在文档注释前，没有文档注释就加在private前
                    PsiDocComment psiDocComment = psiField.getDocComment();
                    if (Objects.nonNull(psiDocComment)) {
                        psiField.addBefore(psiWhiteSpace, psiDocComment);
                    } else {
                        psiField.addBefore(psiWhiteSpace, psiField.getFirstChild());
                    }

                    // 添加到Class
                    newClass.add(psiField);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createMpTemplate(PsiPackage mapperPackage, String mapperQualifiedName, String entityQualifiedName, String xmlFileName, List<ColumnEntity> columnEntityList, List<ColumnEntity> primaryKeyEntityList) {
        PsiClass[] classes = mapperPackage.getClasses();
        String className = CommonUtil.qualifiedNameToClassName(mapperQualifiedName);
        if (Arrays.stream(classes).anyMatch(el -> Objects.equals(el.getName(), className))) {
            throw new ZhiYouException(StrUtil.format("'{}'类已存在", className), CreateMapperTextFieldEnum.mapper);
        }

        PsiDirectory[] directories = mapperPackage.getDirectories();
        if (ArrayUtil.isEmpty(directories)) {
            throw new ZhiYouException(
                    StrUtil.format("'{}'包对应目录不存在", mapperPackage.getQualifiedName()), CreateMapperTextFieldEnum.mapper);
        }

        // 生成Mapper-Xml文件
        File xmlFile = FileUtil.file(xmlFileName + ".xml");
        if (xmlFile.exists()) {
            throw new ZhiYouException(StrUtil.format("'{}.xml'文件已存在", className), CreateMapperTextFieldEnum.xml);
        } else {
            try {
                xmlFile.createNewFile();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new ZhiYouException(e.getMessage(), CreateMapperTextFieldEnum.xml);
            }
        }

        // 执行写入
        ActionUtil.runWriteCommandAction(project, () -> {
            try {
                // 先生成Java文件
                JavaDirectoryService directoryService = JavaDirectoryService.getInstance();

                MapperInterfaceEntity mapperInterfaceEntity = new MapperInterfaceEntity()
                        .setMapperName(className)
                        .setEntityName(CommonUtil.qualifiedNameToClassName(entityQualifiedName))
                        .setEntityQualifiedName(entityQualifiedName);

                directoryService.createClass(
                        directories[0],
                        className,
                        "MapperInterfaceMp",
                        false,
                        mapperInterfaceEntity.toMapStr());

                // 用拷贝的对象接收
                List<ColumnEntity> primaryKeyEntities = CollUtil.list(false, primaryKeyEntityList);
                List<ColumnEntity> columnEntities = CollUtil.list(false, columnEntityList);

                MapperEntity mapperEntity = new MapperEntity()
                        .setMapperQualifiedName(mapperQualifiedName)
                        .setEntityQualifiedName(entityQualifiedName)
                        .setColumnMapList(columnEntities.stream().map(ColumnEntity::toMap).toList())
                        .setPrimaryKeyMapList(primaryKeyEntities.stream().map(ColumnEntity::toMap).toList())
                        .setResultMap(spliceResultMap(columnEntities, primaryKeyEntities))
                        .createAndSetFieldsCommaInterval();

                // 生成模板，然后写入到xml文件中
                String template = ActionUtil.createTextWithTemplate(project, "MapperMp", mapperEntity.toMap());
                // 写入文件
                FileUtil.writeString(template, xmlFile, StandardCharsets.UTF_8);
                // 刷新文件系统
                ActionUtil.refreshFileSystem();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createDefaultTemplate(PsiPackage mapperPackage, String mapperQualifiedName, String entityQualifiedName, String xmlFileName,
                                       String tableName, boolean hasPk, boolean singlePk, boolean autoIncrement,
                                       List<ColumnEntity> columnEntityList, List<ColumnEntity> primaryKeyEntityList) {

        PsiClass[] classes = mapperPackage.getClasses();
        String className = CommonUtil.qualifiedNameToClassName(mapperQualifiedName);
        if (Arrays.stream(classes).anyMatch(el -> Objects.equals(el.getName(), className))) {
            throw new ZhiYouException(StrUtil.format("'{}'类已存在", className), CreateMapperTextFieldEnum.mapper);
        }

        PsiDirectory[] directories = mapperPackage.getDirectories();
        if (ArrayUtil.isEmpty(directories)) {
            throw new ZhiYouException(
                    StrUtil.format("'{}'包对应目录不存在", mapperPackage.getQualifiedName()), CreateMapperTextFieldEnum.mapper);
        }

        // 生成Mapper-Xml文件
        File xmlFile = FileUtil.file(xmlFileName + ".xml");
        if (xmlFile.exists()) {
            throw new ZhiYouException(StrUtil.format("'{}.xml'文件已存在", className), CreateMapperTextFieldEnum.xml);
        } else {
            try {
                xmlFile.createNewFile();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new ZhiYouException(e.getMessage(), CreateMapperTextFieldEnum.xml);
            }
        }

        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        ActionUtil.runWriteCommandAction(project, () -> {
            try {
                JavaDirectoryService directoryService = JavaDirectoryService.getInstance();

                String entityName = CommonUtil.qualifiedNameToClassName(entityQualifiedName);
                MapperInterfaceEntity mapperInterfaceEntity = new MapperInterfaceEntity()
                        .setMapperName(className)
                        .setEntityName(entityName)
                        .setEntityQualifiedName(entityQualifiedName)
                        .setHasPk(hasPk ? "true" : "false")
                        .setSinglePk(singlePk ? "true" : "false")
                        .setAutoIncrementPk(autoIncrement ? "true" : "false")
                        .setPrimaryKeyParam(splicePrimaryKeyParam(singlePk, primaryKeyEntityList, entityName))
                        .setEntityParam(StrUtil.format("{} entity", entityName))
                        .setPkJavaType(splicePkJavaType(singlePk, primaryKeyEntityList));

                PsiClass interfaceClass = directoryService.createClass(
                        directories[0],
                        className,
                        "MapperInterfaceDefault",
                        false,
                        mapperInterfaceEntity.toMapStr());

                codeStyleManager.reformat(interfaceClass);

                Map<String, Object> mapperMap = new MapperEntity()
                        .setMapperQualifiedName(mapperQualifiedName)
                        .setEntityQualifiedName(entityQualifiedName)
                        .setTableName(tableName)
                        .setHasPk(hasPk)
                        .setSinglePk(singlePk)
                        .setAutoIncrementPk(autoIncrement)
                        .setColumnMapList(columnEntityList.stream().map(ColumnEntity::toMap).toList())
                        .setPrimaryKeyMapList(primaryKeyEntityList.stream().map(ColumnEntity::toMap).toList())
                        .setResultMap(spliceResultMap(columnEntityList, primaryKeyEntityList))

                        .createAndSetFieldsCommaInterval()
                        .createAndSetInsertFieldsCommaInterval()
                        .createAndSetCommonFieldsCommaInterval()
                        .createAndSetPrimaryKeyCondition()
                        .createAndSetPropertiesCommaInterval()
                        .createAndSetInsertPropertiesCommaInterval()
                        .createAndSetUpdateAttributeCorrespondence()
                        .createAndSetInsertIfField()
                        .createAndSetInsertIfProperty()
                        .createAndSetAllInsertIfField()
                        .createAndSetAllInsertIfProperty()

                        .toMap();

                String template = ActionUtil.createTextWithTemplate(project, "MapperDefault", mapperMap);
                // 写入文件
                FileUtil.writeString(template, xmlFile, StandardCharsets.UTF_8);
                // 刷新文件系统
                ActionUtil.refreshFileSystem();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    private String splicePkJavaType(boolean singlePk, List<ColumnEntity> primaryKeyEntityList) {
        if (singlePk) {
            ColumnEntity columnEntity = primaryKeyEntityList.get(0);
            return CommonUtil.qualifiedNameToClassName(columnEntity.getPropertyType());
        }

        return null;
    }

    private String splicePrimaryKeyParam(boolean singlePk, List<ColumnEntity> primaryKeyEntityList, String entityName) {
        String result;
        if (singlePk) {
            ColumnEntity columnEntity = primaryKeyEntityList.get(0);
            result = StrUtil.format("{} {}",
                    CommonUtil.qualifiedNameToClassName(columnEntity.getPropertyType()),
                    columnEntity.getPropertyName());

        } else {
            result = StrUtil.format("{} entity", entityName);
        }

        return result;
    }


    private String spliceResultMap(List<ColumnEntity> columnEntities, List<ColumnEntity> primaryKeyEntities) {
        List<ColumnEntity> allList = new ArrayList<>();
        allList.addAll(primaryKeyEntities);
        allList.addAll(columnEntities);

        // <id column="${item.COLUMN_NAME}" jdbcType="${item.JDBC_TYPE}" property="${item.PROPERTY_NAME}"/>
        // <result column="${item.COLUMN_NAME}" jdbcType="${item.JDBC_TYPE}" property="${item.PROPERTY_NAME}"/>

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < allList.size(); i++) {
            ColumnEntity columnEntity = allList.get(i);
            String propertyName = CreateMyBatisResultMapWindow.handlePropertyMapping(columnEntity.getPropertyName());

            String result;
            if (columnEntity.isPk()) {
                result = StrUtil.format("<id column=\"{}\" jdbcType=\"{}\" property=\"{}\"/>",
                        columnEntity.getColumnName(),
                        columnEntity.getJdbcType(),
                        propertyName);
            } else {
                result = StrUtil.format("<result column=\"{}\" jdbcType=\"{}\" property=\"{}\"/>",
                        columnEntity.getColumnName(),
                        columnEntity.getJdbcType(),
                        propertyName);
            }

            builder.append(result).append((i == allList.size() - 1) ? "" : "\n        ");
        }


        return builder.toString();
    }

    /**
     * 在字段中导入注解
     *
     * @param selectMp      是否选择MP注解
     * @param selectSwagger 是否选择Swagger注解
     * @param factory       PsiElementFactory实例
     * @param columnEntity  列
     * @param newClass      当前所在的class
     * @param psiField      待导入注解的PsiField实例
     * @param autoIncrement 主键是否自增
     */
    private void importAnnotationInField(boolean selectMp,
                                         boolean selectSwagger,
                                         PsiElementFactory factory,
                                         ColumnEntity columnEntity,
                                         PsiClass newClass,
                                         PsiField psiField,
                                         boolean autoIncrement) {

        String comment = columnEntity.getComment();

        if (selectMp) {
            String annotationText;
            // 是否为主键
            if (columnEntity.isPk()) {
                // 判断是否为自增主键
                annotationText = StrUtil.format("@{}(value = \"{}\"{})",
                        CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_ID.getValue()),
                        columnEntity.getColumnName(),
                        autoIncrement ? ", type = IdType.AUTO" : "");
                // 导入类
                ActionUtil.importClassesInClass(project, newClass, MyBatisAnnotationEnum.MP_TABLE_ID.getValue());

            } else {
                annotationText = StrUtil.format("@{}(value = \"{}\")",
                        CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue()), columnEntity.getColumnName());
                // 导入类
                ActionUtil.importClassesInClass(project, newClass, MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue());
            }

            PsiAnnotation annotation = factory.createAnnotationFromText(annotationText, psiField);
            PsiElement firstChild = psiField.getFirstChild();
            psiField.addBefore(annotation, firstChild);
        }

        if (selectSwagger && StrUtil.isNotBlank(comment)) {
            String annotationText = StrUtil.format("@{}(\"{}\")",
                    CommonUtil.qualifiedNameToClassName(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getValue()), comment);

            // 导入类
            ActionUtil.importClassesInClass(project, newClass, SwaggerAnnotationEnum.API_MODEL_PROPERTY.getValue());

            PsiAnnotation modelPropertyAnnotation = factory.createAnnotationFromText(annotationText, psiField);
            PsiAnnotation[] annotations = psiField.getAnnotations();
            if (ArrayUtil.isNotEmpty(annotations)) {
                PsiAnnotation annotation = annotations[annotations.length - 1];
                psiField.addAfter(modelPropertyAnnotation, annotation);
            } else {
                psiField.addBefore(modelPropertyAnnotation, psiField.getFirstChild());
            }
        }
    }


    /**
     * 导入注解到类中
     *
     * @param selectLomBok  是否选择Lombok注解
     * @param selectMp      是否选择MyBatis注解
     * @param selectSwagger 是否选择Swagger注解
     * @param factory       PsiElementFactory实例
     * @param newClass      需要添加注解的类
     * @param tableName     表名
     * @param tableComment  表注释
     */
    private void importAnnotationInClass(boolean selectLomBok, boolean selectMp, boolean selectSwagger,
                                         PsiElementFactory factory, PsiClass newClass, String tableName, String tableComment) {

        if (selectLomBok) {
            // 添加lombok注解
            PsiAnnotation dataAnnotation = factory.createAnnotationFromText(
                    "@" + CommonUtil.qualifiedNameToClassName(LombokAnnotationEnum.DATA.getValue()), null);
            PsiAnnotation accessorsAnnotation = factory.createAnnotationFromText(
                    "@" + CommonUtil.qualifiedNameToClassName(LombokAnnotationEnum.ACCESSORS.getValue()) + "(chain = true)", null);

            // 导入类
            ActionUtil.importClassesInClass(project, newClass, LombokAnnotationEnum.DATA.getValue(), LombokAnnotationEnum.ACCESSORS.getValue());

            // 获取public元素
            PsiModifierList childPsiModifierList = getPublicPsiModifierList(newClass);

            if (Objects.nonNull(childPsiModifierList)) {
                newClass.addBefore(dataAnnotation, childPsiModifierList);
                newClass.addBefore(accessorsAnnotation, childPsiModifierList);
            } else {
                newClass.addBefore(dataAnnotation, newClass.getFirstChild());
                newClass.addBefore(accessorsAnnotation, newClass.getFirstChild());
            }
        }

        if (selectMp) {
            PsiAnnotation tableNameAnnotation = factory.createAnnotationFromText(
                    "@" +
                            CommonUtil.qualifiedNameToClassName(MyBatisAnnotationEnum.MP_TABLE_NAME.getValue()) +
                            StrUtil.format("(value =\"{}\")", tableName), null);

            // 导入类
            ActionUtil.importClassesInClass(project, newClass, MyBatisAnnotationEnum.MP_TABLE_NAME.getValue());

            // 获取public元素
            PsiModifierList childPsiModifierList = getPublicPsiModifierList(newClass);

            if (Objects.nonNull(childPsiModifierList)) {
                newClass.addBefore(tableNameAnnotation, childPsiModifierList);
            } else {
                newClass.addBefore(tableNameAnnotation, newClass.getFirstChild());
            }
        }

        if (selectSwagger) {
            String apiModelValue = StrUtil.format("{}(value = \"{}\")",
                    CommonUtil.qualifiedNameToClassName(SwaggerAnnotationEnum.API_MODEL.getValue()),
                    (StrUtil.isBlank(tableComment)) ? tableName : tableComment);

            PsiAnnotation apiModelAnnotation = factory.createAnnotationFromText("@" + apiModelValue, null);

            // 导入类
            ActionUtil.importClassesInClass(project, newClass, MyBatisAnnotationEnum.MP_TABLE_NAME.getValue());

            // 获取public元素
            PsiModifierList childPsiModifierList = getPublicPsiModifierList(newClass);

            if (Objects.nonNull(childPsiModifierList)) {
                newClass.addBefore(apiModelAnnotation, childPsiModifierList);
            } else {
                newClass.addBefore(apiModelAnnotation, newClass.getFirstChild());
            }
        }
    }

    private PsiModifierList getPublicPsiModifierList(PsiClass newClass) {
        PsiModifierList childPsiModifierList = null;
        PsiElement[] children = newClass.getChildren();
        for (PsiElement child : children) {
            if (child instanceof PsiModifierList psiModifierList) {
                if (Objects.equals("public", psiModifierList.getText())) {
                    childPsiModifierList = psiModifierList;
                }
            }
        }

        return childPsiModifierList;
    }


    private boolean fillColumnList(JBIterable<? extends DasColumn> columns,
                                   Set<DasColumn> pkSet, List<ColumnEntity> primaryKeyEntityList, List<ColumnEntity> columnEntityList) {
        boolean autoIncrement = false;
        for (DasColumn dasColumn : columns) {
            String columnName = dasColumn.getName();
            // 判断用户选择
            String propertyName;

            boolean selectCamel = camelCb.isSelected();
            boolean selectSnakeCase = snakeCaseCb.isSelected();

            if (selectCamel) {
                // 驼峰
                propertyName = CommonUtil.toCamel(columnName);
            } else if (selectSnakeCase) {
                // 下划线
                propertyName = CommonUtil.toSnakeCase(columnName);
            } else {
                propertyName = columnName;
            }

            // 判断 propertyName 是否为纯大写，如果是，则转小写（前提是选择驼峰或下划线）
            if (selectCamel || selectSnakeCase) {
                if (StrUtil.isUpperCase(propertyName)) {
                    propertyName = propertyName.toLowerCase();
                }

                propertyName = StrUtil.lowerFirst(propertyName);
            }

            DataType dataType = dasColumn.getDasType().toDataType();
            int jdbcTypeInt = CodeCreateUtil.convertTypeNameToJdbcType(dataType.typeName, getDatabaseType());
            JdbcType jdbcType = JdbcType.valueOf(jdbcTypeInt);
            String javaType = CodeCreateUtil.getJavaType(jdbcType, dateApiCb.isSelected());

            if (DasUtil.isPrimary(dasColumn)) {
                ColumnEntity primaryKeyEntity = new ColumnEntity()
                        .setColumnName(columnName)
                        .setPropertyName(propertyName)
                        .setJdbcType(jdbcType.name())
                        .setPropertyType(javaType)
                        .setPk(true)
                        .setComment(dasColumn.getComment());

                // 赋过一次true值就不会再赋值，因为通常只有一个自增主键
                if (!autoIncrement) {
                    autoIncrement = DasUtil.isAutoGenerated(dasColumn);
                }

                primaryKeyEntityList.add(primaryKeyEntity);
                pkSet.add(dasColumn);
            } else {
                ColumnEntity columnEntity = new ColumnEntity()
                        .setColumnName(columnName)
                        .setPropertyName(propertyName)
                        .setJdbcType(jdbcType.name())
                        .setPropertyType(javaType)
                        .setPk(false)
                        .setComment(dasColumn.getComment());

                columnEntityList.add(columnEntity);
            }
        }

        return autoIncrement;
    }

    private String getDatabaseType() {
        String url = Objects.requireNonNull(dasDataSource.getConnectionConfig()).getUrl();
        return extractDatabaseTypeFromUrl(url);
    }

    public static String extractDatabaseTypeFromUrl(String url) {
        if (url == null) {
            return "";
        } else {
            url = url.toLowerCase();
            if (url.contains(":mysql")) {
                return "MySql";
            } else if (url.contains(":oracle")) {
                return "Oracle";
            } else if (url.contains(":postgresql")) {
                return "PostgreSQL";
            } else if (url.contains(":sqlserver")) {
                return "SqlServer";
            } else {
                return url.contains(":sqlite") ? "Sqlite" : "";
            }
        }
    }

    private boolean fillColumnList(Collection<Column> columns, Set<String> pkNames, List<ColumnEntity> primaryKeyEntityList, List<ColumnEntity> columnEntityList) {
        // 是否自增主键
        boolean autoIncrement = false;

        // 如果是自增主键，那就新增时忽略主键属性，如果不是自增主键，那就把主键都加上新增，更新也是，不带上主键
        for (Column column : columns) {
            String columnName = column.getName();
            // 判断用户选择
            String propertyName;

            boolean selectCamel = camelCb.isSelected();
            boolean selectSnakeCase = snakeCaseCb.isSelected();

            if (selectCamel) {
                // 驼峰
                propertyName = CommonUtil.toCamel(columnName);
            } else if (selectSnakeCase) {
                // 下划线
                propertyName = CommonUtil.toSnakeCase(columnName);
            } else {
                propertyName = columnName;
            }

            // 判断 propertyName 是否为纯大写，如果是，则转小写（前提是选择驼峰或下划线）
            if (selectCamel || selectSnakeCase) {
                if (StrUtil.isUpperCase(propertyName)) {
                    propertyName = propertyName.toLowerCase();
                }

                propertyName = StrUtil.lowerFirst(propertyName);
            }

            JdbcType jdbcType = column.getTypeEnum();
            String javaType = CodeCreateUtil.getJavaType(jdbcType, dateApiCb.isSelected());
            boolean isPk = pkNames.contains(columnName);

            if (isPk) {
                ColumnEntity primaryKeyEntity = new ColumnEntity()
                        .setColumnName(columnName)
                        .setPropertyName(propertyName)
                        .setJdbcType(jdbcType.name())
                        .setPropertyType(javaType)
                        .setPk(isPk)
                        .setComment(column.getComment());

                // 赋过一次true值就不会再赋值，因为通常只有一个自增主键
                if (!autoIncrement) {
                    autoIncrement = column.isAutoIncrement();
                }

                primaryKeyEntityList.add(primaryKeyEntity);
            } else {
                ColumnEntity columnEntity = new ColumnEntity()
                        .setColumnName(columnName)
                        .setPropertyName(propertyName)
                        .setJdbcType(jdbcType.name())
                        .setPropertyType(javaType)
                        .setPk(isPk)
                        .setComment(column.getComment());

                columnEntityList.add(columnEntity);
            }
        }

        return autoIncrement;
    }


    private void createUIComponents() {
        mapperBtn = new TextFieldWithBrowseButton();
        classBtn = new TextFieldWithBrowseButton();
        xmlBtn = new TextFieldWithBrowseButton();
        tableJbList = new JBList<>();

        listScrollPane = new JBScrollPane(tableJbList) {
            @Override
            public Dimension getPreferredSize() {
                Dimension preferredSize = super.getPreferredSize();
                if (!isPreferredSizeSet()) {
                    setPreferredSize(new Dimension(0, preferredSize.height));
                }
                return preferredSize;
            }
        };

        listScrollPane.setBorder(JBUI.Borders.empty());
        listScrollPane.setViewportBorder(JBUI.Borders.empty());

        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(tableJbList);
    }


    public class PackageTextBrowseFolderListener extends TextBrowseFolderListener {
        private String selectPackage;
        private final TextFieldWithBrowseButton btn;

        public PackageTextBrowseFolderListener(TextFieldWithBrowseButton browseButton, String selectPackage) {
            super(FileChooserDescriptorFactory.createSingleFileDescriptor());
            this.selectPackage = selectPackage;
            this.btn = browseButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PackageChooserDialog chooser = new PackageChooserDialog("Java 包选择", module);
            if (StrUtil.isNotBlank(selectPackage)) {
                chooser.selectPackage(selectPackage);
                selectPackage = null;
            } else {
                // 判断后面点击的回显
                String btnText = btn.getText();
                if (StrUtil.isNotBlank(btnText) && btnText.contains(".")) {
                    chooser.selectPackage(btnText);
                }
            }

            if (chooser.showAndGet()) {
                PsiPackage psiPackage = chooser.getSelectedPackage();
                if (Objects.nonNull(psiPackage)) {
                    String packageName = psiPackage.getQualifiedName();
                    if (StrUtil.isNotBlank(packageName)) {
                        btn.setText(packageName);
                    }
                }
            }
        }
    }
}
