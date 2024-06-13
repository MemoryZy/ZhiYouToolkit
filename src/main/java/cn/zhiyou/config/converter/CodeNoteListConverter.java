package cn.zhiyou.config.converter;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.entity.CodeNoteEntity;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 自定义持久化类型的转换器
 *
 * @author Memory
 * @since 2024/1/15
 */
public class CodeNoteListConverter extends Converter<List<CodeNoteEntity>> {
    @Override
    public @Nullable List<CodeNoteEntity> fromString(@NotNull String value) {
        return JSONUtil.toBean(value, new TypeReference<>() {}, false);
    }

    @Override
    public @Nullable String toString(@NotNull List<CodeNoteEntity> value) {
        return JSONUtil.toJsonStr(value);
    }
}
