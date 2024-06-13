package cn.zhiyou.ui.extension;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.ui.scale.JBUIScale;

import javax.swing.*;
import java.util.Date;

/**
 * @author Memory
 * @since 2024/2/7
 */
public class FillExtension implements ExtendableTextComponent.Extension {
    private final ExtendableTextField extendableTextField;
    private final boolean isDate;

    public FillExtension(ExtendableTextField extendableTextField, boolean isDate) {
        this.extendableTextField = extendableTextField;
        this.isDate = isDate;
    }

    @Override
    public Icon getIcon(boolean b) {
        return IconLoader.getIcon("/icons/fill.svg", FillExtension.class.getClassLoader());
    }


    @Override
    public Runnable getActionOnClick() {
        return () -> {
            extendableTextField.setText((isDate)
                    ? DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN)
                    : new Date().getTime() + "");
        };
    }

    @Override
    public @NlsContexts.Tooltip String getTooltip() {
        return isDate ? "填充为当前时间" : "填充为当前时间戳";
    }

    @Override
    public int getIconGap() {
        return JBUIScale.scale(2);
    }
}
