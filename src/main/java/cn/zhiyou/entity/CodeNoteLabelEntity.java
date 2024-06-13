package cn.zhiyou.entity;

import cn.zhiyou.config.CodeNoteSetting;

import java.util.Objects;

/**
 * @author Memory
 * @since 2024/3/11
 */
public class CodeNoteLabelEntity {

    private Integer id;

    private String label;


    public CodeNoteLabelEntity(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static CodeNoteLabelEntity of(Integer labelId) {
        return CodeNoteSetting.getInstance().labelList.stream()
                .filter(el -> Objects.equals(el.getId(), labelId))
                .findFirst()
                .orElse(new CodeNoteLabelEntity(-1, "默认"));
    }
}
