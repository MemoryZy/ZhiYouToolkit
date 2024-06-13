package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.action.CreateMyBatisMapperAction;
import cn.zhiyou.entity.PackageAndPath;
import cn.zhiyou.enums.CreateMapperTextFieldEnum;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.NotificationUtil;
import cn.zhiyou.utils.PopupUtil;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/2/4
 */
@SuppressWarnings("DuplicatedCode")
public class CreateMyBatisMapperOnDataBaseWindow extends DialogWrapper {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton mapperBtn;
    private TextFieldWithBrowseButton moduleBtn;
    private TextFieldWithBrowseButton classBtn;
    private TextFieldWithBrowseButton xmlBtn;
    private JBTextField tablePrefixTf;
    private JBRadioButton defaultRb;
    private JBRadioButton mpRb;
    private JBRadioButton entityOnlyRb;
    private JBCheckBox camelCb;
    private JBCheckBox snakeCaseCb;
    private JBCheckBox nameFitCb;
    private JBCheckBox lombokCb;
    private JBCheckBox mpCb;
    private JBCheckBox swaggerCb;
    private JBCheckBox dateApiCb;
    private JBCheckBox commentCb;
    private JBCheckBox addEntityCb;
    private final Project project;
    private Module selectModule;
    private static final Logger LOG = Logger.getInstance(CreateMyBatisMapperOnDataBaseWindow.class);
    private final CreateMyBatisMapperWindow wrapper;
    private final AnActionEvent event;

    private final TextFieldErrorPopupDecorator moduleErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator mapperErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator classErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator xmlErrorPopupDecorator;

