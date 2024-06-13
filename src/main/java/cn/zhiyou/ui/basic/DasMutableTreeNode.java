package cn.zhiyou.ui.basic;

import cn.zhiyou.enums.DasNodeTypeEnum;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.DasNamespace;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Memory
 * @since 2024/5/19
 */
public class DasMutableTreeNode extends DefaultMutableTreeNode {

    private DasDataSource dasDataSource;

    private DasNamespace schema;

    private Icon dbIcon;

    private DasNodeTypeEnum dasNodeTypeEnum;

    public DasMutableTreeNode(Object userObject, DasDataSource dasDataSource, DasNamespace schema, Icon dbIcon, DasNodeTypeEnum dasNodeTypeEnum) {
        super(userObject);
        this.dasDataSource = dasDataSource;
        this.schema = schema;
        this.dbIcon = dbIcon;
        this.dasNodeTypeEnum = dasNodeTypeEnum;
    }

    public DasMutableTreeNode(Object userObject, DasDataSource dasDataSource, Icon dbIcon, DasNodeTypeEnum dasNodeTypeEnum) {
        super(userObject);
        this.dasDataSource = dasDataSource;
        this.dbIcon = dbIcon;
        this.dasNodeTypeEnum = dasNodeTypeEnum;
    }

    public DasMutableTreeNode(Object userObject, DasNodeTypeEnum dasNodeTypeEnum) {
        super(userObject);
        this.dasNodeTypeEnum = dasNodeTypeEnum;
    }

    public DasDataSource getDasDataSource() {
        return dasDataSource;
    }

    public void setDasDataSource(DasDataSource dasDataSource) {
        this.dasDataSource = dasDataSource;
    }

    public Icon getDbIcon() {
        return dbIcon;
    }

    public void setDbIcon(Icon dbIcon) {
        this.dbIcon = dbIcon;
    }

    public DasNodeTypeEnum getDasNodeTypeEnum() {
        return dasNodeTypeEnum;
    }

    public void setDasNodeTypeEnum(DasNodeTypeEnum dasNodeTypeEnum) {
        this.dasNodeTypeEnum = dasNodeTypeEnum;
    }

    public DasNamespace getSchema() {
        return schema;
    }

    public void setSchema(DasNamespace schema) {
        this.schema = schema;
    }
}
