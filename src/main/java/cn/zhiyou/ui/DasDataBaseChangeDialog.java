package cn.zhiyou.ui;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.constant.ZhiYouConstant;
import cn.zhiyou.enums.DasNodeTypeEnum;
import cn.zhiyou.ui.basic.DasMutableTreeNode;
import cn.zhiyou.utils.CommonUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import cn.zhiyou.utils.NotificationUtil;
import com.intellij.database.Dbms;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.util.DasUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/5/19
 */
public class DasDataBaseChangeDialog extends DialogWrapper {

    private JPanel rootPanel;
    private Tree tree;
    private final Project project;
    private final List<DasDataSource> dasDataSourceList;
    private DasMutableTreeNode dasMutableTreeNode;

    public DasDataBaseChangeDialog(@Nullable Project project, List<DasDataSource> dasDataSourceList) {
        super(project, true);

        this.project = project;
        this.dasDataSourceList = dasDataSourceList;

        setTitle("选择数据源");
        setOKButtonText("选定");
        setCancelButtonText("取消");
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        DasMutableTreeNode rootNode = new DasMutableTreeNode("root", DasNodeTypeEnum.ROOT);

        // 遍历数据源
        for (DasDataSource dasDataSource : dasDataSourceList) {
            Dbms dbms = dasDataSource.getDbms();
            String dbName = dbms.getName();
            Icon icon = dbms.getIcon();

            DasMutableTreeNode dataSourceNode = new DasMutableTreeNode(
                    dasDataSource.getName(), dasDataSource, icon, DasNodeTypeEnum.DATASOURCE);

            // 获取数据库
            JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dasDataSource);
            for (DasNamespace schema : schemas) {
                String schemaName = schema.getName();

                if (isDefaultSchema(dbName, schemaName)) {
                    // 内置表，跳过
                    continue;
                }

                DasMutableTreeNode schemaNode = new DasMutableTreeNode(
                        schemaName, dasDataSource, schema, Icons.schema, DasNodeTypeEnum.SCHEMA);

                dataSourceNode.add(schemaNode);
            }

            rootNode.add(dataSourceNode);
        }

        DefaultTreeModel model = new DefaultTreeModel(rootNode);

        // 初始化数据源
        tree.setModel(model);
        tree.setDragEnabled(true);
        tree.setExpandableItemsEnabled(true);
        tree.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        tree.setRootVisible(false);

        initCellRenderer();

        // 触发快速检索
        CompatibilityUtil.speedSearchInstallOn(tree);

        // 展开所有节点
        CommonUtil.expandAll(tree, new TreePath(rootNode));

        return rootPanel;
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
        TreePath selectionPath = tree.getSelectionPath();
        if (Objects.isNull(selectionPath)) {
            NotificationUtil.notifyWithLog("", "请选择一个Schema！", NotificationType.WARNING, project);
            return false;
        }

        DasMutableTreeNode treeNode = (DasMutableTreeNode) selectionPath.getLastPathComponent();
        DasNodeTypeEnum dasNodeTypeEnum = treeNode.getDasNodeTypeEnum();

        if (!Objects.equals(DasNodeTypeEnum.SCHEMA, dasNodeTypeEnum)) {
            NotificationUtil.notifyWithLog("", "请选择一个Schema！", NotificationType.WARNING, project);
            return false;
        }

        this.dasMutableTreeNode = treeNode;
        return true;
    }


    private boolean isDefaultSchema(String dbName, String schemaName) {
        if (StrUtil.equalsIgnoreCase("mysql", dbName)) {
            // 碰到 MySQL内置数据库就跳过
            List<String> list = ListUtil.list(false, ZhiYouConstant.MYSQL_DEFAULT_SCHEMAS);
            return list.contains(schemaName);
        }
        // todo 其他数据库
//        else

        return false;
    }


    private void initCellRenderer() {
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DasMutableTreeNode dasMutableTreeNode = (DasMutableTreeNode) value;
                Icon dbIcon = dasMutableTreeNode.getDbIcon();

                String text = dasMutableTreeNode.getUserObject().toString();
                SimpleTextAttributes simpleTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;

                append(text, simpleTextAttributes);
                setIcon(dbIcon);
            }
        });
    }

    public DasMutableTreeNode getDasMutableTreeNode() {
        return dasMutableTreeNode;
    }
}
