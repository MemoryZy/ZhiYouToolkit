package cn.zhiyou.ui.basic;

import com.intellij.concurrency.JobLauncher;
import com.intellij.concurrency.JobScheduler;
import com.intellij.ide.ui.laf.darcula.DarculaUIUtil;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBInsets;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author wcp
 * @since 2024/1/26
 */
public class TextFieldErrorPopupDecorator {

    private JComponent myTextField;
    private JRootPane rootPane;
    private RelativePoint myErrorShowPoint;
    private AbstractPopup myErrorPopup;
    private Dimension hintSize;

    private RelativePoint myWindowErrorShowPoint;
    private AbstractPopup myWindowErrorPopup;
    private Dimension windowHintSize;


    public TextFieldErrorPopupDecorator(JRootPane rootPane, JComponent myTextField) {
        this.myTextField = myTextField;
        this.rootPane = rootPane;
        // 初始化监听事件
        initElement(rootPane, myTextField);
        myErrorShowPoint = new RelativePoint(myTextField, new Point(0, myTextField.getHeight()));
        myWindowErrorShowPoint = new RelativePoint(rootPane, new Point(0, rootPane.getHeight()));
    }

    public TextFieldErrorPopupDecorator(JRootPane rootPane) {
        this.rootPane = rootPane;
        // 初始化监听事件
        initElement(rootPane, null);
        myWindowErrorShowPoint = new RelativePoint(rootPane, new Point(0, rootPane.getHeight()));
    }

    public TextFieldErrorPopupDecorator(JRootPane rootPane, JList jList) {
        this.myTextField = jList;
        this.rootPane = rootPane;
        // 初始化监听事件
        initElement(rootPane, jList);
    }


