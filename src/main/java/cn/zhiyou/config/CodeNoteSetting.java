package cn.zhiyou.config;

import cn.zhiyou.config.converter.CodeNoteLabelListConverter;
import cn.zhiyou.config.converter.CodeNoteListConverter;
import cn.zhiyou.entity.CodeNoteEntity;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wcp
 * @since 2024/1/15
 */
@State(name = "zhiYouCodeNote", storages = {@Storage(value = "zhiYouCodeNote.xml")})
public class CodeNoteSetting implements PersistentStateComponent<CodeNoteSetting> {

    public static CodeNoteSetting getInstance() {
        return ApplicationManager.getApplication().getService(CodeNoteSetting.class);
    }

    @OptionTag(converter = CodeNoteListConverter.class)
    public List<CodeNoteEntity> codeNoteEntityList;

    @OptionTag(converter = CodeNoteLabelListConverter.class)
    public List<CodeNoteLabelEntity> labelList;

    @Override
    public @Nullable CodeNoteSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CodeNoteSetting state) {
        this.codeNoteEntityList = state.codeNoteEntityList;
        this.labelList = state.labelList;
    }

    public static void initDefaultLabel() {
        // 初始化默认标签
        List<CodeNoteLabelEntity> labelList = CodeNoteSetting.getInstance().labelList;
        if (labelList == null) {
            CodeNoteSetting.getInstance().labelList = new ArrayList<>();
            CodeNoteSetting.getInstance().labelList.add(new CodeNoteLabelEntity(-1, "默认"));
        }
    }

}
