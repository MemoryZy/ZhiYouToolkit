package cn.zhiyou.entity;

/**
 * 包名和路径
 *
 * @param mapperPackage mapper包路径
 * @param entityPackage entity包路径
 * @param xmlPath       xml文件路径
 * @author Memory
 * @since 2023/12/22
 */
public record PackageAndPath(String mapperPackage, String entityPackage, String xmlPath) {

}
