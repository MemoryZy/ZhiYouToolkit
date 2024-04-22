package cn.zhiyou.action.child;

import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class ConvertToCamelAction extends AnAction {
    public ConvertToCamelAction() {
        super("Convert to Camel (驼峰)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.convertSelectText(e, CommonUtil::toCamel);
    }
}
