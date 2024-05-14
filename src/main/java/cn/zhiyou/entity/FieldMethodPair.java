package cn.zhiyou.entity;

import com.intellij.psi.PsiMethod;

/**
 * 方法与字段对应
 */
public class FieldMethodPair {
    /**
     * 类名或方法形参名
     */
    private final String classOrParamName;
    /**
     * 类全限定名名
     */
    private final String qualifiedName;
    /**
     * 字段名
     */
    private final String fieldName;
    /**
     * 方法（可以是Getter、也可以是Setter）
     */
    private final PsiMethod method;
    /**
     * 字段类型
     */
    private final String fieldType;

    public FieldMethodPair(String classOrParamName, String qualifiedName, String fieldName, PsiMethod method, String fieldType) {
        this.classOrParamName = classOrParamName;
        this.qualifiedName = qualifiedName;
        this.fieldName = fieldName;
        this.method = method;
        this.fieldType = fieldType;
    }

    public String getClassOrParamName() {
        return classOrParamName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public String getFieldType() {
        return fieldType;
    }
}
