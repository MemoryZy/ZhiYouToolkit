package cn.zhiyou.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.zhiyou.config.DataBaseSetting;
import cn.zhiyou.entity.PackageAndPath;
import cn.zhiyou.notify.OpenDataBaseSettingNotificationAction;
import cn.zhiyou.notify.OpenDatabaseToolWindowNotificationAction;
import cn.zhiyou.ui.CreateMyBatisMapperWindow;
import cn.zhiyou.ui.DataBaseSelectWindow;
import cn.zhiyou.ui.basic.DasMutableTreeNode;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.database.model.*;
import com.intellij.ide.IdeView;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2023/12/22
 */
public class CreateMyBatisMapperAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        // 当前 module
        Module module = event.getData(PlatformDataKeys.MODULE);
        if (project == null || module == null) {
            return;
        }

        DataBaseSetting dataBaseSetting = DataBaseSetting.getInstance();

        DasDataSource dasDataSource = null;
        SimpleDataSource simpleDataSource = null;
        JBIterable<? extends DasTable> dasTables = null;
        // 先判断有没有Database以及有没有配置数据源，有的话叫用户选择
        if (CompatibilityUtil.existDatabasePlugin()) {
            List<DasDataSource> dasDataSourceList = ActionUtil.getAllLocalDataSource(project);
            // 有Database插件无数据源
            if (CollUtil.isEmpty(dasDataSourceList)) {
                // 用插件数据源
                if (!checkPluginDataSource()) {
                    NotificationUtil.notifyWithLink(
                            "数据源缺失",
                            "建议侧边栏打开Database工具配置数据源，或配置插件数据源",
                            new NotificationAction[]{new OpenDatabaseToolWindowNotificationAction(), new OpenDataBaseSettingNotificationAction()},
                            NotificationType.WARNING,
                            project);
                    return;
                }

                simpleDataSource = new SimpleDataSource(
                        dataBaseSetting.url,
                        dataBaseSetting.user,
                        dataBaseSetting.pass,
                        dataBaseSetting.driver);
            } else {
                // 有Database插件有数据源，做选择
                DataBaseSelectWindow dataBaseSelectWindow = new DataBaseSelectWindow(project, dasDataSourceList);

                if (dataBaseSelectWindow.showAndGet()) {
                    DasMutableTreeNode dasMutableTreeNode = dataBaseSelectWindow.getDasMutableTreeNode();
                    dasDataSource = dasMutableTreeNode.getDasDataSource();
                    // 获取选择的schema
                    DasNamespace schema = dasMutableTreeNode.getSchema();
                    // 获取schema中的表
                    JBIterable<? extends DasObject> dasChildren = schema.getDasChildren(ObjectKind.TABLE);
                    DasTable[] array = dasChildren.map(el -> (DasTable) el).toArray(new DasTable[0]);
                    dasTables = JBIterable.of(array);
                } else {
                    return;
                }
            }
        } else {
            if (!checkPluginDataSource()) {
                NotificationUtil.notifyWithLink(
                        "逆向生成",
                        "缺失数据源配置",
                        new NotificationAction[]{new OpenDataBaseSettingNotificationAction()},
                        NotificationType.WARNING,
                        project);
                return;
            }

            simpleDataSource = new SimpleDataSource(
                    dataBaseSetting.url,
                    dataBaseSetting.user,
                    dataBaseSetting.pass,
                    dataBaseSetting.driver);
        }

        // 查找所有xml文件
        Collection<VirtualFile> virtualFiles = ActionUtil.findFilesByExt(project, "xml", module.getModuleScope());
        // 根据xml文件获取
        PackageAndPath packageAndPath = searchFilePath(project, virtualFiles);
        // 弹窗
        new CreateMyBatisMapperWindow(event, project, module, packageAndPath, simpleDataSource, dasTables, dasDataSource).show();
    }


    private boolean checkPluginDataSource() {
        DataBaseSetting dataBaseSetting = DataBaseSetting.getInstance();

        String host = dataBaseSetting.host;
        String port = dataBaseSetting.port;
        String user = dataBaseSetting.user;
        String pass = dataBaseSetting.pass;
        String dataBase = dataBaseSetting.dataBase;
        String url = dataBaseSetting.url;
        String driver = dataBaseSetting.driver;

        return !StrUtil.isBlank(host)
                && !StrUtil.isBlank(port)
                && !StrUtil.isBlank(user)
                && !StrUtil.isBlank(pass)
                && !StrUtil.isBlank(dataBase)
                && !StrUtil.isBlank(url)
                && !StrUtil.isBlank(driver);
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = false;
        // 当前工程
        Project project = e.getProject();
        // 鼠标右键选择的路径
        IdeView ideView = null;
        try {
            ideView = e.getRequiredData(LangDataKeys.IDE_VIEW);
        } catch (Throwable ex) {
            // do
        }

        if (null != ideView && ideView.getDirectories().length > 0) {
            // 文件夹(包)
            PsiDirectory directory = ideView.getOrChooseDirectory();
            if (Objects.nonNull(project) && Objects.nonNull(directory)) {
                VirtualFile virtualFile = directory.getVirtualFile();
                String path = virtualFile.getPath();
                enable = path.contains("/test/java") || path.contains("/main/java") || path.contains("/resources");
            }
        }

        // 设置可见性
        e.getPresentation().setVisible(enable);
    }


    /**
     * 遍历xml文件，获取mapper接口路径、实体类路径、xml文件路径
     *
     * @param virtualFiles xml虚拟文件列表
     * @return left: xml文件路径；middle: mapper接口路径；right: 实体类路径
     */
    public static PackageAndPath searchFilePath(Project project, Collection<VirtualFile> virtualFiles) {
        // xml文件路径
        String xmlFilePath = null;
        // mapper文件路径
        String mapperFilePath = null;
        // entity文件路径
        String entityFilePath = null;

        // 遍历xml文件
        for (VirtualFile virtualFile : virtualFiles) {
            // 文件无效则跳过，可能是被删除了
            if (!virtualFile.exists()) {
                continue;
            }

            PsiFile psiFile = ActionUtil.virtualFileToPsiFile(project, virtualFile);
            if (psiFile instanceof XmlFile xmlFile) {
                XmlTag rootTag = xmlFile.getRootTag();
                // 判断是否是mybatis的xml文件
                if (Objects.nonNull(rootTag) && Objects.equals("mapper", rootTag.getName())) {
                    // 获取该mapper-xml所对应的Mapper接口类全限定名
                    String mapperNameSpace = StrUtil.trimToEmpty(rootTag.getAttributeValue("namespace"));
                    PsiClass psiClass = ActionUtil.findClass(project, mapperNameSpace);
                    // Mapper接口类真实存在
                    if (Objects.nonNull(psiClass)) {
                        // 只找一次，合格就不继续赋值
                        if (Objects.isNull(xmlFilePath)) {
                            // xml文件路径
                            xmlFilePath = CommonUtil.removeLastElement(virtualFile.getPath(), '/');
                        }
                        if (Objects.isNull(mapperFilePath)) {
                            // 获取包名
                            mapperFilePath = CommonUtil.getPackageName(mapperNameSpace);
                        }
                    }

                    // 用于匹配resultType前缀，防止一些java.lang包下的类
                    String resultTypeMatch = StrUtil.splitTrim(mapperNameSpace, ".").get(0);

                    // 寻找entity类
                    XmlTag[] resultMapTags = rootTag.findSubTags("resultMap");
                    if (ArrayUtil.isNotEmpty(resultMapTags)) {
                        for (XmlTag resultMapTag : resultMapTags) {
                            String resultMapId = StrUtil.trimToEmpty(resultMapTag.getAttributeValue("id"));
                            // 如果叫BaseResultMap，则八九不离十
                            if (Objects.equals("BaseResultMap", resultMapId)) {
                                String entityType = StrUtil.trimToEmpty(resultMapTag.getAttributeValue("type"));
                                if (StrUtil.isNotBlank(entityType)) {
                                    entityFilePath = CommonUtil.getPackageName(entityType);
                                    break;
                                }
                            }
                        }
                    } else {
                        // 找<select>标签
                        XmlTag[] selectTags = rootTag.findSubTags("select");
                        for (XmlTag selectTag : selectTags) {
                            String selectResultType = StrUtil.trimToEmpty(selectTag.getAttributeValue("resultType"));
                            // 没有resultType标签，或者resultType不是以项目包名开头的
                            if (StrUtil.isNotBlank(selectResultType) && selectResultType.startsWith(resultTypeMatch)) {
                                entityFilePath = CommonUtil.getPackageName(selectResultType);
                                break;
                            }
                        }
                    }
                }
            }

            if (StrUtil.isNotBlank(xmlFilePath) && StrUtil.isNotBlank(mapperFilePath) && StrUtil.isNotBlank(entityFilePath)) {
                break;
            }
        }

        return new PackageAndPath(mapperFilePath, entityFilePath, xmlFilePath);
    }

}
