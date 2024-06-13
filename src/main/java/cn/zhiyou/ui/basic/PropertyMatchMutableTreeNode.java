package cn.zhiyou.ui.basic;

import cn.zhiyou.enums.PropertyTreeNodeValueTypeEnum;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/2/29
 */
public class PropertyMatchMutableTreeNode extends DefaultMutableTreeNode {

    /**
     * 属性类型（简称）
     */
    private String propertyType;

    /**
     * 匹配属性
     */
    private String matchProperty;

    /**
     * 匹配属性类型
     */
    private String matchPropertyType;

    private PropertyTreeNodeValueTypeEnum nodeValueTypeEnum;

    private Integer size;

    public PropertyMatchMutableTreeNode(Object userObject) {
        super(userObject);
    }

    public PropertyMatchMutableTreeNode(Object userObject, String propertyType) {
        super(userObject);
        this.propertyType = propertyType;
    }

    public PropertyMatchMutableTreeNode(Object userObject, PropertyTreeNodeValueTypeEnum nodeValueTypeEnum) {
        super(userObject);
        this.nodeValueTypeEnum = nodeValueTypeEnum;
    }

    public PropertyMatchMutableTreeNode(Object userObject, String propertyType, String matchProperty, String matchPropertyType, PropertyTreeNodeValueTypeEnum nodeValueTypeEnum) {
        super(userObject);
        this.propertyType = propertyType;
        this.matchProperty = matchProperty;
        this.matchPropertyType = matchPropertyType;
        this.nodeValueTypeEnum = nodeValueTypeEnum;
    }

    public PropertyMatchMutableTreeNode(Object userObject, String propertyType, String matchProperty, String matchPropertyType) {
        super(userObject);
        this.propertyType = propertyType;
        this.matchProperty = matchProperty;
        this.matchPropertyType = matchPropertyType;
    }

    public PropertyMatchMutableTreeNode(Object userObject, String propertyType, PropertyTreeNodeValueTypeEnum nodeValueTypeEnum) {
        super(userObject);
        this.propertyType = propertyType;
        this.nodeValueTypeEnum = nodeValueTypeEnum;
    }

    public Integer getSize() {
        return size;
    }

    public PropertyMatchMutableTreeNode setSize(Integer size) {
        this.size = size;
        return this;
    }

    public PropertyTreeNodeValueTypeEnum getNodeValueTypeEnum() {
        return nodeValueTypeEnum;
    }

    public void setNodeValueTypeEnum(PropertyTreeNodeValueTypeEnum nodeValueTypeEnum) {
        this.nodeValueTypeEnum = nodeValueTypeEnum;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getMatchProperty() {
        return matchProperty;
    }

    public void setMatchProperty(String matchProperty) {
        this.matchProperty = matchProperty;
    }

    public String getMatchPropertyType() {
        return matchPropertyType;
    }

    public void setMatchPropertyType(String matchPropertyType) {
        this.matchPropertyType = matchPropertyType;
    }

    @Override
    public String toString() {
        if (Objects.equals(PropertyTreeNodeValueTypeEnum.successProperty, nodeValueTypeEnum)) {
            return getUserObject().toString() + " " + propertyType + " " + matchProperty + " " + matchPropertyType;
        } else if (Objects.equals(PropertyTreeNodeValueTypeEnum.failedProperty, nodeValueTypeEnum)) {
            return getUserObject().toString() + " " + propertyType;
        } else {
            return getUserObject().toString();
        }
    }
}
