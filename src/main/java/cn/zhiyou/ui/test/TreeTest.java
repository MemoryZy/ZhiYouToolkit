package cn.zhiyou.ui.test;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author wcp
 * @since 2024/2/27
 */
public class TreeTest extends DialogWrapper {
    private JPanel panel1;
    private Tree tree;

    public TreeTest(@Nullable Project project) {
        super(project, true);
        // ColoredTreeCellRenderer

        // 创建节点
        DefaultMutableTreeNode child1Leaf1 = new DefaultMutableTreeNode();
        child1Leaf1.setUserObject("child1Leaf1");

        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode();
        child1.add(child1Leaf1);
        child1.setUserObject("child1");

        DefaultMutableTreeNode child1Leaf2 = new DefaultMutableTreeNode();
        child1Leaf2.setUserObject("child1Leaf2");

        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode();
        child2.add(child1Leaf2);
        child2.setUserObject("child2");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject("root");
        root.add(child1);
        root.add(child2);

        // 创建数据模型
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
        tree.setDragEnabled(true);
        tree.setExpandableItemsEnabled(true);

        new TreeSpeedSearch(tree);

        tree.setCellRenderer( new NodeRenderer());

        init();
        setTitle("");
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }
}
