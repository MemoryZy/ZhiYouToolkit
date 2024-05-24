package cn.zhiyou.utils;

import cn.hutool.core.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/5/24
 */
public class ReflectUtil extends cn.hutool.core.util.ReflectUtil {

    public static Object getStaticFinalFieldValue(String classQualifiedName, String fieldName) {
        Field matchField = null;
        try {
            Class<?> clz = Class.forName(classQualifiedName);
            Field[] declaredFields = ClassUtil.getDeclaredFields(clz);

            for (Field field : declaredFields) {
                if (Objects.equals(fieldName, field.getName())) {
                    // 检查字段是否是静态且Final的
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        matchField = field;
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return Objects.isNull(matchField) ? null :ReflectUtil.getStaticFieldValue(matchField);
    }

}
