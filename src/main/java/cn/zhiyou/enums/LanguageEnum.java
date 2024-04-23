package cn.zhiyou.enums;

import com.intellij.json.json5.Json5Language;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.properties.PropertiesLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.sh.ShLanguage;
import com.intellij.sql.psi.SqlLanguage;
import org.intellij.plugins.markdown.lang.MarkdownLanguage;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.jetbrains.yaml.YAMLLanguage;

import java.util.Objects;

/**
 * @author wcp
 * @since 2024/4/9
 */
public enum LanguageEnum {
    Text(PlainTextLanguage.INSTANCE, null),
    Java(JavaLanguage.INSTANCE, "com.intellij.java"),
    Json(Json5Language.INSTANCE, null),
    Kotlin(KotlinLanguage.INSTANCE, "org.jetbrains.kotlin"),
    Sql(SqlLanguage.INSTANCE, "com.intellij.database"),
    Markdown(MarkdownLanguage.INSTANCE, "org.intellij.plugins.markdown"),
    Xml(XMLLanguage.INSTANCE, null),
    Html(HTMLLanguage.INSTANCE, null),
    JavaScript(JavascriptLanguage.INSTANCE, "JavaScript"),
    Shell(ShLanguage.INSTANCE, "com.jetbrains.sh"),
    Properties(PropertiesLanguage.INSTANCE, "com.intellij.properties"),
    Yaml(YAMLLanguage.INSTANCE, "org.jetbrains.plugins.yaml"),
    Groovy(GroovyLanguage.INSTANCE, "org.intellij.groovy");

    private final Language language;
    private final String pluginId;

    LanguageEnum(Language language, String pluginId) {
        this.language = language;
        this.pluginId = pluginId;
    }

    public Language getLanguage() {
        return language;
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
