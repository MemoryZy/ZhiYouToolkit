package cn.zhiyou.config;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.ui.DataBaseSettingConfigurationWindow;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class DataBaseSettingConfiguration implements Configurable {

    private final DataBaseSettingConfigurationWindow window;
    private final DataBaseSetting dataBaseSetting;

    /**
     * 构造器，IDE 在初始化我们插件的时候，会实例化拓展点对象，而实例化时只能通过无参构造器创建对象。
     */
    public DataBaseSettingConfiguration() {
        this.dataBaseSetting = DataBaseSetting.getInstance();
        this.window = new DataBaseSettingConfigurationWindow(
                dataBaseSetting.host,
                dataBaseSetting.port,
                dataBaseSetting.user,
                dataBaseSetting.dataBase,
                dataBaseSetting.url,
                dataBaseSetting.driver);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return window.getRootPanel();
    }

    @Override
    public boolean isModified() {
        String host = dataBaseSetting.host;
        String port = dataBaseSetting.port;
        String user = dataBaseSetting.user;
        String pass = dataBaseSetting.pass;
        String dataBase = dataBaseSetting.dataBase;
        String url = dataBaseSetting.url;
        String driver = dataBaseSetting.driver;

        String windowHost = StrUtil.emptyToNull(window.getHost());
        String windowPort = StrUtil.emptyToNull(window.getPort());
        String windowUser = StrUtil.emptyToNull(window.getUser());
        String windowPass = StrUtil.emptyToNull(window.getPass());
        String windowDataBase = StrUtil.emptyToNull(window.getDataBase());
        String windowUrl = StrUtil.emptyToNull(window.getUrl());
        String windowDriver = StrUtil.emptyToNull(window.getDriver());

        // 如果不等于，说明有修改
        return !Objects.equals(host, windowHost)
                || !Objects.equals(port, windowPort)
                || !Objects.equals(user, windowUser)
                // 如果输入框密码为空，则跳过密码的验证
                || (Objects.nonNull(windowPass) && !Objects.equals(pass, windowPass))
                || !Objects.equals(dataBase, windowDataBase)
                || !Objects.equals(url, windowUrl)
                || !Objects.equals(driver, windowDriver);
    }


    /**
     * 当在配置页面点击 apply 或者 ok 按钮时，该方法会被调用
     */
    @Override
    public void apply() {
        // 赋值
        dataBaseSetting.host = StrUtil.trimToNull(window.getHost());
        dataBaseSetting.port = StrUtil.trimToNull(window.getPort());

        // todo 在这里对这个做个加密
        dataBaseSetting.user = StrUtil.trimToNull(window.getUser());

        // 默认是隐藏的，所以按确定时可能
        String newPass = StrUtil.trimToNull(window.getPass());
        if (Objects.nonNull(newPass)) {
            dataBaseSetting.pass = newPass;
        }

        dataBaseSetting.dataBase = StrUtil.trimToNull(window.getDataBase());
        dataBaseSetting.url = StrUtil.trimToNull(window.getUrl());
        dataBaseSetting.driver = StrUtil.trimToNull(window.getDriver());
    }


    @Override
    public String getDisplayName() {
        return "ZhiYouToolkit";
    }
}