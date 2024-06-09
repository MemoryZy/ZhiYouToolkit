package cn.zhiyou.constant;

/**
 * @author wcp
 * @since 2024/2/23
 */
public class ZhiYouConstant {

    public static final String mapperNotify = """
            输入SQL字段: {}，有效属性: {}，未匹配属性: {}
            {}
            """;

    public static final String[] MYSQL_DEFAULT_SCHEMAS = {
            "information_schema", "mysql", "performance_schema", "sys"
    };

    public static final String GITHUB_ISSUES_URL = "https://github.com/MemoryZy/ZhiYouToolkit/issues/new?";

    public static final String REPORT_ERROR_TEMPLATE = """
            # Description
            {}
            
            # Information
            > OS: {}
            >
            > IDE Version: {}
            >
            > Plugin Version: {}

            # Stacktrace
            ```
            {}
            ```
            """;

}
