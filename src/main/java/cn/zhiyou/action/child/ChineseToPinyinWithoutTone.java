package cn.zhiyou.action.child;

import cn.zhiyou.action.ConvertStringAction;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wcp
 * @since 2024/3/15
 */
public class ChineseToPinyinWithoutTone extends AnAction {
    public ChineseToPinyinWithoutTone() {
        super("Chinese to Pinyin (不带音调)");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.convertSelectText(e, el -> CommonUtil.toPinyin(el, true, false));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(ConvertStringAction.containsChinese(e));
    }
}
