package cn.zhiyou.enums;

import java.util.Objects;

/**
 * @author Memory
 * @since 2024/4/9
 */
public enum LanguageEnum {
    Text("com.intellij.openapi.fileTypes.PlainTextLanguage", null),
    Java("com.intellij.lang.java.JavaLanguage", "com.intellij.java"),
    Json("com.intellij.json.json5.Json5Language", null),
    Xml("com.intellij.lang.xml.XMLLanguage", null),
    Html("com.intellij.lang.html.HTMLLanguage", null),

    // ------------ 以下是导入的依赖
    Kotlin("org.jetbrains.kotlin.idea.KotlinLanguage", "org.jetbrains.kotlin"),
    Sql("com.intellij.sql.psi.SqlLanguage", "com.intellij.database"),
    JavaScript("com.intellij.lang.javascript.JavascriptLanguage", "JavaScript"),
    Shell("com.intellij.sh.ShLanguage", "com.jetbrains.sh"),

    // Markdown(MarkdownLanguage.INSTANCE, "org.intellij.plugins.markdown"),
    // Properties(PropertiesLanguage.INSTANCE, "com.intellij.properties"),
    // Yaml(YAMLLanguage.INSTANCE, "org.jetbrains.plugins.yaml"),
    // Groovy(GroovyLanguage.INSTANCE, "org.intellij.groovy");

    ;

    private final String languageClassQualifiedName;
    private final String pluginId;

    LanguageEnum(String languageClassQualifiedName, String pluginId) {
        this.languageClassQualifiedName = languageClassQualifiedName;
        this.pluginId = pluginId;
    }

    public String getLanguageClassQualifiedName() {
        return languageClassQualifiedName;
    }

    public String getPluginId() {
        return pluginId;
    }

    public static LanguageEnum of(String name) {
        for (LanguageEnum value : LanguageEnum.values()) {
            if (Objects.equals(value.name(), name)) {
                return value;
            }
        }
        return null;
    }

}
