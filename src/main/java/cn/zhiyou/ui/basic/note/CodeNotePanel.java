package cn.zhiyou.ui.basic.note;

import cn.zhiyou.ui.CodeNoteWindow;

import javax.swing.*;
import java.awt.*;

/**
 * @author wcp
 * @since 2024/3/8
 */
public class CodeNotePanel extends JPanel {

    private final CodeNoteWindow codeNoteWindow;

    public CodeNotePanel(LayoutManager layout, CodeNoteWindow codeNoteWindow) {
        super(layout);
        this.codeNoteWindow = codeNoteWindow;
    }

    public CodeNoteWindow getCodeNoteWindow() {
        return codeNoteWindow;
    }

}
