package cn.zhiyou.entity.template;

/**
 * @author Memory
 * @since 2023/12/27
 */
public class ColumnEntity extends TemplateEntity {

    /**
     * 列名
     */
    private String columnName;

    /**
     * jdbcType
     */
    private String jdbcType;

    /**
     * 属性名
     */
    private String propertyName;

    /**
     * 属性类型
     */
    private String propertyType;

    /**
     * 是否为主键
     */
    private boolean isPk;

    /**
     * 字段注释
     */
    private String comment;

    public String getColumnName() {
        return columnName;
    }

    public ColumnEntity setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public ColumnEntity setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public ColumnEntity setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public ColumnEntity setPropertyType(String propertyType) {
        this.propertyType = propertyType;
        return this;
    }

    public boolean isPk() {
        return isPk;
    }

    public ColumnEntity setPk(boolean pk) {
        isPk = pk;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ColumnEntity setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
