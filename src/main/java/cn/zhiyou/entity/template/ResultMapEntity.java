package cn.zhiyou.entity.template;

import java.util.List;
import java.util.Map;

/**
 * @author Memory
 * @since 2023/12/27
 */
public class ResultMapEntity extends TemplateEntity {

    /**
     * ResultMap名称
     */
    private String name;

    /**
     * 映射类全限定名
     */
    private String qualifiedName;

    /**
     * 所有列
     */
    private List<Map<String, Object>> columns;

    public String getName() {
        return name;
    }

    public ResultMapEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public ResultMapEntity setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        return this;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public ResultMapEntity setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
        return this;
    }
}
