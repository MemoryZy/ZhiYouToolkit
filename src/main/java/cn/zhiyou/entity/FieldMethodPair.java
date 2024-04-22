package cn.zhiyou.entity;

import com.intellij.psi.PsiMethod;

/**
 * 方法与字段对应
 *
 * @param classOrParamName 类名或方法形参名
 * @param qualifiedName    类全限定名名
 * @param fieldName        字段名
 * @param method           方法（可以是Getter、也可以是Setter）
 * @author wcp
 * @since 2023/11/28
 */
public record FieldMethodPair(String classOrParamName, String qualifiedName, String fieldName, PsiMethod method, String fieldType) {

}
