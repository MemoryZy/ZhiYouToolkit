package cn.zhiyou.config.converter;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.zhiyou.entity.CodeNoteLabelEntity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Memory
 * @since 2024/3/11
 */
public class CodeNoteLabelListConverter extends Converter<List<CodeNoteLabelEntity>> {
    private static final Logger LOG = Logger.getInstance(CodeNoteLabelListConverter.class);

    @Override
    public @Nullable List<CodeNoteLabelEntity> fromString(@NotNull String value) {
        return JSONUtil.toBean(value, new TypeReference<>() {}, false);
    }

    @Override
    public @Nullable String toString(@NotNull List<CodeNoteLabelEntity> value) {
        return JSONUtil.toJsonStr(value);
    }

}
