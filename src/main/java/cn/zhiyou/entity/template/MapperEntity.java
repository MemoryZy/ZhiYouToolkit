package cn.zhiyou.entity.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wcp
 * @since 2023/12/26
 */
public class MapperEntity extends TemplateEntity {

    /**
     * Mapper接口全限定名
     */
    private String mapperQualifiedName;

    /**
     * 实体类全限定名
     */
    private String entityQualifiedName;

    /**
     * 普通列信息，不含主键
     */
    private List<Map<String, Object>> columnMapList;

    /**
     * 表名
     */
    private String tableName;

    /**
     * ResultMap文本
     */
    private String resultMap;

    /**
     * 是否有主键
     */
    private boolean hasPk;

    /**
     * 是否为单独主键
     */
    private boolean singlePk;

    /**
     * 是否为自增主键
     */
    private boolean autoIncrementPk;

    /**
     * 主键列表
     */
    private List<Map<String, Object>> primaryKeyMapList;

    /**
     * 所有列字段，按照逗号间隔，例如 [id, name, age]
     */
    private String fieldsCommaInterval;

    /**
     * 新增时的列字段，按照逗号间隔，例如 [id, name, age]
     */
    private String insertFieldsCommaInterval;

    /**
     * 属性，按照逗号间隔，例如 [#{id,jdbcType=BIGINT},#{name,jdbcType=VARCHAR}]
     */
    private String propertiesCommaInterval;

    /**
     * 新增时的属性，按照逗号间隔，例如 [#{id,jdbcType=BIGINT},#{name,jdbcType=VARCHAR}]
     */
    private String insertPropertiesCommaInterval;

    /**
     * Update属性对应，按照逗号间隔，例如 [id = #{id,jdbcType=VARCHAR}, code = #{code,jdbcType=VARCHAR}]
     */
    private String updateAttributeCorrespondence;

    /**
     * 主键条件，按AND分割，例如: 单个主键就是 [id = #{id,jdbcType=BIGINT}]，多主键就是 [id = #{id,jdbcType=BIGINT} AND FLID = #{flid,jdbcType=VARCHAR}]
     */
    private String primaryKeyCondition;

    /**
     * 普通insert列名，存在所有元素，并换行
     */
    private String commonFieldsCommaInterval;

    /**
     * 插入时的if，不包括id <if test="chgId != null">CHG_ID,</if>
     */
    private String insertIfField;

    /**
     * 插入时的if，不包括id <if test="chgId != null">#{chgId,jdbcType=BIGINT},</if>
     */
    private String insertIfProperty;

    /**
     * 插入时的if，包括id CHG_ID,
     */
    private String allInsertIfField;

    /**
     * 插入时的if，包括id #{chgId,jdbcType=BIGINT},
     */
    private String allInsertIfProperty;


    public MapperEntity setMapperQualifiedName(String mapperQualifiedName) {
        this.mapperQualifiedName = mapperQualifiedName;
        return this;
    }

    public MapperEntity setEntityQualifiedName(String entityQualifiedName) {
        this.entityQualifiedName = entityQualifiedName;
        return this;
    }


    public MapperEntity setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getAllInsertIfField() {
        return allInsertIfField;
    }

    public void setAllInsertIfField(String allInsertIfField) {
        this.allInsertIfField = allInsertIfField;
    }

    public String getAllInsertIfProperty() {
        return allInsertIfProperty;
    }

    public void setAllInsertIfProperty(String allInsertIfProperty) {
        this.allInsertIfProperty = allInsertIfProperty;
    }

    public String getInsertIfField() {
        return insertIfField;
    }

    public void setInsertIfField(String insertIfField) {
        this.insertIfField = insertIfField;
    }

    public String getInsertIfProperty() {
        return insertIfProperty;
    }

    public void setInsertIfProperty(String insertIfProperty) {
        this.insertIfProperty = insertIfProperty;
    }

    public String getCommonFieldsCommaInterval() {
        return commonFieldsCommaInterval;
    }

    public void setCommonFieldsCommaInterval(String commonFieldsCommaInterval) {
        this.commonFieldsCommaInterval = commonFieldsCommaInterval;
    }

    public String getResultMap() {
        return resultMap;
    }

    public MapperEntity setResultMap(String resultMap) {
        this.resultMap = resultMap;
        return this;
    }