    public CreateMyBatisMapperOnDataBaseWindow(@NotNull AnActionEvent event, @Nullable Project project, List<DbTable> dbTables) {
        super(project, true);
        this.project = project;

        JRootPane rootPane = getRootPane();
        moduleErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, moduleBtn.getTextField());
        mapperErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, mapperBtn.getTextField());
        classErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, classBtn.getTextField());
        xmlErrorPopupDecorator = new TextFieldErrorPopupDecorator(rootPane, xmlBtn.getTextField());
        this.event = event;

        // 转为DasTable
        List<DasTable> dasTableList = dbTables.stream().map(el -> (DasTable) el.getDelegate()).toList();
        // 转换
        JBIterable<DasTable> dasTables = JBIterable.from(dasTableList);
        // 数据源
        DbDataSource dataSource = dbTables.get(0).getDataSource();

        this.wrapper = new CreateMyBatisMapperWindow(event, project, dasTables, dataSource);

        setPropertyListener();
        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);
        // 初始化输入框
        initTfBtn();

        setOKButtonText("生成");
        setCancelButtonText("取消");

        setTitle("MyBatis生成");
        init();
    }

    private void initTfBtn() {
        // 如果只有一个module，那就默认
        List<Module> moduleList = ActionUtil.getModuleList(project);
        if (moduleList.size() == 1) {
            Module module = moduleList.get(0);
            this.selectModule = module;
            moduleBtn.setText(module.getName());
            initTfBtnText();
        }

        moduleBtn.addBrowseFolderListener(new ModuleTextBrowseFolderListener(moduleBtn));
        mapperBtn.addBrowseFolderListener(new PackageTextBrowseFolderListener(mapperBtn));
        classBtn.addBrowseFolderListener(new PackageTextBrowseFolderListener(classBtn));
        xmlBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()));
    }


    private void initTfBtnText() {
        // 查找所有xml文件
        Collection<VirtualFile> virtualFiles = ActionUtil.findFilesByExt(project, "xml", selectModule.getModuleScope());
        // 根据xml文件获取
        PackageAndPath packageAndPath = CreateMyBatisMapperAction.searchFilePath(project, virtualFiles);
        // 信息
        String mapperPackage = packageAndPath.mapperPackage();
        String entityPackage = packageAndPath.entityPackage();
        String xmlPath = packageAndPath.xmlPath();

        if (StrUtil.isNotBlank(mapperPackage)) {
            mapperBtn.setText(mapperPackage);
        }
        if (StrUtil.isNotBlank(entityPackage)) {
            classBtn.setText(entityPackage);
        }
        if (StrUtil.isNotBlank(xmlPath)) {
            xmlBtn.setText(xmlPath);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
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
    }

    private void createUIComponents() {
        moduleBtn = new TextFieldWithBrowseButton();
        mapperBtn = new TextFieldWithBrowseButton();
        classBtn = new TextFieldWithBrowseButton();
        xmlBtn = new TextFieldWithBrowseButton();
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
        String mapperPath = mapperBtn.getText();
        String classPath = classBtn.getText();
        String xmlPath = xmlBtn.getText();

        try {
            // 获取mapper接口地址，并检测是否存在
            PsiPackage mapperPackage = ActionUtil.findPackage(project, mapperPath);
            VirtualFile mapperVirtualFile = ActionUtil.getVfByPackage(mapperPackage);

            // 检测该包是否为当前module下
            if (checkPackageNotOnModule(mapperVirtualFile)) {
                // 弹出错误提示框
                mapperErrorPopupDecorator.setError(StrUtil.format("Mapper接口所在包路径在'{}'下为空或不存在", selectModule.getName()));
                return false;
            }

            // 获取实体类地址，并检测是否存在
            PsiPackage entityPackage = ActionUtil.findPackage(project, classPath);
            VirtualFile entityVirtualFile = ActionUtil.getVfByPackage(entityPackage);
            if (checkPackageNotOnModule(entityVirtualFile)) {
                // 弹出错误提示框
                classErrorPopupDecorator.setError(StrUtil.format("实体类所在包路径在'{}'下为空或不存在", selectModule.getName()));
                return false;
            }

            // 获取xml文件地址，并检测是否存在
            File xmlFilePath = null;
            if (StrUtil.isNotBlank(xmlPath)) {
                xmlFilePath = FileUtil.file(xmlPath);
            }

            if (checkFileNotOnModule(xmlFilePath)) {
                // 弹出错误提示框
                xmlErrorPopupDecorator.setError(StrUtil.format("xml文件所在路径在'{}'下为空或不存在", selectModule.getName()));
                return false;
            }

            wrapper.mapperBtn.setText(this.mapperBtn.getText());
            wrapper.classBtn.setText(this.classBtn.getText());
            wrapper.xmlBtn.setText(this.xmlBtn.getText());
            wrapper.tablePrefixTf.setText(this.tablePrefixTf.getText());

            wrapper.defaultRb.setSelected(this.defaultRb.isSelected());
            wrapper.mpRb.setSelected(this.mpRb.isSelected());
            wrapper.entityOnlyRb.setSelected(this.entityOnlyRb.isSelected());

            wrapper.camelCb.setSelected(this.camelCb.isSelected());
            wrapper.snakeCaseCb.setSelected(this.snakeCaseCb.isSelected());
            wrapper.nameFitCb.setSelected(this.nameFitCb.isSelected());

            wrapper.lombokCb.setSelected(this.lombokCb.isSelected());
            wrapper.mpCb.setSelected(this.mpCb.isSelected());
            wrapper.swaggerCb.setSelected(this.swaggerCb.isSelected());

            wrapper.dateApiCb.setSelected(this.dateApiCb.isSelected());
            wrapper.commentCb.setSelected(this.commentCb.isSelected());
            wrapper.addEntityCb.setSelected(this.addEntityCb.isSelected());

            wrapper.executeOkActionOnDataBase();
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
                //         "ricoToolBox.mybatis.error.listener.tip.id",
                //         "Failed",
                //         e.getMessage(),
                //         Icons.error,
                //         3000,
                //         commentCb,
                //         GotItTooltip.BOTTOM_MIDDLE);
            }

            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            NotificationUtil.notifyApplication("系统异常!", NotificationType.ERROR, project);
            return true;
        }

        NotificationUtil.notifyApplication("", "Mapper生成成功", NotificationType.INFORMATION, project);

        return true;
    }


    private boolean checkPackageNotOnModule(VirtualFile virtualFile) {
        if (Objects.isNull(virtualFile)) {
            return true;
        }

        String mapperCanonicalPath = virtualFile.getCanonicalPath();
        if (Objects.isNull(mapperCanonicalPath)) {
            return true;
        }

        // 现在直接不匹配全路径了（因为方法返回的是反值，是[不处于module]）
        return !mapperCanonicalPath.contains(selectModule.getName());

        // String moduleFilePath = selectModule.getModuleFilePath();
        // // 这个可能是单项目才是这样
        // int index = moduleFilePath.indexOf("/.idea");
        // String projectModuleOnly;
        // // 这边是多项目的，没有.idea
        // if (index == -1) {
        //     String moduleName = selectModule.getName();
        //     index = moduleFilePath.indexOf("/" + moduleName + ".iml");
        //     if (index == -1) {
        //         // 直接看名字
        //         return mapperCanonicalPath.contains(moduleName);
        //     }
        //
        //     projectModuleOnly = moduleFilePath.substring(0, index);
        // } else {
        //     projectModuleOnly = moduleFilePath.substring(0, index);
        // }
        //
        //
        // return !mapperCanonicalPath.startsWith(projectModuleOnly);
    }


    private boolean checkFileNotOnModule(File xmlFilePath) {
        if (Objects.isNull(xmlFilePath) || !xmlFilePath.exists()) {
            return true;
        }

        String path = xmlFilePath.getPath().replace("\\", "/");

        // 现在直接不匹配全路径了（因为方法返回的是反值，是[不处于module]）
        return !path.contains(selectModule.getName());

        // String moduleFilePath = selectModule.getModuleFilePath();
        //
        // int index = moduleFilePath.indexOf("/.idea");
        // String projectModuleOnly;
        // // 这边是多项目的，没有.idea
        // if (index == -1) {
        //     String moduleName = selectModule.getName();
        //     index = moduleFilePath.indexOf("/" + moduleName + ".iml");
        //     if (index == -1) {
        //         // 直接看名字
        //         return path.contains(moduleName);
        //     }
        //
        //     projectModuleOnly = moduleFilePath.substring(0, index);
        // } else {
        //     // 单项目
        //     projectModuleOnly = moduleFilePath.substring(0, index);
        // }
        //
        // return !path.startsWith(projectModuleOnly);
    }


    public class ModuleTextBrowseFolderListener extends TextBrowseFolderListener {

        private final TextFieldWithBrowseButton moduleBtn;

        public ModuleTextBrowseFolderListener(TextFieldWithBrowseButton moduleBtn) {
            super(FileChooserDescriptorFactory.createSingleFileDescriptor());
            this.moduleBtn = moduleBtn;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<Module> moduleList = ActionUtil.getModuleList(project);
            // 展示窗口
            ChooseModulesDialog chooseModulesDialog = new ChooseModulesDialog(project, moduleList, "选择Module", "");
            chooseModulesDialog.setSize(300, 220);
            chooseModulesDialog.setSingleSelectionMode();

            if (chooseModulesDialog.showAndGet()) {
                List<Module> chosenElements = chooseModulesDialog.getChosenElements();
                if (CollUtil.isNotEmpty(chosenElements)) {
                    Module module = chosenElements.get(0);
                    selectModule = module;

                    String name = (moduleList.size() == 1)
                            ? module.getName()
                            : project.getName() + "/" + module.getName();

                    moduleBtn.setText(name);

                    initTfBtnText();
                }
            }
        }
    }


    public class PackageTextBrowseFolderListener extends TextBrowseFolderListener {
        private final TextFieldWithBrowseButton browseButton;

        public PackageTextBrowseFolderListener(TextFieldWithBrowseButton browseButton) {
            super(FileChooserDescriptorFactory.createSingleFileDescriptor());
            this.browseButton = browseButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectModule == null) {
                moduleErrorPopupDecorator.setError("请先选择Module");
                return;
            }

            PackageChooserDialog chooser = new PackageChooserDialog("Java 包选择", selectModule);
            // 判断后面点击的回显
            String btnText = browseButton.getText();
            if (StrUtil.isNotBlank(btnText) && btnText.contains(".")) {
                chooser.selectPackage(btnText);
            }

            if (chooser.showAndGet()) {
                PsiPackage psiPackage = chooser.getSelectedPackage();
                if (Objects.nonNull(psiPackage)) {
                    String packageName = psiPackage.getQualifiedName();
                    if (StrUtil.isNotBlank(packageName)) {
                        browseButton.setText(packageName);
                    }
                }
            }
        }
    }

}
