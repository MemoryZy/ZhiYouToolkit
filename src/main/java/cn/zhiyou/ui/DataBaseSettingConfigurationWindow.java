package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.zhiyou.config.DataBaseSetting;
import cn.zhiyou.constant.Icons;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CompatibilityUtil;
import cn.zhiyou.utils.PopupUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.GotItTooltip;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Memory
 * @since 2023/12/18
 */
public class DataBaseSettingConfigurationWindow {
    private JPanel rootPanel;
    private JBTextField hostTf;
    private JBTextField portTf;
    private JBTextField userTf;
    private JBPasswordField passPf;
    private JBTextField dbTf;
    private ExpandableTextField urlTf;
    private ComboBox<String> driverCb;
    private ExpandableTextField pmTf;
    private ActionLink tcBtn;
    private JPanel mainPanel;

    public static final String[] drivers = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"};

    private static final String constant = "jdbc:mysql://{}:{}{}{}";

    public DataBaseSettingConfigurationWindow(String host,
                                              String port,
                                              String user,
                                              String dataBase,
                                              String url,
                                              String driver) {

        if (CompatibilityUtil.existDatabasePlugin()) {
            ActionUtil.showGotItTip(
                    "zhiyou.dbTool.exist.id",
                    "Warning",
                    "由于您的IDEA中已存在Database工具插件，\n建议优先使用Database的入口进行数据库配置及后续的文件生成。",
                    "我知道了",
                    Icons.waring,
                    mainPanel,
                    GotItTooltip.BOTTOM_MIDDLE);
        }

        passPf.setPasswordIsStored(true);
        for (String driverCon : drivers) {
            driverCb.addItem(driverCon);
        }

        hostTf.setText((StrUtil.isNotBlank(host)) ? host : "localhost");
        portTf.setText((StrUtil.isNotBlank(port)) ? port : "3306");
        userTf.setText((StrUtil.isNotBlank(user)) ? user : "");
        // passPf.setText((StrUtil.isNotBlank(pass)) ? pass : "");
        // passPf.setText("");
        dbTf.setText((StrUtil.isNotBlank(dataBase)) ? dataBase : "");
        // 这里要判断驱动
        urlTf.setText((StrUtil.isNotBlank(url)) ? url : StrUtil.format(constant, "localhost", "3306", "", ""));
        driverCb.setSelectedItem((StrUtil.isNotBlank(driver)) ? driver : drivers[0]);

        pmTf.getEmptyText().setText("characterEncoding=utf8&useSSL=false");

        init();
    }


    private void init() {
        urlTf.setEditable(false);

        // 监听host、port、dataBase输入框的输入
        hostTf.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateUrlTextField();
            }
        });

        portTf.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateUrlTextField();
            }
        });

        dbTf.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateUrlTextField();
            }
        });

        pmTf.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                updateUrlTextField();
            }
        });

        tcBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = getUrl();
                String user = getUser();
                String pass = getPass();
                String driver = getDriver();

                if (StrUtil.isBlank(pass)) {
                    pass = DataBaseSetting.getInstance().pass;
                }

                MessageType messageType;
                String resultHeader, message;
                try (SimpleDataSource simpleDataSource = new SimpleDataSource(url, user, pass, driver)) {
                    simpleDataSource.getConnection();

                    messageType = MessageType.INFO;
                    resultHeader = "Succeeded";
                    message = "The connection is successful and can be used freely.";
                } catch (SQLException ex) {
                    messageType = MessageType.ERROR;
                    resultHeader = "Failed";
                    message = ex.getMessage();
                }

                RelativePoint relativePoint = PopupUtil.calculateBelowPoint(tcBtn);
                PopupUtil.showHTmlTextBalloon(message, messageType, relativePoint, Balloon.Position.below);
            }
        });
    }

    private void updateUrlTextField() {
        String hostText = StrUtil.trimToEmpty(hostTf.getText());
        String portText = StrUtil.trimToEmpty(portTf.getText());
        String dbText = StrUtil.trimToEmpty(dbTf.getText());
        String pmText = pmTf.getText();

        String db = "";
        if (StrUtil.isNotBlank(dbText)) {
            if (dbText.startsWith("/")) {
                db = dbText;
            } else {
                db = "/" + dbText;
            }
        }

        String pm = "";
        if (StrUtil.isNotBlank(pmText)) {
            if (pmText.startsWith("?")) {
                pm = pmText;
            } else {
                pm = "?" + pmText;
            }
        }

        urlTf.setText(StrUtil.format(constant, hostText, portText, db, pm));
    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getHost() {
        return hostTf.getText();
    }

    public String getPort() {
        return portTf.getText();
    }

    public String getUser() {
        return userTf.getText();
    }

    public String getPass() {
        return new String(passPf.getPassword());
    }

    public String getDataBase() {
        return dbTf.getText();
    }

    public String getUrl() {
        return urlTf.getText();
    }

    public String getDriver() {
        Object selectedItem = driverCb.getSelectedItem();
        return Objects.isNull(selectedItem) ? null : selectedItem.toString();
    }

}