    public List<Map<String, Object>> getPrimaryKeyMapList() {
        return primaryKeyMapList;
    }

    public MapperEntity setPrimaryKeyMapList(List<Map<String, Object>> primaryKeyMapList) {
        this.primaryKeyMapList = primaryKeyMapList;
        return this;
    }

    public String getFieldsCommaInterval() {
        return fieldsCommaInterval;
    }

    public void setFieldsCommaInterval(String fieldsCommaInterval) {
        this.fieldsCommaInterval = fieldsCommaInterval;
    }

    public String getInsertPropertiesCommaInterval() {
        return insertPropertiesCommaInterval;
    }

    public void setInsertPropertiesCommaInterval(String insertPropertiesCommaInterval) {
        this.insertPropertiesCommaInterval = insertPropertiesCommaInterval;
    }

    public MapperEntity setInsertFieldsCommaInterval(String insertFieldsCommaInterval) {
        this.insertFieldsCommaInterval = insertFieldsCommaInterval;
        return this;
    }

    public MapperEntity setPropertiesCommaInterval(String propertiesCommaInterval) {
        this.propertiesCommaInterval = propertiesCommaInterval;
        return this;
    }

    public MapperEntity setUpdateAttributeCorrespondence(String updateAttributeCorrespondence) {
        this.updateAttributeCorrespondence = updateAttributeCorrespondence;
        return this;
    }

    public boolean isHasPk() {
        return hasPk;
    }

    public MapperEntity setHasPk(boolean hasPk) {
        this.hasPk = hasPk;
        return this;
    }

    public boolean isAutoIncrementPk() {
        return autoIncrementPk;
    }

    public MapperEntity setAutoIncrementPk(boolean autoIncrementPk) {
        this.autoIncrementPk = autoIncrementPk;
        return this;
    }

    public boolean isSinglePk() {
        return singlePk;
    }

    public MapperEntity setSinglePk(boolean singlePk) {
        this.singlePk = singlePk;
        return this;
    }

    public String getPrimaryKeyCondition() {
        return primaryKeyCondition;
    }

    public MapperEntity setPrimaryKeyCondition(String primaryKeyCondition) {
        this.primaryKeyCondition = primaryKeyCondition;
        return this;
    }

    public String getMapperQualifiedName() {
        return mapperQualifiedName;
    }

    public String getEntityQualifiedName() {
        return entityQualifiedName;
    }

    public List<Map<String, Object>> getColumnMapList() {
        return columnMapList;
    }

    public MapperEntity setColumnMapList(List<Map<String, Object>> columnMapList) {
        this.columnMapList = columnMapList;
        return this;
    }

    public String getTableName() {
        return tableName;
    }


    public String getInsertFieldsCommaInterval() {
        return insertFieldsCommaInterval;
    }

    public String getPropertiesCommaInterval() {
        return propertiesCommaInterval;
    }

    public String getUpdateAttributeCorrespondence() {
        return updateAttributeCorrespondence;
    }


