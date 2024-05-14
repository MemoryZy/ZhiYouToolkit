package cn.zhiyou.utils;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.JdbcType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 编码生成工具
 *
 * @author wcp
 * @since 2023/11/27
 */
public class CodeCreateUtil {

    /**
     * 添加单个字段到Class中
     *
     * @param project   当前项目
     * @param psiClass  当前Class
     * @param fieldText 字段属性文本（private String name;）
     */
    public static void addSingleField(Project project, PsiClass psiClass, String fieldText) {
        // Java元素构建器
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        ActionUtil.runWriteCommandAction(project, () -> {
            // 构建字段对象
            PsiField psiField = factory.createFieldFromText(fieldText, psiClass);
            // 添加到Class
            psiClass.add(psiField);
        });
    }


    /**
     * 将指定的类添加到给定类的实现列表中。
     *
     * @param project  Java项目
     * @param psiClass 要添加实现的类
     * @param refClass 要添加的类
     */
    public static void addSingleImplements(Project project, PsiClass psiClass, PsiClass refClass) {
        // Java元素构建器
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        ActionUtil.runWriteCommandAction(project, () -> {
            PsiJavaCodeReferenceElement classReferenceElement = factory.createClassReferenceElement(refClass);
            PsiElement implementsList = psiClass.getImplementsList();
            if (Objects.nonNull(implementsList)) {
                implementsList.add(classReferenceElement);
            }
        });
    }


    /**
     * 添加实体类的继承类，没有用异步，需要外层加上异步
     *
     * @param factory  工厂
     * @param psiClass 要添加到的类
     * @param refClass 引用类
     */
    public static void addSingleExtendsNoAsync(PsiElementFactory factory, PsiClass psiClass, PsiClass refClass) {
        // 添加继承
        PsiJavaCodeReferenceElement classReferenceElement = factory.createClassReferenceElement(refClass);
        PsiReferenceList extendsList = psiClass.getExtendsList();
        if (Objects.nonNull(extendsList)) {
            extendsList.add(classReferenceElement);
        }
    }


    /**
     * 根据对象获取其类型字符串
     *
     * @param obj 对象
     * @return 类型名
     */
    @SuppressWarnings("rawtypes")
    public static String getStrType(Object obj) {
        String type = Object.class.getSimpleName();
        if ((obj instanceof Double) || (obj instanceof Integer) || (obj instanceof Boolean)) {
            type = obj.getClass().getSimpleName();

        } else if (obj instanceof String) {
            String str = (String) obj;
            // 时间类型判断
            return checkJsonDateType(str);

        } else if (obj instanceof List ) {
            List list = (List) obj;
            // 判断是否有值
            if (list.isEmpty()) {
                type = List.class.getSimpleName();
            } else {
                String genericsType = getStrType(list.get(0));
                type = StrUtil.format("{}<{}>", List.class.getSimpleName(), genericsType);
            }
        }

        return type;
    }


    /**
     * 判断是否为时间类型
     *
     * @param obj 参数
     * @return 为时间类型返回Date、否则返回String
     */
    private static String checkJsonDateType(String obj) {
        String type = String.class.getSimpleName();
        try {
            DateTime time = DateUtil.parse(obj);
            if (Objects.nonNull(time)) {
                type = Date.class.getSimpleName();
            }
        } catch (DateException e) {
            // 忽略异常
        }
        return type;
    }


