package cn.zhiyou.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.config.CodeNoteSetting;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

/**
 * @author wcp
 * @since 2024/3/11
 */
public class CodeNoteLabelDetailWindow extends DialogWrapper {
    private JPanel rootPanel;
    private JBTextField labelNameTf;
    private final TextFieldErrorPopupDecorator textFieldErrorPopupDecorator;
    private final Integer labelId;
    private final boolean isAdd;

    public CodeNoteLabelDetailWindow(@Nullable Project project, String labelNameStr, Integer labelId) {
        super(project, true);
        this.textFieldErrorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), labelNameTf);
        this.labelId = labelId;
        this.isAdd = Objects.isNull(labelId);

        if (StrUtil.isNotBlank(labelNameStr)) {
            labelNameTf.setText(labelNameStr);
        }

        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                labelNameTf.requestFocusInWindow();
            }
        });

        setTitle("标签详情");
        setOKButtonText("确定");
        setCancelButtonText("取消");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            if (executeOkAction()) {
                applyFields();
                close(OK_EXIT_CODE);
            }
        }
    }

    private boolean executeOkAction() {
        String labelNameText = StrUtil.trim(labelNameTf.getText());
        if (StrUtil.isBlank(labelNameText)) {
            textFieldErrorPopupDecorator.setError("标签名不能为空！");
            return false;
        }

        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        if (labelList == null) {
            ArrayList<CodeNoteLabelEntity> list = new ArrayList<>();
            CodeNoteSetting.getInstance().labelList = list;
            list.add(new CodeNoteLabelEntity(-1, "默认"));
        }

        List<CodeNoteLabelEntity> sameLabel = CodeNoteSetting.getInstance().labelList
                .stream().filter(el -> Objects.equals(el.getLabel(), labelNameText)).toList();

        if (CollUtil.isNotEmpty(sameLabel)) {
            textFieldErrorPopupDecorator.setError("已有同名标签！");
            return false;
        }

        if (isAdd) {
            // 计算最大id
            int id = 1;
            if (CollUtil.isNotEmpty(CodeNoteSetting.getInstance().labelList)) {
                CodeNoteLabelEntity labelEntity = Collections.max(CodeNoteSetting.getInstance().labelList, Comparator.comparingInt(CodeNoteLabelEntity::getId));
                id = labelEntity.getId() + 1;
            }

            CodeNoteSetting.getInstance().labelList.add(new CodeNoteLabelEntity(id, labelNameText));
        } else {
            CodeNoteLabelEntity codeNoteLabelEntity = CodeNoteSetting.getInstance().labelList.stream()
                    .filter(el -> Objects.equals(el.getId(), labelId))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(codeNoteLabelEntity)) {
                codeNoteLabelEntity.setLabel(labelNameText);
            }
        }

        return true;
    }


}
