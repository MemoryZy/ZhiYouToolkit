package cn.zhiyou.ui;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.ui.extension.FillExtension;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.fields.ExtendableTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wcp
 * @since 2024/1/19
 */
public class ConvertTimestampWindow extends DialogWrapper {
    private JPanel rootPanel;
    private ExtendableTextField timestampTextField;
    private ExtendableTextField timeTextField;
    private final TextFieldErrorPopupDecorator timestampErrorPopupDecorator;
    private final TextFieldErrorPopupDecorator timeErrorPopupDecorator;

    public ConvertTimestampWindow(@Nullable Project project) {
        super(project, true);
        this.timestampErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), timestampTextField);
        this.timeErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), timeTextField);

        setModal(false);
        setTitle("时间戳转换");

        setOKButtonText("时间戳转时间");
        setCancelButtonText("取消");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        String timestamp = StrUtil.trimToNull(timestampTextField.getText());
        if (StrUtil.isBlank(timestamp)) {
            timestampErrorPopupDecorator.setError("时间戳内容为空");
            return;
        }

        // 时间戳位数
        int length = timestamp.length();
        if (length == 10) {
            timestamp = timestamp + "000";
        } else if (length != 13) {
            timestampErrorPopupDecorator.setError("时间戳位数错误");
            return;
        }

        long timestampLong = Long.parseLong(timestamp);
        DateTime date = DateUtil.date(timestampLong);
        String format = DateUtil.format(date, DatePattern.NORM_DATETIME_MS_PATTERN);

        timeTextField.setText(format);
    }


    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        actions.add(new MyChangeAction());
        actions.add(getCancelAction());
        return actions.toArray(new Action[0]);
    }


    private void createUIComponents() {
        timestampTextField = new ExtendableTextField(20);
        timestampTextField.addExtension(new FillExtension(timestampTextField, false));
        timeTextField = new ExtendableTextField(20);
        timeTextField.addExtension(new FillExtension(timeTextField, true));
    }


    private class MyChangeAction extends DialogWrapperExitAction {
        public MyChangeAction() {
            super("时间转时间戳", 2);
        }

        @Override
        protected void doAction(ActionEvent event) {
            String time = StrUtil.trimToNull(timeTextField.getText());
            // 时间转时间戳
            if (StrUtil.isBlank(time)) {
                timeErrorPopupDecorator.setError("时间内容为空");
                return;
            }

            Date date;
            try {
                date = DateUtil.parse(time, CommonUtil.DATE_PATTERN);
            } catch (Exception e) {
                timeErrorPopupDecorator.setError("时间格式错误");
                return;
            }

            timestampTextField.setText(String.valueOf(date.getTime()));
        }
    }

}
