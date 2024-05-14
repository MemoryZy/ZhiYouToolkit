package cn.zhiyou.ui;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.enums.PropertyTreeNodeValueTypeEnum;
import cn.zhiyou.ui.basic.PropertyMatchMutableTreeNode;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author wcp
 * @since 2024/2/29
 */
public class PropertyMatchWindow extends DialogWrapper {
    private JPanel rootPanel;
    private Tree tree;
    private final String mainClassName;
    private final String matchClassName;

    public PropertyMatchWindow(@Nullable Project project,
                               String mainClassName,
                               String matchClassName,
                               List<PropertyMatchMutableTreeNode> successNodeList,
                               List<PropertyMatchMutableTreeNode> failedNodeList) {
        super(project, true);
        this.mainClassName = mainClassName;
        this.matchClassName = matchClassName;

        initTree(successNodeList, failedNodeList);
        setModal(false);
        setTitle("类属性匹配");
        setOKButtonText("确定");
        init();
    }

    private void initTree(List<PropertyMatchMutableTreeNode> successNodeList, List<PropertyMatchMutableTreeNode> failedNodeList) {
        PropertyMatchMutableTreeNode rootNode = new PropertyMatchMutableTreeNode("Result", PropertyTreeNodeValueTypeEnum.root);
        PropertyMatchMutableTreeNode successNode = new PropertyMatchMutableTreeNode("匹配成功", PropertyTreeNodeValueTypeEnum.success);
        PropertyMatchMutableTreeNode failedNode = new PropertyMatchMutableTreeNode("匹配失败", PropertyTreeNodeValueTypeEnum.failed);

        successNode.setSize(successNodeList.size());
        for (PropertyMatchMutableTreeNode treeNode : successNodeList) {
            successNode.add(treeNode);
        }

        failedNode.setSize(failedNodeList.size());
        for (PropertyMatchMutableTreeNode treeNode : failedNodeList) {
            failedNode.add(treeNode);
        }

        rootNode.add(successNode);
        rootNode.add(failedNode);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        tree.setModel(model);
        tree.setDragEnabled(true);
        tree.setExpandableItemsEnabled(true);

        initCellRenderer();
        initPopupMenu();

        CompatibilityUtil.speedSearchInstallOn(tree);

        TreePath rootTreePath = new TreePath(rootNode);
        TreePath successTreePath = new TreePath(successNode);
        TreePath failedTreePath = new TreePath(failedNode);

        tree.expandPath(rootTreePath);
        tree.expandPath(successTreePath);
        tree.expandPath(failedTreePath);
    }

