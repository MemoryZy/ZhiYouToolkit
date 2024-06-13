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
 * @author Memory
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

        } else if (obj instanceof String str) {
            // 时间类型判断
            return checkJsonDateType(str);

        } else if (obj instanceof List list) {
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
        return switch (qualifiedName) {
            case "boolean", "java.lang.Boolean" -> "BIT";
            case "byte", "java.lang.Byte" -> "TINYINT";
            case "short", "java.lang.Short" -> "SMALLINT";
            case "char", "java.lang.Character" -> "CHAR";
            case "int", "java.lang.Integer" -> "INTEGER";
            case "long", "java.lang.Long" -> "BIGINT";
            case "float", "java.lang.Float" -> "FLOAT";
            case "double", "java.lang.Double" -> "DOUBLE";
            case "java.math.BigDecimal" -> "DECIMAL";
            case "String", "java.lang.String" -> "VARCHAR";
            case "java.util.Date" -> "TIMESTAMP";
            case "java.sql.Date" -> "DATE";
            case "java.sql.Time" -> "TIME";
            case "java.sql.Timestamp" -> "TIMESTAMP";
            case "byte[]" -> "VARBINARY";
            case "java.sql.Blob" -> "BLOB";
            case "java.sql.Clob" -> "CLOB";
            case "java.util.Set" -> "ARRAY";
            case "java.util.Enum" -> "VARCHAR";
            case "java.util.Enum[]" -> "ARRAY";
            case "java.time.Instant" -> "TIMESTAMP";
            case "java.time.LocalDateTime" -> "TIMESTAMP";
            case "java.time.LocalDate" -> "DATE";
            case "java.time.LocalTime" -> "TIME";
            case "java.util.Collection", "java.util.List", "java.util.ArrayList", "java.util.Map", "java.util.HashMap" ->
                    "ARRAY";
            default -> "";
        };
    }


    /**
     * 根据JdbcType和useNewDateApi返回Java类型
     *
     * @param jdbcType      JdbcType枚举类型
     * @param useNewDateApi 是否使用新日期API
     * @return Java类型字符串
     */
    public static String getJavaType(JdbcType jdbcType, boolean useNewDateApi) {
        return switch (jdbcType) {
            case ARRAY -> Array.class.getName();
            case BIT, BOOLEAN -> Boolean.class.getName();
            case TINYINT -> Byte.class.getName();
            case SMALLINT -> Short.class.getName();
            case INTEGER -> Integer.class.getName();
            case BIGINT -> Long.class.getName();
            case FLOAT -> Float.class.getName();
            case DOUBLE -> Double.class.getName();
            case NUMERIC, DECIMAL, REAL -> BigDecimal.class.getName();
            case CHAR, VARCHAR, LONGVARCHAR, NVARCHAR, CLOB, NCLOB, SQLXML, NCHAR -> String.class.getName();
            case DATE -> useNewDateApi ? LocalDate.class.getName() : Date.class.getName();
            case TIME -> useNewDateApi ? LocalTime.class.getName() : Time.class.getName();
            case TIMESTAMP -> useNewDateApi ? LocalDateTime.class.getName() : Date.class.getName();
            // case BINARY, VARBINARY, LONGVARBINARY, BLOB -> Byte[].class.getName();
            case BINARY, VARBINARY, LONGVARBINARY, BLOB -> "Byte[]";
            // Add more cases for other JDBC types as needed...
            default -> Object.class.getName(); // or handle the default case as needed
        };
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
