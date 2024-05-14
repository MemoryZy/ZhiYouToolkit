package cn.zhiyou.entity;

import com.intellij.psi.PsiField;

/**
 * 字段映射实体
 */
public class FieldMappedEntity {
    /**
     * 数据列名
     */
    private final String columnName;
    /**
     * 属性名
     */
    private final String propertyName;
    /**
     * 注释
     */
    private final String comment;
    /**
     * 是否为主键
     */
    private final boolean isPk;
    /**
     * 是否自增
     */
    private final boolean isAutoIncrement;
    /**
     * 对应的PsiField
     */
    private final PsiField psiField;

    public FieldMappedEntity(String columnName, String propertyName, String comment, boolean isPk, boolean isAutoIncrement, PsiField psiField) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.comment = comment;
        this.isPk = isPk;
        this.isAutoIncrement = isAutoIncrement;
        this.psiField = psiField;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getComment() {
        return comment;
    }

    public boolean isPk() {
        return isPk;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public PsiField getPsiField() {
        return psiField;
    }
}
