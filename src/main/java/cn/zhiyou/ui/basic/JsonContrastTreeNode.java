package cn.zhiyou.ui.basic;

import cn.zhiyou.enums.JsonContrastNodeTypeEnum;
import com.intellij.psi.PsiType;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Memory
 * @since 2024/5/20
 */
public class JsonContrastTreeNode extends DefaultMutableTreeNode {

    private String jsonKey;
    private Object jsonValue;
    private PsiType psiType;
    private JsonContrastNodeTypeEnum nodeType;

    public JsonContrastTreeNode(Object userObject) {
        super(userObject);
    }

    public JsonContrastTreeNode(Object userObject, JsonContrastNodeTypeEnum nodeType) {
        super(userObject);
        this.nodeType = nodeType;
    }

    public JsonContrastTreeNode(Object userObject, Object jsonValue, String jsonKey, JsonContrastNodeTypeEnum nodeType) {
        super(userObject);
        this.jsonValue = jsonValue;
        this.jsonKey = jsonKey;
        this.nodeType = nodeType;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public JsonContrastTreeNode setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
        return this;
    }

    public JsonContrastNodeTypeEnum getNodeType() {
        return nodeType;
    }

    public JsonContrastTreeNode setNodeType(JsonContrastNodeTypeEnum nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public Object getJsonValue() {
        return jsonValue;
    }

    public JsonContrastTreeNode setJsonValue(Object jsonValue) {
        this.jsonValue = jsonValue;
        return this;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public JsonContrastTreeNode setPsiType(PsiType psiType) {
        this.psiType = psiType;
        return this;
    }
}