    /**
     * 获取JDBC类型
     *
     * @param qualifiedName 限定名
     * @return 返回JDBC类型，如果未找到则返回""
     */
    public static String getJdbcType(String qualifiedName) {
        if (qualifiedName.equals("boolean") || qualifiedName.equals("java.lang.Boolean")) {
            return "BIT";
        } else if (qualifiedName.equals("byte") || qualifiedName.equals("java.lang.Byte")) {
            return "TINYINT";
        } else if (qualifiedName.equals("short") || qualifiedName.equals("java.lang.Short")) {
            return "SMALLINT";
        } else if (qualifiedName.equals("char") || qualifiedName.equals("java.lang.Character")) {
            return "CHAR";
        } else if (qualifiedName.equals("int") || qualifiedName.equals("java.lang.Integer")) {
            return "INTEGER";
        } else if (qualifiedName.equals("long") || qualifiedName.equals("java.lang.Long")) {
            return "BIGINT";
        } else if (qualifiedName.equals("float") || qualifiedName.equals("java.lang.Float")) {
            return "FLOAT";
        } else if (qualifiedName.equals("double") || qualifiedName.equals("java.lang.Double")) {
            return "DOUBLE";
        } else if (qualifiedName.equals("java.math.BigDecimal")) {
            return "DECIMAL";
        } else if (qualifiedName.equals("String") || qualifiedName.equals("java.lang.String")) {
            return "VARCHAR";
        } else if (qualifiedName.equals("java.util.Date")) {
            return "TIMESTAMP";
        } else if (qualifiedName.equals("java.sql.Date")) {
            return "DATE";
        } else if (qualifiedName.equals("java.sql.Time")) {
            return "TIME";
        } else if (qualifiedName.equals("java.sql.Timestamp")) {
            return "TIMESTAMP";
        } else if (qualifiedName.equals("byte[]")) {
            return "VARBINARY";
        } else if (qualifiedName.equals("java.sql.Blob")) {
            return "BLOB";
        } else if (qualifiedName.equals("java.sql.Clob")) {
            return "CLOB";
        } else if (qualifiedName.equals("java.util.Set")) {
            return "ARRAY";
        } else if (qualifiedName.equals("java.util.Enum")) {
            return "VARCHAR";
        } else if (qualifiedName.equals("java.util.Enum[]")) {
            return "ARRAY";
        } else if (qualifiedName.equals("java.time.Instant")) {
            return "TIMESTAMP";
        } else if (qualifiedName.equals("java.time.LocalDateTime")) {
            return "TIMESTAMP";
        } else if (qualifiedName.equals("java.time.LocalDate")) {
            return "DATE";
        } else if (qualifiedName.equals("java.time.LocalTime")) {
            return "TIME";
        } else if (qualifiedName.equals("java.util.Collection") || qualifiedName.equals("java.util.List") ||
                qualifiedName.equals("java.util.ArrayList") || qualifiedName.equals("java.util.Map") ||
                qualifiedName.equals("java.util.HashMap")) {
            return "ARRAY";
        } else {
            return "";
        }
    }


    /**
     * 根据JdbcType和useNewDateApi返回Java类型
     *
     * @param jdbcType      JdbcType枚举类型
     * @param useNewDateApi 是否使用新日期API
     * @return Java类型字符串
     */
    public static String getJavaType(JdbcType jdbcType, boolean useNewDateApi) {

        if (jdbcType == JdbcType.ARRAY) {
            return Array.class.getName();
        } else if (jdbcType == JdbcType.BIT || jdbcType == JdbcType.BOOLEAN) {
            return Boolean.class.getName();
        } else if (jdbcType == JdbcType.TINYINT) {
            return Byte.class.getName();
        } else if (jdbcType == JdbcType.SMALLINT) {
            return Short.class.getName();
        } else if (jdbcType == JdbcType.INTEGER) {
            return Integer.class.getName();
        } else if (jdbcType == JdbcType.BIGINT) {
            return Long.class.getName();
        } else if (jdbcType == JdbcType.FLOAT) {
            return Float.class.getName();
        } else if (jdbcType == JdbcType.DOUBLE) {
            return Double.class.getName();
        } else if (jdbcType == JdbcType.NUMERIC || jdbcType == JdbcType.DECIMAL || jdbcType == JdbcType.REAL) {
            return BigDecimal.class.getName();
        } else if (jdbcType == JdbcType.CHAR || jdbcType == JdbcType.VARCHAR || jdbcType == JdbcType.LONGVARCHAR ||
                jdbcType == JdbcType.NVARCHAR || jdbcType == JdbcType.CLOB || jdbcType == JdbcType.NCLOB ||
                jdbcType == JdbcType.SQLXML || jdbcType == JdbcType.NCHAR) {
            return String.class.getName();
        } else if (jdbcType == JdbcType.DATE) {
            return useNewDateApi ? LocalDate.class.getName() : Date.class.getName();
        } else if (jdbcType == JdbcType.TIME) {
            return useNewDateApi ? LocalTime.class.getName() : Time.class.getName();
        } else if (jdbcType == JdbcType.TIMESTAMP) {
            return useNewDateApi ? LocalDateTime.class.getName() : Date.class.getName();
        } else if (jdbcType == JdbcType.BINARY || jdbcType == JdbcType.VARBINARY || jdbcType == JdbcType.LONGVARBINARY ||
                jdbcType == JdbcType.BLOB) {
            return "Byte[]";
        } else {
            return Object.class.getName();
        }
    }


