package cn.zhiyou.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.psi.DataSourceManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Memory
 * @since 2024/5/25
 */
public class DatabaseUtil {

    public static List<DasDataSource> getAllLocalDataSource(Project project) {
        List<DasDataSource> dataSourceList = new ArrayList<>();

        // 前提是用户的idea存在Database插件，否则报错
        if (CompatibilityUtil.existDatabasePlugin()) {
            // 获取数据源管理器
            List<DataSourceManager<?>> managers = DataSourceManager.getManagers(project);
            // 获取所有本地配置的数据源
            if (CollUtil.isNotEmpty(managers)) {
                for (DataSourceManager<?> manager : managers) {
                    List<?> dataSources = manager.getDataSources();
                    if (ArrayUtil.isNotEmpty(dataSources)) {
                        for (Object dataSource : dataSources) {
                            if (dataSource instanceof DasDataSource dasDataSource) {
                                dataSourceList.add(dasDataSource);
                            }
                        }
                    }
                }
            }
        }

        return dataSourceList;
    }

}
