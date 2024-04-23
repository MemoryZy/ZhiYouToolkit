package cn.zhiyou.entity.template;

/**
 * @author wcp
 * @since 2023/12/28
 */
public class MapperInterfaceEntity extends TemplateEntity {

    private String entityQualifiedName;
    private String mapperName;
    private String entityName;

    // -------------------------- 默认 -------------------------- //

    private String hasPk;
    private String singlePk;
    private String autoIncrementPk;

    private String primaryKeyParam;
    private String entityParam;
    private String pkJavaType;

    public String getEntityQualifiedName() {
        return entityQualifiedName;
    }

    public MapperInterfaceEntity setEntityQualifiedName(String entityQualifiedName) {
        this.entityQualifiedName = entityQualifiedName;
        return this;
    }

    public String getMapperName() {
        return mapperName;
    }

    public MapperInterfaceEntity setMapperName(String mapperName) {
        this.mapperName = mapperName;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public MapperInterfaceEntity setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getHasPk() {
        return hasPk;
    }

    public MapperInterfaceEntity setHasPk(String hasPk) {
        this.hasPk = hasPk;
        return this;
    }

    public String getSinglePk() {
        return singlePk;
    }

    public MapperInterfaceEntity setSinglePk(String singlePk) {
        this.singlePk = singlePk;
        return this;
    }

    public String getAutoIncrementPk() {
        return autoIncrementPk;
    }

    public MapperInterfaceEntity setAutoIncrementPk(String autoIncrementPk) {
        this.autoIncrementPk = autoIncrementPk;
        return this;
    }

    public String getPrimaryKeyParam() {
        return primaryKeyParam;
    }

    public MapperInterfaceEntity setPrimaryKeyParam(String primaryKeyParam) {
        this.primaryKeyParam = primaryKeyParam;
        return this;
    }

    public String getEntityParam() {
        return entityParam;
    }

    public MapperInterfaceEntity setEntityParam(String entityParam) {
        this.entityParam = entityParam;
        return this;
    }

    public String getPkJavaType() {
        return pkJavaType;
    }

    public MapperInterfaceEntity setPkJavaType(String pkJavaType) {
        this.pkJavaType = pkJavaType;
        return this;
    }
}