    private void initCellRenderer() {
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                PropertyMatchMutableTreeNode propertyMatchMutableTreeNode = (PropertyMatchMutableTreeNode) value;
                String propertyType = propertyMatchMutableTreeNode.getPropertyType();
                String matchProperty = propertyMatchMutableTreeNode.getMatchProperty();
                String matchPropertyType = propertyMatchMutableTreeNode.getMatchPropertyType();
                PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = propertyMatchMutableTreeNode.getNodeValueTypeEnum();
                Integer size = propertyMatchMutableTreeNode.getSize();

                // String text = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
                String text = propertyMatchMutableTreeNode.getUserObject().toString();
                SimpleTextAttributes simpleTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                String mainType = "";
                String matchType = "";
                String fragment = "";
                String sizeStrPre = "";
                String sizeStr = "";
                String sizeStrPost = "";
                boolean successSize = true;
                String rootStr = "";

                SimpleTextAttributes attributes = SimpleTextAttributes.merge(simpleTextAttributes, SimpleTextAttributes.GRAYED_ATTRIBUTES);
                SimpleTextAttributes blueAttributes = new SimpleTextAttributes(0, JBColor.blue);
                SimpleTextAttributes blueLightAttributes = new SimpleTextAttributes(0, new JBColor(new Color(148, 180, 243), new Color(148, 180, 243)));
                SimpleTextAttributes successSizeAttributes = new SimpleTextAttributes(0, new JBColor(new Color(16, 152, 41), new Color(121, 239, 142)));
                SimpleTextAttributes failedSizeAttributes = new SimpleTextAttributes(0, new JBColor(new Color(219, 59, 75), new Color(247, 118, 131)));

                Icon icon;
                String iconStr = "";

                if (StrUtil.isNotBlank(propertyType)) {
                    mainType = " (" + propertyType + ")";
                }

                if (StrUtil.isNotBlank(matchProperty)) {
                    fragment = " -> " + matchProperty;
                    if (StrUtil.isNotBlank(matchPropertyType)) {
                        matchType = " (" + matchPropertyType + ")";
                    }
                }

                if (Objects.nonNull(size)) {
                    sizeStrPre = " (";
                    sizeStr = size + "属性";
                    sizeStrPost = ")";
                }

                if (nodeValueTypeEnum == PropertyTreeNodeValueTypeEnum.root) {
                    iconStr = "json_object.svg";
                    rootStr = " [" + mainClassName + "]类 -> " + "[" + matchClassName + "]类";
                } else if (nodeValueTypeEnum == PropertyTreeNodeValueTypeEnum.success) {
                    iconStr = "okNew.svg";
                } else if (nodeValueTypeEnum == PropertyTreeNodeValueTypeEnum.failed) {
                    iconStr = "waring.svg";
                    successSize = false;
                } else if (nodeValueTypeEnum == PropertyTreeNodeValueTypeEnum.successProperty) {
                    iconStr = "property_success.svg";
                } else if (nodeValueTypeEnum == PropertyTreeNodeValueTypeEnum.failedProperty) {
                    iconStr = "property_failed.svg";
                }

                // 设定图标
                icon = IconLoader.getIcon("/icons/" + iconStr, JsonCollectTreeWindow.class.getClassLoader());

                append(text, simpleTextAttributes);
                if (StrUtil.isNotBlank(mainType)) append(mainType, blueAttributes, true);
                if (StrUtil.isNotBlank(sizeStrPre)) append(sizeStrPre, attributes, true);
                if (StrUtil.isNotBlank(sizeStr))
                    append(sizeStr, successSize ? successSizeAttributes : failedSizeAttributes, true);
                if (StrUtil.isNotBlank(sizeStrPost)) append(sizeStrPost, attributes, true);
                if (StrUtil.isNotBlank(fragment)) append(fragment, attributes, false);
                if (StrUtil.isNotBlank(matchType)) append(matchType, blueLightAttributes, false);
                if (StrUtil.isNotBlank(rootStr)) append(rootStr, attributes, false);
                setIcon(icon);
            }
        });
    }

    private void initPopupMenu() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addSeparator();
        group.add(new CopyLeftAction());
        group.addSeparator();
        group.add(new CopyRightAction());
        group.addSeparator();
        group.add(new CopyCorrespondingAction());
        group.addSeparator();
        group.add(new RemoveAction());

        group.addSeparator();
        group.add(new CopyClassNameAction(true));
        group.addSeparator();
        group.add(new CopyClassNameAction(false));

        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.POPUP, group);
        JPopupMenu popupMenu = actionPopupMenu.getComponent();

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // 获取选中的节点
                    TreePath[] paths = tree.getSelectionPaths();
                    if (ArrayUtil.isEmpty(paths)) {
                        int row = tree.getRowForLocation(e.getX(), e.getY());
                        if (row != -1) {
                            tree.setSelectionRow(row);
                            TreePath selectPath = tree.getPathForRow(row);
                            PropertyMatchMutableTreeNode treeNode = (PropertyMatchMutableTreeNode) selectPath.getLastPathComponent();
                            PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = treeNode.getNodeValueTypeEnum();
                            // 必须是属性节点或root节点
                            if (Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.successProperty)
                                    || Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.failedProperty)
                                    || Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.root)) {
                                popupMenu.show(tree, e.getX(), e.getY());
                            }
                        }
                    } else {
                        boolean match = Arrays.stream(paths).anyMatch(el -> {
                            PropertyMatchMutableTreeNode treeNode = (PropertyMatchMutableTreeNode) el.getLastPathComponent();
                            PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = treeNode.getNodeValueTypeEnum();
                            // 必须是属性节点
                            return Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.successProperty)
                                    || Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.failedProperty)
                                    || Objects.equals(nodeValueTypeEnum, PropertyTreeNodeValueTypeEnum.root);
                        });

                        if (match) {
                            popupMenu.show(tree, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        return actions.toArray(new Action[0]);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }


    private class CopyLeftAction extends AnAction {
        public CopyLeftAction() {
            super("拷贝属性 (左)");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                List<String> propertyList = new ArrayList<>();
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        Object userObject = node.getUserObject();
                        propertyList.add(userObject.toString());
                    }
                }

                ActionUtil.setClipboard(StrUtil.join("\n", propertyList));
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            int count = 0;
            if (ArrayUtil.isNotEmpty(paths)) {
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        count++;
                    }
                }
            }

            e.getPresentation().setEnabledAndVisible(count > 0);
        }
    }


    private class CopyRightAction extends AnAction {

        public CopyRightAction() {
            super("拷贝属性 (右)");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                List<String> propertyList = new ArrayList<>();
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        String matchProperty = node.getMatchProperty();
                        if (StrUtil.isNotBlank(matchProperty)) {
                            propertyList.add(matchProperty);
                        }
                    }
                }

                ActionUtil.setClipboard(StrUtil.join("\n", propertyList));
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            int count = 0;
            if (ArrayUtil.isNotEmpty(paths)) {
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        count++;
                    }
                }
            }

            e.getPresentation().setEnabledAndVisible(count > 0);
        }
    }

    private class CopyCorrespondingAction extends AnAction {

        public CopyCorrespondingAction() {
            super("拷贝两者..");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                List<String> propertyList = new ArrayList<>();
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {

                        Object userObject = node.getUserObject();
                        String matchProperty = node.getMatchProperty();
                        String end = "";
                        if (StrUtil.isNotBlank(matchProperty)) {
                            end = ": " + matchProperty;
                        }

                        propertyList.add(userObject.toString() + end);
                    }
                }

                ActionUtil.setClipboard(StrUtil.join("\n", propertyList));
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            int count = 0;
            if (ArrayUtil.isNotEmpty(paths)) {
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        count++;
                    }
                }
            }

            e.getPresentation().setEnabledAndVisible(count > 0);
        }
    }

    private class RemoveAction extends AnAction {

        public RemoveAction() {
            super("删除...");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null) {
                for (TreePath path : paths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.getParent() != null) {
                        ((DefaultMutableTreeNode) node.getParent()).remove(node);
                    }
                }
                ((DefaultTreeModel) tree.getModel()).reload();
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            TreePath[] paths = tree.getSelectionPaths();
            int count = 0;
            if (ArrayUtil.isNotEmpty(paths)) {
                for (TreePath path : paths) {
                    PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) path.getLastPathComponent();
                    PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                    if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)
                            || Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
                        count++;
                    }
                }
            }

            e.getPresentation().setEnabledAndVisible(count > 0);
        }
    }

    private class CopyClassNameAction extends AnAction {
        private final boolean left;

        public CopyClassNameAction(boolean left) {
            super(left ? "拷贝左类名" : "拷贝右类名");
            this.left = left;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            ActionUtil.setClipboard(left ? mainClassName : matchClassName);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            boolean enabled = false;
            TreePath[] paths = tree.getSelectionPaths();
            if (ArrayUtil.isNotEmpty(paths) && paths.length == 1) {
                TreePath treePath = paths[0];
                PropertyMatchMutableTreeNode node = (PropertyMatchMutableTreeNode) treePath.getLastPathComponent();
                PropertyTreeNodeValueTypeEnum nodeValueTypeEnum = node.getNodeValueTypeEnum();
                if (Objects.equals(PropertyTreeNodeValueTypeEnum.root, nodeValueTypeEnum)) {
                    enabled = true;
                }
            }

            e.getPresentation().setEnabledAndVisible(enabled);
        }
    }

}
