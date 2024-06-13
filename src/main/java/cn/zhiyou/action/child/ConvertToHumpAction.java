package cn.zhiyou.action.child;

import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class ConvertToHumpAction extends AnAction {
    public ConvertToHumpAction() {
        super(ActionBundle.message("action.convert.to.hump.text"),
                ActionBundle.message("action.convert.to.hump.description"),
                null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.convertSelectText(e, CommonUtil::toCamel);
    }
}
