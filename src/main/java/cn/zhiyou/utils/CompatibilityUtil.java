package cn.zhiyou.utils;

import cn.hutool.core.util.StrUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;

import java.util.List;

/**
 * @author wcp
 * @since 2024/5/6
 */
public class CompatibilityUtil {

    public static final int version_231 = 231;

    public static void speedSearchInstallOn(JBList<?> jbList) {
        // 231及以下用构造方法
        // int ideaVersion = getIdeaVersion();
        // if (ideaVersion <= version_231) {
            new ListSpeedSearch<>(jbList);
        // } else {
        //     ListSpeedSearch.installOn(jbList);
        // }
    }


    public static void speedSearchInstallOn(JBTable table) {
        // 231及以下用构造方法
        // int ideaVersion = getIdeaVersion();
        // if (ideaVersion <= version_231) {
            new TableSpeedSearch(table);
        // } else {
        //     TableSpeedSearch.installOn(table);
        // }
    }


    public static void speedSearchInstallOn(Tree tree) {
        // 231及以下用构造方法
        // int ideaVersion = getIdeaVersion();
        // if (ideaVersion <= version_231) {
            new TreeSpeedSearch(tree);
        // } else {
        //     TreeSpeedSearch.installOn(tree);
        // }
    }


    public static int getIdeaVersion() {
        // 可视为IDEA版本
        try {
            PluginId pluginId = PluginId.getId("org.jetbrains.plugins.terminal");
            IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(pluginId);

            if (null != plugin) {
                String version = plugin.getVersion();
                List<String> list = StrUtil.splitTrim(version, ".");
                version = list.get(0);
                return Integer.parseInt(version);
            }
        } catch (Exception ignored) {

        }

        return 0;
    }

}
