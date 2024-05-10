package cn.zhiyou.ui;

import cn.zhiyou.utils.CompatibilityUtil;
import com.intellij.database.model.DasDataSource;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author wcp
 * @since 2024/3/22
 */
public class DasDataBaseChangeDialog extends DialogWrapper {
    private JBList<String> showList;
    private DefaultListModel<String> defaultListModel;
    private final List<DasDataSource> dasDataSourceList;

    public DasDataBaseChangeDialog(@Nullable Project project, List<DasDataSource> dasDataSourceList) {
        super(project, true);
        this.dasDataSourceList = dasDataSourceList;

        setOKButtonText("选定");
        setCancelButtonText("取消");
        setTitle("选择数据源");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        defaultListModel = new DefaultListModel<>();
        for (DasDataSource dasDataSource : dasDataSourceList) {
            defaultListModel.addElement(dasDataSource.getName());
        }

        showList = new JBList<>(defaultListModel);
        // 触发快速查找
        CompatibilityUtil.speedSearchInstallOn(showList);

        showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showList.setSelectedIndex(0);

        JBScrollPane scrollPane = new JBScrollPane(showList) {
            @Override
            public Dimension getPreferredSize() {
                Dimension preferredSize = super.getPreferredSize();
                if (!isPreferredSizeSet()) {
                    setPreferredSize(new Dimension(0, preferredSize.height));
                }
                return preferredSize;
            }
        };

        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setViewportBorder(JBUI.Borders.empty());
        scrollPane.setPreferredSize(new Dimension(350, 250));

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(scrollPane, BorderLayout.CENTER);
        jPanel.setBorder(IdeBorderFactory.createBorder(SideBorder.ALL));

        return jPanel;
    }


    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            if (executeOkAction()) {
                applyFields();
                close(OK_EXIT_CODE);
            }
        }
    }

    private boolean executeOkAction() {
        int selectedIndex = showList.getSelectedIndex();
        return (selectedIndex != -1) && (selectedIndex < defaultListModel.getSize());
    }

    public int getSelectIndex() {
        return showList.getSelectedIndex();
    }

}
