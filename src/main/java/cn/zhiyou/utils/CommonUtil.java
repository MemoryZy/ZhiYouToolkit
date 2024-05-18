package cn.zhiyou.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wcp
 * @since 2023/11/27
 */
public class CommonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String[] DATE_PATTERN = {
            DatePattern.NORM_MONTH_PATTERN,
            DatePattern.SIMPLE_MONTH_PATTERN,
            DatePattern.NORM_DATE_PATTERN,
            DatePattern.NORM_DATETIME_MINUTE_PATTERN,
            DatePattern.NORM_DATETIME_PATTERN,
            DatePattern.NORM_DATETIME_MS_PATTERN,
            DatePattern.ISO8601_PATTERN,
            DatePattern.CHINESE_DATE_PATTERN,
            DatePattern.CHINESE_DATE_TIME_PATTERN,
            DatePattern.PURE_DATE_PATTERN,
            DatePattern.PURE_DATETIME_PATTERN,
            DatePattern.PURE_DATETIME_MS_PATTERN,
            "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss.SSS",
    };

    /**
     * 驼峰、空格转下划线
     *
     * @param text 文本
     * @return 转换后的文本
     */
    public static String toSnakeCase(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        String[] split = text.split(" ");
        if (split.length > 1) {
            return blankToSnakeCase(text);
        }

        return NamingCase.toUnderlineCase(text);
    }


    /**
     * 下划线、空格转驼峰
     *
     * @param text 文本
     * @return 转换后的文本
     */
    public static String toCamel(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        String[] split = text.split(" ");
        if (split.length > 1) {
            text = blankToSnakeCase(text);
        }

        return NamingCase.toCamelCase(text);
    }


    /**
     * 空白分隔转下划线
     *
     * @param text 文本
     * @return 转换后的文本
     */
    public static String blankToSnakeCase(String text) {
        // 替换开头的空格
        text = text.replaceAll("^\\s+", "_");
        // 替换结尾的空格
        text = text.replaceAll("\\s+$", "_");
        // 替换所有空格
        text = text.replaceAll("\\s+", "_");

        return text;
    }


    /**
     * 获取字符串前面缩进量
     *
     * @param text 字符串
     * @return 缩进量
     */
    public static int getIndentation(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (!Character.isSpaceChar(c)) {
                break;
            }

            count++;
        }

        return count;
    }

    /**
     * 获取字符串前的所有空白
     *
     * @param text 文本
     * @return 空白长度
     */
    public static String startBlank(String text) {
        return fillBlank(getIndentation(text));
    }


    /**
     * 填充空白
     *
     * @param count 空白字符数量
     * @return 空白
     */
    public static String fillBlank(int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, (char) 32);
        return String.valueOf(chars);
    }


    /**
     * 将字符串进行三种方式的比较
     *
     * @param v1 字符串1
     * @param v2 字符串2
     * @return 比较结果 -> true：相同；false：不相同
     */
    public static boolean matchCase(String v1, String v2) {
        // 名称匹配（不区分大小写）
        return StringUtils.equalsIgnoreCase(v1, v2)
                // 下划线匹配
                || StringUtils.equalsIgnoreCase(toSnakeCase(v1), toSnakeCase(v2))
                // 驼峰匹配
                || StringUtils.equalsIgnoreCase(toCamel(v1), toCamel(v2));
    }


    /**
     * 将全限定名转换为单独类名
     *
     * @param qualifiedName 全限定名
     * @return 类名
     */
    public static String qualifiedNameToClassName(String qualifiedName) {
        List<String> list = StrUtil.split(qualifiedName, '.');
        return list.get(list.size() - 1);
    }


    /**
     * 获取包名
     *
     * @param qualifiedName 完整的类名
     * @return 包名
     */
    public static String getPackageName(String qualifiedName) {
        return removeLastElement(qualifiedName, '.');
    }


    public static String removeLastElement(String content, char separator) {
        List<String> list = StrUtil.splitTrim(content, separator);
        return StrUtil.join(separator + "", list.subList(0, list.size() - 1));
    }

    /**
     * 判断字符串是否包含中文
     *
     * @param str 待判断的字符串
     * @return 如果字符串包含中文，返回true；否则返回false
     */
    public static boolean containsChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 定义一个包含中文字符的正则表达式
        String regex = "[\\u4E00-\\u9FFF\\u3400-\\u4DBF\\uF900-\\uFAFF]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.find();
    }


    /**
     * 将字符串转换为拼音
     *
     * @param src            要转换的字符串
     * @param fullSpell      是否为全拼
     * @param phoneticSymbol 是否需要音调符号
     * @return 转换后的拼音字符串
     */
    public static String toPinyin(String src, boolean fullSpell, boolean phoneticSymbol) {
        // 1.如果是空字符串,则不处理.
        if (StrUtil.isBlank(src)) {
            return null;
        }

        // 针对Pinyin4j 做出配置,就是针对lu的lv的配置 ->使用v来配置,
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        if (phoneticSymbol) {
            format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
            format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        } else {
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
        }

        // 2.如果字符串非空
        // 遍历字符串的每个字符串,针对每个字符串进行转换,把转换得到的结果,拼接到StringBuilder里面
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            String[] tmp = null;
            try {
                tmp = PinyinHelper.toHanyuPinyinStringArray(ch, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 忽略
            }
            if (ArrayUtil.isEmpty(tmp)) {
                // 如果是空的字符,就说明转换失败了.
                // 如果输入的字符,没有汉语拼音,自然就会转换失败.
                // 保留原始字符,加入结果中
                builder.append(ch);
            } else if (fullSpell) {
                String tmpStr = tmp[0];
                // 拼音结果为true，首字母转大写
                builder.append((tmpStr.length() == 1) ? tmpStr : StrUtil.upperFirst(tmp[0]));
            } else {
                // 拼音结果为false
                // 比如 卡 =["ka","qia"] 此时取0号元素,得到了"ka",再取0号字符
                builder.append(tmp[0].charAt(0));
            }
        }

        return builder.toString();
    }


    /**
     * 获取当前日期并返回格式化后的字符串
     *
     * @return 格式化后的当前日期字符串，格式为"yyyy/MM/dd"
     */
    public static String getDateNow() {
        return DateUtil.format(new Date(), "yyyy/MM/dd");
    }


    /**
     * 获取当前时间并格式化
     *
     * @return 格式化后的当前时间字符串，格式为"yyyy/MM/dd HH:mm:ss"
     */
    public static String getNow() {
        return DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss");
    }


    /**
     * 从给定的字符串中提取JSON字符串
     *
     * @param includeJsonStr 包含JSON字符串的字符串
     * @return 提取的JSON字符串，如果给定的字符串为空或null，则返回空字符串
     */
    public static String extractJsonStr(String includeJsonStr) {
        if (StrUtil.isBlank(includeJsonStr)) {
            return "";
        }

        String json = extractJsonString(includeJsonStr);
        // 判断是否是JSON字符串
        return isJson(json) ? json : "";
    }

    /**
     * 从给定的字符串中提取JSON字符串
     *
     * @param includeJsonStr 包含JSON字符串的字符串
     * @return 提取的JSON字符串，如果给定的字符串为空或null，则返回空字符串
     */
    private static String extractJsonString(String includeJsonStr) {
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < includeJsonStr.length(); i++) {
            if (includeJsonStr.charAt(i) == '{' || includeJsonStr.charAt(i) == '[') {
                startIndex = i;
                break;
            }
        }
        if (startIndex == -1) {
            return ""; // 没有找到 JSON 字符串
        }

        int count = 1; // 计数器，用于匹配 {} 或 []
        for (int i = startIndex + 1; i < includeJsonStr.length(); i++) {
            char c = includeJsonStr.charAt(i);
            if (c == '{' || c == '[') {
                count++;
            } else if (c == '}' || c == ']') {
                count--;
                if (count == 0) {
                    endIndex = i;
                    break;
                }
            }
        }

        if (endIndex == -1) {
            return ""; // JSON 字符串不完整
        }

        return includeJsonStr.substring(startIndex, endIndex + 1);
    }

    public static String formatJson(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object jsonObj = gson.fromJson(json, Object.class);
            return gson.toJson(jsonObj);
        } catch (Exception e) {
            return "[]";
        }
    }


    public static boolean isJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public static boolean isJsonArray(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode instanceof ArrayNode;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public static boolean isJsonObject(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode instanceof ObjectNode;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public static boolean isXML(String text) {
        try {
            XmlUtil.parseXml(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String jsonToXml(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json.getBytes());
            XmlMapper xmlMapper = new XmlMapper();
            String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

            StringBuilder builder = new StringBuilder(StrUtil.trim(xml))
                    .replace(0, 12, "<root>");
            builder.replace(builder.length() - 13, builder.length(), "</root>");
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String xmlToJson(String xml) {
        try {
            ObjectMapper xmlMapper = new XmlMapper();
            JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 将Json压缩成一行
     *
     * @param jsonStr json字符串
     * @return 压缩json
     * @throws JsonProcessingException 非法Json
     */
    public static String compressJson(String jsonStr) throws JsonProcessingException {
        return objectMapper.writeValueAsString(objectMapper.readTree(jsonStr));
    }


    /**
     * 【中文转Unicode】
     *
     * @param str
     * @return 返回转码后的字符串 - Unicode格式
     */
    public static String chineseToUnicode(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            // 汉字范围 \u4e00 - \u9fa5 (中文)
            if (chr1 >= 19968 && chr1 <= 171941) {
                result.append("\\u").append(Integer.toHexString(chr1));
            } else {
                result.append(str.charAt(i));
            }
        }
        return result.toString();
    }

    /**
     * 【判断是否为中文字符】
     *
     * @param c
     * @return 返回判断结果 - boolean类型
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 【Unicode转中文】
     *
     * @param unicode
     * @return 返回转码后的字符串 - 中文格式
     */
    public static String unicodeToChinese(final String unicode) {
        StringBuilder string = new StringBuilder();
        String[] hex = unicode.split("\\\\u");
        for (String s : hex) {
            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if (s.length() >= 4) {// 取前四个，判断是否是汉字
                    String chinese = s.substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = isChinese((char) chr);
                        // 转化成功，判断是否在  汉字范围内
                        if (isChinese) {// 在汉字范围内
                            // 追加成string
                            string.append((char) chr);
                            // 并且追加  后面的字符
                            String behindString = s.substring(4);
                            string.append(behindString);
                        } else {
                            string.append(s);
                        }
                    } catch (NumberFormatException e1) {
                        string.append(s);
                    }
                } else {
                    string.append(s);
                }
            } catch (NumberFormatException e) {
                string.append(s);
            }
        }
        return string.toString();
    }

}
