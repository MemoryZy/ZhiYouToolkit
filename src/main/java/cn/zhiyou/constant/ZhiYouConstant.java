package cn.zhiyou.constant;

import java.util.concurrent.LinkedBlockingQueue;

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

    public static final String[] JAVA_COLLECTION = {
            "java.util.List",
            "java.util.ArrayList",
            "java.util.LinkedList",
            "java.util.Vector",
            "java.util.Stack",
            "java.util.CopyOnWriteArrayList",

            "java.util.Set",
            "java.util.HashSet",
            "java.util.LinkedHashSet",
            "java.util.TreeSet",
            "java.util.CopyOnWriteArraySet",
            "java.util.EnumSet",

            "java.util.Queue",
            "java.util.LinkedList",
            "java.util.PriorityQueue",
            "java.util.ArrayDeque",
            "java.util.ConcurrentLinkedQueue",
            "java.util.BlockingQueue",
            "java.util.concurrent.ArrayBlockingQueue",
            "java.util.concurrent.ConcurrentLinkedQueue",
            "java.util.concurrent.ConcurrentSkipListSet",
            "java.util.concurrent.LinkedBlockingQueue",

            "java.util.Map",
            "java.util.HashMap",
            "java.util.LinkedHashMap",
            "java.util.TreeMap",
            "java.util.Hashtable",
            "java.util.WeakHashMap",
            "java.util.IdentityHashMap",
            "java.util.ConcurrentHashMap",
            "java.util.EnumMap",
    };


}
