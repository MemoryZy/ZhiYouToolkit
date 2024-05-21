package cn.zhiyou.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.ui.JsonContrastWindow;
import cn.zhiyou.utils.ActionUtil;
import cn.zhiyou.utils.CommonUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author wcp
 * @since 2024/5/19
 */
public class JsonContrastAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        // 只用于编辑器中包含Json的
        Editor editor = ActionUtil.getEditor(event);
        Document document = editor.getDocument();
        // 文本
        String text = StrUtil.trim(document.getText());
        String jsonStr = (CommonUtil.isJson(text)) ? text : CommonUtil.extractJsonStr(text);

        // 文档输入后检测Json数组，输出数量
        if (StrUtil.isNotBlank(jsonStr)) {
            if (CommonUtil.isJson(jsonStr)) {
                // 当前Json
                JSONObject jsonObject = (JSONObject) JSONUtil.parse(jsonStr, JSONConfig.create().setIgnoreNullValue(false));

                // 选择类
                PsiClass selectPsiClass = ActionUtil.chooseClass(project, "选择映射类");

                if (Objects.isNull(selectPsiClass)) return;
                // 类型没匹配上可以在属性后面 加灰色，说类型不匹配，Json值为 xxx

                new JsonContrastWindow(project, selectPsiClass, jsonObject).show();
            }
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只用于编辑器中包含Json的
        Editor editor = ActionUtil.getEditor(e);
        Document document = editor.getDocument();
        // 提取json
        String text = StrUtil.trim(document.getText());
        String jsonStr = (CommonUtil.isJson(text)) ? text : CommonUtil.extractJsonStr(text);
        JSON json = null;
        if (StrUtil.isNotBlank(jsonStr) && CommonUtil.isJson(jsonStr)) {
            json = JSONUtil.parse(jsonStr, JSONConfig.create().setIgnoreNullValue(false));
        }

        e.getPresentation().setEnabledAndVisible(Objects.nonNull(json) && json instanceof JSONObject);
    }

}
