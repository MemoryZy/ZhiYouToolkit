package cn.zhiyou.entity.template;

/**
 * @author wcp
 * @since 2023/12/26
 */
public class PrimaryKeyEntity extends TemplateEntity {

    /**
     * 主键属性全限定名
     */
    private String qualifiedName;

    /**
     * 主键列名
     */
    private String columnName;

    /**
     * 主键JdbcType
     */
    private String jdbcType;

    /**
     * 主键属性名
     */
    private String propertyName;


    public String getQualifiedName() {
        return qualifiedName;
    }

    public PrimaryKeyEntity setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public PrimaryKeyEntity setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public PrimaryKeyEntity setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PrimaryKeyEntity setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }
}
