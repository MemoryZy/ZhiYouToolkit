package cn.zhiyou.entity;

import com.intellij.psi.PsiField;

/**
 * @param columnName   数据列名
 * @param propertyName 属性名
 * @param comment      注释
 * @author Memory
 * @since 2024/1/8
 */
public record FieldMappedEntity(String columnName, String propertyName, String comment, boolean isPk, boolean isAutoIncrement, PsiField psiField) {

}
