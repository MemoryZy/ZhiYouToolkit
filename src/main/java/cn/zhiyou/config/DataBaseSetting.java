package cn.zhiyou.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ZhiYouDataBase", storages = {@Storage(value = "ZhiYouDataBase.xml")})
public class DataBaseSetting implements PersistentStateComponent<DataBaseSetting> {

    public static DataBaseSetting getInstance() {
        return ApplicationManager.getApplication().getService(DataBaseSetting.class);
    }

    public String host;
    public String port;
    public String user;
    public String pass;
    public String dataBase;
    public String url;
    public String driver;

    /**
     * IDE获取状态数据，即配置数据，通过XML序列化实现持久化过程
     * 点击OK的时候会执行
     */
    @Override
    public @Nullable DataBaseSetting getState() {
        // 设置完APPID和密钥之后，使其可以直接使用，否则只能重启才可以使用
        // DataBaseUtils.host = this.host;
        // DataBaseUtils.port = this.port;
        // DataBaseUtils.user = this.user;
        // DataBaseUtils.pass = this.pass;
        // DataBaseUtils.dataBase = this.dataBase;
        // DataBaseUtils.url = this.url;
        // DataBaseUtils.driver = this.driver;

        return this;
    }

    /**
     * IDE重启的时候通过XML反序列化实现加载配置数据的过程
     */
    @Override
    public void loadState(@NotNull DataBaseSetting state) {
        this.host = state.host;
        this.port = state.port;
        this.user = state.user;
        this.pass = state.pass;
        this.dataBase = state.dataBase;
        this.url = state.url;
        this.driver = state.driver;
    }
}