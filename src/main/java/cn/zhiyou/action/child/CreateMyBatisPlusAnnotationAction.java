package cn.zhiyou.action.child;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.zhiyou.config.DataBaseSetting;
import cn.zhiyou.constant.PluginNameConstant;
import cn.zhiyou.entity.FieldMappedEntity;
import cn.zhiyou.enums.MyBatisAnnotationEnum;
import cn.zhiyou.exception.ZhiYouException;
import cn.zhiyou.notify.OpenDataBaseSettingNotificationAction;
import cn.zhiyou.notify.OpenDatabaseToolWindowNotificationAction;
import cn.zhiyou.ui.CreateMpAnnotationDialogWrapper;
import cn.zhiyou.ui.DasDataBaseChangeDialog;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.DasTable;
import com.intellij.database.util.DasUtil;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class CreateMyBatisPlusAnnotationAction extends AnAction {

    private final DataBaseSetting dataBaseSetting = DataBaseSetting.getInstance();

    public CreateMyBatisPlusAnnotationAction() {
        super("MyBatis Plus Annotation");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        createMpAnnotation(e);
    }

    private void createMpAnnotation(AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        // 当前类
        PsiClass psiClass = ActionUtil.getPsiClass(event);
        PsiField[] fields = ActionUtil.getAllFieldFilterStaticAndUnWrite(psiClass);
        // 表名注解
        PsiAnnotation tableNameAnnotation = psiClass.getAnnotation(MyBatisAnnotationEnum.MP_TABLE_NAME.getValue());
        // 表名
        String tableName = ActionUtil.getMemberValue(tableNameAnnotation, "value");
        // 是否有注解表名
        boolean hasTableName = true;

        List<FieldMappedEntity> fieldMappedEntityList = new ArrayList<>();
        // 先判断有没有Database以及有没有配置数据源，有的话叫用户选择
        if (ActionUtil.existPlugin(PluginNameConstant.DB_TABLE_CLASS_NAME)) {
            List<DasDataSource> dasDataSourceList = ActionUtil.getAllLocalDataSource(project);
            // 有Database插件无数据源
            if (CollUtil.isEmpty(dasDataSourceList)) {
                // 用插件数据源
                try {
                    if (StrUtil.isBlank(tableName)) {
                        hasTableName = false;
                        tableName = Messages.showInputDialog(project, "请输入表名", "注解生成", Messages.getInformationIcon());
                        if (StrUtil.isBlank(tableName)) {
                            return;
                        }
                    }

                    fillFieldMappedEntityListByPluginDataSource(project, tableName, true, fields, fieldMappedEntityList);
                } catch (ZhiYouException e) {
                    return;
                }

            } else {
                // 有Database插件有数据源，做选择
                DasTable matchDasTable = null;
                DasDataBaseChangeDialog dasDataBaseChangeDialog = new DasDataBaseChangeDialog(project, dasDataSourceList);
                if (dasDataBaseChangeDialog.showAndGet()) {
                    int dataSourceIndex = dasDataBaseChangeDialog.getSelectIndex();
                    DasDataSource dasDataSource = dasDataSourceList.get(dataSourceIndex);
                    JBIterable<? extends DasTable> tables = DasUtil.getTables(dasDataSource);

                    if (StrUtil.isBlank(tableName)) {
                        hasTableName = false;
                        tableName = Messages.showInputDialog(project, "请输入表名", "注解生成", Messages.getInformationIcon());
                        if (StrUtil.isBlank(tableName)) {
                            return;
                        }
                    }

                    for (DasTable dasTable : tables) {
                        String dasTableName = dasTable.getName();
                        if (!dasTable.isSystem() && StrUtil.equalsIgnoreCase(tableName, dasTableName)) {
                            matchDasTable = dasTable;
                            break;
                        }
                    }
                } else {
                    return;
                }

                // 构建数据
                if (matchDasTable == null) {
                    NotificationUtil.notifyApplication(StrUtil.format("未找到与{}匹配的数据表", tableName), NotificationType.WARNING, project);
                    return;
                } else {
                    JBIterable<? extends DasColumn> columns = DasUtil.getColumns(matchDasTable);
                    for (PsiField psiField : fields) {
                        // 去除自己项目中的类型
                        PsiType type = psiField.getType();
                        if (ActionUtil.isReferenceType(type)) {
                            continue;
                        }

                        // 列名
                        String propertyName = psiField.getName();
                        // 判断有无指定注解，有注解就跳过
                        if (psiField.hasAnnotation(MyBatisAnnotationEnum.MP_TABLE_ID.getValue())
                                || psiField.hasAnnotation(MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue())) {
                            continue;
                        }

                        DasColumn dasColumn = columns.toStream()
                                .filter(el -> CommonUtil.matchCase(el.getName(), propertyName))
                                .findFirst()
                                .orElse(null);

                        if (Objects.isNull(dasColumn)) {
                            continue;
                        }

                        fieldMappedEntityList.add(
                                new FieldMappedEntity(
                                        dasColumn.getName(),
                                        propertyName,
                                        dasColumn.getComment(),
                                        DasUtil.isPrimary(dasColumn),
                                        DasUtil.isAutoGenerated(dasColumn),
                                        psiField));
                    }
                }
            }
        } else {
            try {
                if (StrUtil.isBlank(tableName)) {
                    hasTableName = false;
                    tableName = Messages.showInputDialog(project, "请输入表名", "注解生成", Messages.getInformationIcon());
                    if (StrUtil.isBlank(tableName)) {
                        return;
                    }
                }

                fillFieldMappedEntityListByPluginDataSource(project, tableName, false, fields, fieldMappedEntityList);
            } catch (ZhiYouException e) {
                return;
            }
        }

        new CreateMpAnnotationDialogWrapper(
                project,
                psiClass,
                "生成MyBatis Plus注解",
                hasTableName ? null : tableName,
                fieldMappedEntityList)
                .show();
    }

    private void fillFieldMappedEntityListByPluginDataSource(Project project, String tableName, boolean hasDatabase,
                                                             PsiField[] fields, List<FieldMappedEntity> fieldMappedEntityList) {
        // 插件数据源
        String host = dataBaseSetting.host;
        String port = dataBaseSetting.port;
        String user = dataBaseSetting.user;
        String pass = dataBaseSetting.pass;
        String dataBase = dataBaseSetting.dataBase;
        String url = dataBaseSetting.url;
        String driver = dataBaseSetting.driver;

        if (StrUtil.isBlank(host)
                || StrUtil.isBlank(port)
                || StrUtil.isBlank(user)
                || StrUtil.isBlank(pass)
                || StrUtil.isBlank(dataBase)
                || StrUtil.isBlank(url)
                || StrUtil.isBlank(driver)) {

            NotificationUtil.notifyWithLink(
                    "数据源缺失",
                    hasDatabase ? "建议侧边栏打开Database工具配置数据源，或配置插件数据源" : "建议配置插件数据源",
                    new NotificationAction[]{new OpenDatabaseToolWindowNotificationAction(), new OpenDataBaseSettingNotificationAction()},
                    NotificationType.WARNING,
                    project);

            throw new ZhiYouException("");
        }

        try (SimpleDataSource simpleDataSource = new SimpleDataSource(url, user, pass, driver)) {
            // 表信息
            Table tableMeta = MetaUtil.getTableMeta(simpleDataSource, tableName);

            Collection<Column> columns = tableMeta.getColumns();

            for (PsiField psiField : fields) {
                // 去除自己项目中的类型
                PsiType type = psiField.getType();
                if (ActionUtil.isReferenceType(type)) {
                    continue;
                }

                // 列名
                String propertyName = psiField.getName();
                // 判断有无指定注解，有注解就跳过
                if (psiField.hasAnnotation(MyBatisAnnotationEnum.MP_TABLE_ID.getValue())
                        || psiField.hasAnnotation(MyBatisAnnotationEnum.MP_TABLE_FIELD.getValue())) {
                    continue;
                }

                // 找匹配的字段
                Column column = columns.stream()
                        .filter(el -> CommonUtil.matchCase(el.getName(), propertyName))
                        .findFirst()
                        .orElse(null);

                if (Objects.isNull(column)) {
                    continue;
                }

                fieldMappedEntityList.add(new FieldMappedEntity(column.getName(), propertyName, column.getComment(), column.isPk(), column.isAutoIncrement(), psiField));
            }
        } catch (DbRuntimeException e) {
            Messages.showErrorDialog("数据库连接失败", "");
            throw new ZhiYouException("");
        }
    }
}