    /**
     * 生成所有列字段，并按照逗号间隔，例如 [id, name, age]
     */
    public MapperEntity createAndSetFieldsCommaInterval() {
        List<ColumnEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), ColumnEntity.class);
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        columnEntityList.addAll(0, primaryKeyEntityList);

        if (CollUtil.isNotEmpty(columnEntityList)) {
            // 10个就换行
            List<String> columnList = columnEntityList.stream().map(ColumnEntity::getColumnName).toList();

            String fieldsCommaInterval;
            if (columnList.size() > 8) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < columnList.size(); i++) {
                    String column = columnList.get(i);
                    // 8个字段换个行并加8个空格
                    if (i != 0 && i % 7 == 0) {
                        builder.append(column).append(", \n        ");
                    } else {
                        // 最后一个字段不加分隔符
                        builder.append(column).append((i == columnList.size() - 1) ? "" : ", ");
                    }
                }
                fieldsCommaInterval = builder.toString();
            } else {
                fieldsCommaInterval = StrUtil.join(", ", columnList);
            }

            setFieldsCommaInterval(fieldsCommaInterval);
        }

        return this;
    }

    public MapperEntity createAndSetCommonFieldsCommaInterval() {
        List<ColumnEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), ColumnEntity.class);
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        columnEntityList.addAll(0, primaryKeyEntityList);

        String commonFieldsCommaInterval = "";
        if (CollUtil.isNotEmpty(columnEntityList)) {
            List<String> columnList = columnEntityList.stream().map(ColumnEntity::getColumnName).toList();
            commonFieldsCommaInterval = StrUtil.join(",\n         ", columnList);
        }

        setCommonFieldsCommaInterval(commonFieldsCommaInterval);
        return this;
    }


    /**
     * 生成新增列字段，并按照逗号间隔，例如 [id, name, age]
     */
    public MapperEntity createAndSetInsertFieldsCommaInterval() {
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        List<PrimaryKeyEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), PrimaryKeyEntity.class);
        List<String> primaryKeyList = primaryKeyEntityList.stream().map(PrimaryKeyEntity::getColumnName).toList();

        if (CollUtil.isNotEmpty(columnEntityList)) {
            // 过滤主键
            List<String> properties = columnEntityList.stream()
                    .map(ColumnEntity::getColumnName)
                    // 如果是自增主键，就剔除该主键
                    .filter(el -> !singlePk || !autoIncrementPk || !primaryKeyList.contains(el))
                    .toList();

            setInsertFieldsCommaInterval(StrUtil.join(", \n         ", properties));
        }

        return this;
    }

    /**
     * 生成所有属性字段，并按照逗号间隔，例如 [#{id,jdbcType=BIGINT},#{name,jdbcType=VARCHAR}]
     */
    public MapperEntity createAndSetPropertiesCommaInterval() {
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        if (CollUtil.isNotEmpty(columnEntityList)) {
            List<String> properties = columnEntityList.stream()
                    // 属性名，jdbcType
                    .map(el -> String.format("#{%s,jdbcType=%s}", el.getPropertyName(), el.getJdbcType()))
                    .toList();

            setPropertiesCommaInterval(StrUtil.join(", \n                ", properties));
        }

        return this;
    }

    /**
     * 生成新增属性字段，并按照逗号间隔，例如 [#{id,jdbcType=BIGINT},#{name,jdbcType=VARCHAR}]
     */
    public MapperEntity createAndSetInsertPropertiesCommaInterval() {
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        List<PrimaryKeyEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), PrimaryKeyEntity.class);
        List<String> primaryKeyList = primaryKeyEntityList.stream().map(PrimaryKeyEntity::getPropertyName).toList();

        if (CollUtil.isNotEmpty(columnEntityList)) {
            List<String> properties = columnEntityList.stream()
                    // 如果是自增主键，就剔除该主键
                    .filter(el -> !singlePk || !autoIncrementPk || !primaryKeyList.contains(el.getPropertyName()))
                    // 属性名，jdbcType
                    .map(el -> String.format("#{%s,jdbcType=%s}", el.getPropertyName(), el.getJdbcType()))
                    .toList();

            setInsertPropertiesCommaInterval(StrUtil.join(", \n                ", properties));
        }

        return this;
    }

    /**
     * 生成所有属性字段与列字段对应的update，并按照逗号间隔，例如 [id = #{id,jdbcType=VARCHAR}, code = #{code,jdbcType=VARCHAR}]
     */
    public MapperEntity createAndSetUpdateAttributeCorrespondence() {
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        List<PrimaryKeyEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), PrimaryKeyEntity.class);

        if (CollUtil.isNotEmpty(columnEntityList)) {
            List<String> properties = columnEntityList.stream()
                    .filter(el -> {
                        // 更新时需要去除主键
                        if (hasPk) {
                            for (PrimaryKeyEntity primaryKeyEntity : primaryKeyEntityList) {
                                if (Objects.equals(el.getColumnName(), primaryKeyEntity.getColumnName())) {
                                    return false;
                                }
                            }
                        }

                        return true;
                    })
                    // 列名，属性名，jdbcType
                    .map(el -> String.format("%s = #{%s,jdbcType=%s}", el.getColumnName(), el.getPropertyName(), el.getJdbcType()))
                    .toList();

            setUpdateAttributeCorrespondence(StrUtil.join(",\n              ", properties));
        }

        return this;
    }

    /**
     * 主键条件，按AND分割，例如: 单个主键就是 [id = #{id,jdbcType=BIGINT}]，多主键就是 [id = #{id,jdbcType=BIGINT} AND FLID = #{flid,jdbcType=VARCHAR}]
     */
    public MapperEntity createAndSetPrimaryKeyCondition() {
        // 获取所有主键
        List<PrimaryKeyEntity> primaryKeyEntityList = toBean(getPrimaryKeyMapList(), PrimaryKeyEntity.class);
        if (CollUtil.isNotEmpty(primaryKeyEntityList)) {
            List<String> properties = primaryKeyEntityList.stream()
                    // 列名，属性名，jdbcType
                    .map(el -> String.format("%s = #{%s,jdbcType=%s}", el.getColumnName(), el.getPropertyName(), el.getJdbcType()))
                    .toList();

            setPrimaryKeyCondition(StrUtil.join("\n AND ", properties));
        }

        return this;
    }


    public MapperEntity createAndSetInsertIfField() {
        // 不包括主键
        // <if test="chgId != null">CHG_ID,</if>
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columnEntityList.size(); i++) {
            ColumnEntity columnEntity = columnEntityList.get(i);
            String columnName = columnEntity.getColumnName();
            String propertyName = columnEntity.getPropertyName();
            boolean end = i == columnEntityList.size() - 1;
            String postfix = end ? "" : ",";

            builder.append(StrUtil.format("{}<if test=\"{} != null\">{}</if>{}",
                    (i == 0) ? "" : "            ",
                    propertyName,
                    columnName + postfix,
                    end ? "" : "\n"));
        }

        setInsertIfField(builder.toString());
        return this;
    }


    public MapperEntity createAndSetInsertIfProperty() {
        // 不包括主键
        // <if test="chgId != null">#{chgId,jdbcType=BIGINT},</if>
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columnEntityList.size(); i++) {
            ColumnEntity columnEntity = columnEntityList.get(i);
            String propertyName = columnEntity.getPropertyName();
            boolean end = i == columnEntityList.size() - 1;
            String postfix = end ? "" : ",";
            String property = StrUtil.format("#{{},jdbcType={}}{}", propertyName, columnEntity.getJdbcType(), postfix);

            builder.append(StrUtil.format("{}<if test=\"{} != null\">{}</if>{}",
                    (i == 0) ? "" : "            ",
                    propertyName,
                    property,
                    end ? "" : "\n"));
        }

        setInsertIfProperty(builder.toString());
        return this;
    }


    public MapperEntity createAndSetAllInsertIfField() {
        // 包括主键
        // <if test="chgId != null">CHG_ID,</if>
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        List<ColumnEntity> primaryKeyMapList = toBean(getPrimaryKeyMapList(), ColumnEntity.class);
        columnEntityList.addAll(primaryKeyMapList);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columnEntityList.size(); i++) {
            ColumnEntity columnEntity = columnEntityList.get(i);
            String columnName = columnEntity.getColumnName();
            String propertyName = columnEntity.getPropertyName();
            boolean end = i == columnEntityList.size() - 1;
            String postfix = end ? "" : ",";

            builder.append(StrUtil.format("{}<if test=\"{} != null\">{}</if>{}",
                    (i == 0) ? "" : "            ",
                    propertyName,
                    columnName + postfix,
                    end ? "" : "\n"));
        }

        setAllInsertIfField(builder.toString());
        return this;
    }


    public MapperEntity createAndSetAllInsertIfProperty() {
        // 不包括主键
        // <if test="chgId != null">#{chgId,jdbcType=BIGINT},</if>
        List<ColumnEntity> columnEntityList = toBean(getColumnMapList(), ColumnEntity.class);
        List<ColumnEntity> primaryKeyMapList = toBean(getPrimaryKeyMapList(), ColumnEntity.class);
        columnEntityList.addAll(primaryKeyMapList);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columnEntityList.size(); i++) {
            ColumnEntity columnEntity = columnEntityList.get(i);
            String propertyName = columnEntity.getPropertyName();
            boolean end = i == columnEntityList.size() - 1;
            String postfix = end ? "" : ",";
            String property = StrUtil.format("#{{},jdbcType={}}{}", propertyName, columnEntity.getJdbcType(), postfix);

            builder.append(StrUtil.format("{}<if test=\"{} != null\">{}</if>{}",
                    (i == 0) ? "" : "            ",
                    propertyName,
                    property,
                    end ? "" : "\n"));
        }

        setAllInsertIfProperty(builder.toString());
        return this;
    }


}
