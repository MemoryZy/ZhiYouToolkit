package cn.zhiyou.entity;

/**
 * 包名和路径
 */
public class PackageAndPath {
    /**
     * mapper包路径
     */
    private final String mapperPackage;
    /**
     * entity包路径
     */
    private final String entityPackage;
    /**
     * xml文件路径
     */
    private final String xmlPath;

    public PackageAndPath(String mapperPackage, String entityPackage, String xmlPath) {
        this.mapperPackage = mapperPackage;
        this.entityPackage = entityPackage;
        this.xmlPath = xmlPath;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public String getXmlPath() {
        return xmlPath;
    }
}
