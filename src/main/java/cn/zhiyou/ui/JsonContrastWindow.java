package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.constant.ZhiYouConstant;
import cn.zhiyou.enums.JsonContrastNodeTypeEnum;
import cn.zhiyou.ui.basic.JsonContrastTreeNode;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Memory
 * @since 2024/5/20
 */
public class JsonContrastWindow extends DialogWrapper {
    private Tree tree;
    private final PsiClass selectPsiClass;
    private final JSONObject jsonObject;

    public JsonContrastWindow(@Nullable Project project, PsiClass selectPsiClass, JSONObject jsonObject) {
        super(project, true);
        this.selectPsiClass = selectPsiClass;
        this.jsonObject = jsonObject;

        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // 类名
        String className = selectPsiClass.getName();
        // 根节点
        JsonContrastTreeNode classNode = new JsonContrastTreeNode(className, JsonContrastNodeTypeEnum.ROOT_);

        // 递归添加所有属性，包括嵌套属性
        recursionMatchProperty(selectPsiClass, jsonObject, classNode);

        // 构建树
        tree = new Tree(new DefaultTreeModel(classNode));
        tree.setDragEnabled(true);
        tree.setExpandableItemsEnabled(true);
        tree.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));

        initCellRenderer();

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tree)
                .addExtraAction(new ExpandAction())
                .addExtraAction(new CollapseAction());

        JPanel rootPanel = new JPanel(new BorderLayout());
        // 添加位置
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);

        Dimension dimension = new Dimension(400, 470);
        rootPanel.setMinimumSize(dimension);
        rootPanel.setPreferredSize(dimension);

        return rootPanel;
    }


    private void recursionMatchProperty(PsiClass psiClass, JSONObject jsonObject, JsonContrastTreeNode parentNode) {
        // 获取该类所有字段
        PsiField[] allFields = ActionUtil.getAllFieldFilterStatic(psiClass);
        for (PsiField psiField : allFields) {
            String fieldName = psiField.getName();
            JsonContrastTreeNode childNode = new JsonContrastTreeNode(fieldName);

            // 匹配Json中的key
            Map.Entry<String, Object> matchEntry = null;
            for (Map.Entry<String, Object> entry : jsonObject) {
                if (CommonUtil.matchCase(entry.getKey(), fieldName)) {
                    matchEntry = entry;
                    break;
                }
            }

            String jsonKey;
            Object jsonValue;
            boolean notMatch = Objects.isNull(matchEntry);
            if (notMatch) {
                jsonKey = null;
                jsonValue = null;
            } else {
                jsonKey = matchEntry.getKey();
                Object value = matchEntry.getValue();
                if (value instanceof JSONNull) {
                    jsonValue = null;
                } else {
                    jsonValue = value;
                }
            }

            // 字段类型
            PsiType psiType = psiField.getType();

            // 是否为引用类型
            if (ActionUtil.isReferenceType(psiType)) {
                // 如果没找到Json对应的key，或者匹配到的不是object的话，就不递归
                if (notMatch || !(jsonValue instanceof JSONObject)) {
                    childNode.setJsonValue(null)
                            .setJsonKey(null)
                            .setPsiType(psiType)
                            .setNodeType(JsonContrastNodeTypeEnum.CLASS_);
                } else {
                    // 递归
                    childNode.setJsonValue(jsonValue)
                            .setJsonKey(jsonKey)
                            .setPsiType(psiType)
                            .setNodeType(JsonContrastNodeTypeEnum.CLASS_);

                    recursionMatchProperty(ActionUtil.getPsiClassByReferenceType(psiType), (JSONObject) jsonValue, childNode);
                }

                parentNode.add(childNode);
            } else {
                // 普通类型
                childNode.setJsonValue(jsonValue)
                        .setJsonKey(jsonKey)
                        .setPsiType(psiType)
                        .setNodeType(JsonContrastNodeTypeEnum.PROPERTY_);

                parentNode.add(childNode);
            }
        }
    }


    private void initCellRenderer() {
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                SimpleTextAttributes simpleTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                SimpleTextAttributes lightAttributes = SimpleTextAttributes.merge(simpleTextAttributes, SimpleTextAttributes.GRAYED_ATTRIBUTES);
                SimpleTextAttributes purpleAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(127, 2, 6), new Color(232, 122, 113)));
                SimpleTextAttributes stringColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(6, 125, 23), new Color(104, 169, 114)));
                SimpleTextAttributes booleanWithNullColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(0, 51, 179), new Color(199, 125, 187)));
                SimpleTextAttributes numberColorAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, new JBColor(new Color(25, 80, 234), new Color(41, 171, 183)));

                JsonContrastTreeNode treeNode = (JsonContrastTreeNode) value;
                Object jsonValue = treeNode.getJsonValue();
                JsonContrastNodeTypeEnum nodeType = treeNode.getNodeType();
                PsiType psiType = treeNode.getPsiType();

                boolean isRoot = Objects.equals(JsonContrastNodeTypeEnum.ROOT_, nodeType);
                boolean isClass = Objects.equals(JsonContrastNodeTypeEnum.CLASS_, nodeType);
                boolean isProperty = Objects.equals(JsonContrastNodeTypeEnum.PROPERTY_, nodeType);

                append(treeNode.getUserObject().toString(), isRoot ? simpleTextAttributes : purpleAttributes);

                if (!isRoot) {
                    String after = " = ";
                    String realType = "";
                    String type = "";

                    String valueOfType;
                    if (Objects.isNull(jsonValue) || jsonValue instanceof JSONNull) {
                        type = "null";
                        valueOfType = "null";
                    } else {
                        if (jsonValue instanceof String) {
                            type = String.class.getName();
                            valueOfType = "\"" + jsonValue + "\"";
                        } else if (jsonValue instanceof Boolean) {
                            type = Boolean.class.getName();
                            valueOfType = jsonValue.toString();
                        } else if (jsonValue instanceof Number) {
                            type = Number.class.getName();
                            valueOfType = jsonValue.toString();
                        } else if (jsonValue instanceof JSONObject) {
                            valueOfType = "";
                        } else if (jsonValue instanceof JSONArray jsonArray) {
                            valueOfType = jsonArray.isEmpty() ? "size = 0" : "size = " + jsonArray.size();
                        } else {
                            valueOfType = jsonValue.toString();
                        }
                    }

                    SimpleTextAttributes useColorTextAttributes = simpleTextAttributes;

                    if (Objects.equals("null", type) || Objects.equals(type, Boolean.class.getName())) {
                        useColorTextAttributes = booleanWithNullColorAttributes;
                    } else if (Objects.equals(type, String.class.getName())) {
                        useColorTextAttributes = stringColorAttributes;
                    } else if (Objects.equals(type, Number.class.getName())) {
                        useColorTextAttributes = numberColorAttributes;
                    }

                    // 全限定名
                    String presentableText = psiType.getPresentableText();
                    if (!Objects.equals(String.class.getSimpleName(), presentableText) && !Objects.isNull(jsonValue)) {
                        String canonicalText = psiType.getCanonicalText(true);
                        presentableText = getPresentableText(canonicalText, presentableText);

                        realType = "{" + presentableText + "} ";
                    }

                    append(after, simpleTextAttributes);
                    // 真实属性类型（除String、null）
                    if (StrUtil.isNotBlank(realType)) append(realType, lightAttributes);

                    append(valueOfType, useColorTextAttributes);
                }

                Icon icon = null;
                if (isRoot) {
                    icon = AllIcons.Debugger.Value;
                } else if (isClass) {
                    icon = AllIcons.Nodes.Class;
                } else if (isProperty) {
                    icon = AllIcons.Nodes.Field;
                }

                setIcon(icon);
            }
        });
    }

    private String getPresentableText(String canonicalText, String presentableText) {
        for (String name : ZhiYouConstant.JAVA_COLLECTION) {
            if (canonicalText.startsWith(name)) {
                return CommonUtil.qualifiedNameToClassName(name);
            }
        }

        return presentableText;
    }


    private class ExpandAction extends AnAction {

        public ExpandAction() {
            super("展开所有", null, Icons.expandAll);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            TreeNode root = (TreeNode) tree.getModel().getRoot();
            CommonUtil.expandAll(tree, new TreePath(root));
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }

    private class CollapseAction extends AnAction {
        public CollapseAction() {
            super("折叠所有", null, Icons.collapseAll);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            TreeNode root = (TreeNode) tree.getModel().getRoot();
            CommonUtil.collapseAll(tree, new TreePath(root));
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    }

}
