package cn.zhiyou.action.child;

import cn.zhiyou.action.ConvertStringAction;
import cn.zhiyou.bundle.ActionBundle;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/3/15
 */
public class ChineseToPinyinWithTone extends AnAction {

    public ChineseToPinyinWithTone() {
        super(ActionBundle.message("action.chinese.to.pinyin.with.tone.text"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.convertSelectText(e, el -> CommonUtil.toPinyin(el, true, true));
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