    private void initElement(JRootPane rootPane, JComponent myTextField) {
        // // todo 这个作用是组合边框
        // Border border = JBUI.Borders.customLine(JBUI.CurrentTheme.NewClassDialog.bordersColor(), 1, 1, 1, 1);
        // Border border;
        // if (myTextField instanceof EditorTextField) {
        //     border = JBUI.Borders.customLine(JBColor.lightGray, 0, 0, 0, 0);
        // } else {
        //     border = JBUI.Borders.customLine(JBColor.lightGray, 1, 1, 1, 1);
        // }
        //
        // Border oriBorder = myTextField.getBorder();
        // if (oriBorder == null) {
        //     oriBorder = border;
        //     myTextField.setBorder(border);
        // }
        //
        // Border errorBorder = new ErrorBorder(oriBorder);
        // myTextField.setBorder(JBUI.Borders.merge(border, errorBorder, false));
        // myTextField.setBackground(JBUI.CurrentTheme.NewClassDialog.searchFieldBackground());
        // myTextField.putClientProperty("StatusVisibleFunction", (BooleanFunction<JTextField>) field -> field.getText().isEmpty());

        // 注册组件移动事件
        rootPane.addAncestorListener(new AncestorListenerAdapter() {
            /**
             * 弹窗移动时事件
             */
            @Override
            public void ancestorMoved(AncestorEvent event) {
                // 弹出提示-跟随移动
                if (myTextField != null) {
                    if (myErrorPopup != null) {
                        // 永远在输入框上方的位置
                        Insets insets = myTextField.getInsets();
                        Point point = new Point(0, insets.top - JBUIScale.scale(6) - hintSize.height);
                        RelativePoint relativePoint = new RelativePoint(myTextField, point);
                        myErrorPopup.setLocation(relativePoint);
                    }
                }

                if (myWindowErrorPopup != null) {
                    Insets insets = rootPane.getInsets();
                    int y = insets.top - JBUIScale.scale(6) - windowHintSize.height + 40;
                    Point point = new Point(7, y);
                    RelativePoint relativePoint = new RelativePoint(rootPane, point);
                    myWindowErrorPopup.setLocation(relativePoint);
                }
            }
        });

        rootPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                disposePopup();
            }
        });
    }

    public void setError(String error) {
        // 这行的作用是给文本框外部变为红色
        if (myTextField instanceof JList) {
            Border lineBorder = BorderFactory.createLineBorder(JBColor.red, 2);
            myTextField.setBorder(lineBorder);
        } else {
            myTextField.putClientProperty("JComponent.outline", error != null ? "error" : null);
        }

        if (myErrorPopup != null && !myErrorPopup.isDisposed()) Disposer.dispose(myErrorPopup);
        if (error == null) return;

        // 聚焦文本框（代替复合边框的方案）
        myTextField.requestFocusInWindow();

        ComponentPopupBuilder popupBuilder = ComponentValidator.createPopupBuilder(new ValidationInfo(error, myTextField), errorHint -> {
                    Insets insets = myTextField.getInsets();
                    hintSize = errorHint.getPreferredSize();
                    int y = insets.top - JBUIScale.scale(6) - hintSize.height;
                    Point point = new Point(2, y);
                    myErrorShowPoint = new RelativePoint(myTextField, point);
                }).setCancelOnWindowDeactivation(false)
                .setCancelOnClickOutside(true)
                .setRequestFocus(true)
                .addUserData("SIMPLE_WINDOW");

        myErrorPopup = (AbstractPopup) popupBuilder.createPopup();
        myErrorPopup.show(myErrorShowPoint);
    }

    public void setWindowError(String error) {
        rootPane.putClientProperty("JComponent.outline", error != null ? "error" : null);
        if (myWindowErrorPopup != null && !myWindowErrorPopup.isDisposed()) Disposer.dispose(myWindowErrorPopup);
        if (error == null) return;

        //　　　　
        //

        error = "　　　　" + error + "　　　　";

        rootPane.setBorder(BorderFactory.createLineBorder(JBColor.red));

        ComponentPopupBuilder popupBuilder = ComponentValidator.createPopupBuilder(new ValidationInfo(error, rootPane), errorHint -> {
                    Insets insets = rootPane.getInsets();
                    windowHintSize = errorHint.getPreferredSize();
                    int y = insets.top - JBUIScale.scale(6) - windowHintSize.height + 40;
                    Point point = new Point(7, y);
                    myWindowErrorShowPoint = new RelativePoint(rootPane, point);
                }).setCancelOnWindowDeactivation(false)
                .setCancelOnClickOutside(true)
                .setRequestFocus(true)
                .addUserData("SIMPLE_WINDOW");

        myWindowErrorPopup = (AbstractPopup) popupBuilder.createPopup();
        myWindowErrorPopup.show(myWindowErrorShowPoint);
    }


    private void disposePopup() {
        if (myErrorPopup != null && !myErrorPopup.isDisposed()) Disposer.dispose(myErrorPopup);
        if (myWindowErrorPopup != null && !myWindowErrorPopup.isDisposed()) Disposer.dispose(myWindowErrorPopup);
    }


    /**
     * 只会在组件的error和waring状态下绘制
     */
    private static final class ErrorBorder implements Border {
        private final Border errorDelegateBorder;

        private ErrorBorder(Border delegate) {
            errorDelegateBorder = delegate;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (checkError(c)) {
                errorDelegateBorder.paintBorder(c, g, x, y, width, height);
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return checkError(c) ? errorDelegateBorder.getBorderInsets(c) : JBInsets.create(1,1);/*JBInsets.emptyInsets()*/
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        private static boolean checkError(Component c) {
            Object outlineObj = ((JComponent) c).getClientProperty("JComponent.outline");
            if (outlineObj == null) return false;

            DarculaUIUtil.Outline outline = outlineObj instanceof DarculaUIUtil.Outline
                    ? (DarculaUIUtil.Outline) outlineObj : DarculaUIUtil.Outline.valueOf(outlineObj.toString());
            return outline == DarculaUIUtil.Outline.error || outline == DarculaUIUtil.Outline.warning;
        }
    }

}
