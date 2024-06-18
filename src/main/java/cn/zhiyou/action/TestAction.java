package cn.zhiyou.action;

import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.zhiyou.config.DataBaseSetting;
import cn.zhiyou.entity.PackageAndPath;
import cn.zhiyou.notify.OpenDataBaseSettingNotificationAction;
import cn.zhiyou.ui.CreateMyBatisMapperWindow;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * @author Memory
 * @since 2024/2/4
 */
public class TestAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(TestAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        // 当前 module
        Module module = event.getData(PlatformDataKeys.MODULE);
        if (project == null || module == null) {
            return;
        }

        DataBaseSetting dataBaseSetting = DataBaseSetting.getInstance();
        if (!DataBaseSetting.checkPluginDataSource()) {
            NotificationUtil.notifyWithLink(
                    "逆向生成",
                    "缺失数据源配置",
                    new NotificationAction[]{new OpenDataBaseSettingNotificationAction()},
                    NotificationType.WARNING,
                    project);
            return;
        }

        SimpleDataSource simpleDataSource = new SimpleDataSource(
                dataBaseSetting.url,
                dataBaseSetting.user,
                dataBaseSetting.pass,
                dataBaseSetting.driver);

        try (Connection connection = simpleDataSource.getConnection();) {
            // connection.prepareStatement()
            DatabaseMetaData metaData = connection.getMetaData();

            ResultSet schemas = metaData.getSchemas();

            while (schemas.next()) {
                String schemaName = schemas.getString("TABLE_SCHEM");
                System.out.println("Schema Name: " + schemaName);
            }

        } catch (Throwable throwable) {

        }

        // 查找所有xml文件
        Collection<VirtualFile> virtualFiles = ActionUtil.findFilesByExt(project, "xml", module.getModuleScope());
        // 根据xml文件获取
        PackageAndPath packageAndPath = CreateMyBatisMapperAction.searchFilePath(project, virtualFiles);
        // 弹窗
        new CreateMyBatisMapperWindow(event, project, module, packageAndPath, simpleDataSource, null, null).show();
    }

}
