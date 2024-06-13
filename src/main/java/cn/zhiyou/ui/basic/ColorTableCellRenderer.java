package cn.zhiyou.ui.basic;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 自定义单元格渲染器
 *
 * @author Memory
 * @since 2024/3/15
 */
public class ColorTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // 设置行的背景色
        if (row % 2 == 0) {
            // 偶数行设置为浅灰色
            c.setBackground(new JBColor(Gray._242, Gray._51));
        } else {
            // 奇数行设置为白色
            c.setBackground(JBColor.WHITE);
        }

        // 如果行被选中，覆盖背景色
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else {
            c.setForeground(table.getForeground());
        }

        return c;
    }
}