    public static int convertTypeNameToJdbcType(String jdbcTypeName, String databaseType) {
        if (StrUtil.isBlank(jdbcTypeName)) {
            return Types.OTHER;
        }

        String fixed = jdbcTypeName.toUpperCase();
        if (fixed.contains("BIGINT")) {
            return Types.BIGINT;
        } else if (fixed.contains("TINYINT")) {
            return Types.TINYINT;
        } else if (fixed.contains("LONGVARBINARY")) {
            return Types.LONGVARBINARY;
        } else if (fixed.contains("VARBINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("LONGVARCHAR")) {
            return Types.LONGVARCHAR;
        } else if (fixed.contains("SMALLINT")) {
            return Types.SMALLINT;
        } else if (fixed.contains("DATETIME")) {
            return Types.TIMESTAMP;
        } else if (fixed.equals("DATE") && "Oracle".equals(databaseType)) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("NUMBER")) {
            return Types.DECIMAL;
        } else if (fixed.contains("BOOLEAN")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("BINARY")) {
            return Types.VARBINARY;
        } else if (fixed.contains("BIT")) {
            return Types.BIT;
        } else if (fixed.contains("BOOL")) {
            return Types.BOOLEAN;
        } else if (fixed.contains("DATE")) {
            return Types.DATE;
        } else if (fixed.contains("TIMESTAMP")) {
            return Types.TIMESTAMP;
        } else if (fixed.contains("TIME")) {
            return Types.TIME;
        } else if (!fixed.contains("REAL") && !fixed.contains("NUMBER")) {
            if (fixed.contains("FLOAT")) {
                return Types.FLOAT;
            } else if (fixed.contains("DOUBLE")) {
                return Types.DOUBLE;
            } else if ("CHAR".equals(fixed)) {
                return Types.CHAR;
            } else if (fixed.contains("INT")) {
                return Types.INTEGER;
            } else if (fixed.contains("DECIMAL")) {
                return Types.DECIMAL;
            } else if (fixed.contains("NUMERIC")) {
                return Types.NUMERIC;
            } else if (!fixed.contains("CHAR") && !fixed.contains("TEXT")) {
                if (fixed.contains("BLOB")) {
                    return Types.BLOB;
                } else if (fixed.contains("CLOB")) {
                    return Types.CLOB;
                } else {
                    return fixed.contains("REFERENCE") ? Types.REF : Types.OTHER;
                }
            } else {
                return Types.VARCHAR;
            }
        } else {
            return Types.REAL;
        }

    }


    public static String removeShortestIndentation(String code) {
        // 分割代码为数组行
        String[] lines = code.split("\n");
        // 找出最少的缩进量（除空白行）
        String[] array = Arrays.stream(lines).filter(el -> !el.isEmpty()).toArray(String[]::new);
        int shortestIndentation = findShortestIndentation(array);
        // 构建新的代码字符串
        StringBuilder newCode = new StringBuilder();

        for (String line : lines) {
            // 移除每行的最少缩进量
            newCode.append(removeIndentation(line, shortestIndentation));
            // 添加换行符，除了最后一行
            if (!line.equals(lines[lines.length - 1])) {
                newCode.append("\n");
            }
        }

        return newCode.toString();
    }

    private static int findShortestIndentation(String[] lines) {
        int shortest = Integer.MAX_VALUE;
        for (String line : lines) {
            int indentation = getIndentation(line);
            if (indentation < shortest) {
                shortest = indentation;
            }
        }
        return shortest;
    }

    private static int getIndentation(String line) {
        // 查找非空白字符前的空白字符数量
        int indentation = 0;
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                break;
            }
            indentation++;
        }
        return indentation;
    }

    private static String removeIndentation(String line, int indentation) {
        // 移除最少的缩进量
        return (line.isEmpty()) ? line : line.substring(indentation);
    }


    public static long generateSerializationId(String className, String[] fields, String[] methods) {
        // Combine class name, fields, and methods to create a unique string
        String uniqueString = className + String.join("", fields) + String.join("", methods);
        // Use SHA-256 hash function to generate a fixed-size ID
        byte[] hashBytes = hashBytes(uniqueString, "SHA-256");
        // Convert the byte array to a long
        return byteArrayToLong(hashBytes);
    }

    private static byte[] hashBytes(String inputString, String algorithm) {
        try {
            // Use the specified hash algorithm
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(inputString.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long byteArrayToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Math.min(bytes.length, 8); i++) {
            result = (result << 8) | (bytes[i] & 0xFF);
        }
        return result;
    }


}
