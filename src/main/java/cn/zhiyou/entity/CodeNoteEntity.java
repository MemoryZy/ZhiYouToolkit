package cn.zhiyou.entity;

import cn.zhiyou.config.CodeNoteSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Memory
 * @since 2024/1/15
 */
public class CodeNoteEntity {

    private String id;
    private String codeName;
    private String codeContent;
    private String codeType;
    private Integer labelId;
    private int sortNum;

    public CodeNoteEntity(String id, String codeName, String codeContent, Integer labelId, int sortNum, String codeType) {
        this.id = id;
        this.codeName = codeName;
        this.codeContent = codeContent;
        this.labelId = labelId;
        this.sortNum = sortNum;
        this.codeType = codeType;
    }

    public String getId() {
        return id;
    }

    public CodeNoteEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getCodeName() {
        return codeName;
    }

    public CodeNoteEntity setCodeName(String codeName) {
        this.codeName = codeName;
        return this;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public CodeNoteEntity setCodeContent(String codeContent) {
        this.codeContent = codeContent;
        return this;
    }

    public int getSortNum() {
        return sortNum;
    }

    public CodeNoteEntity setSortNum(int sortNum) {
        this.sortNum = sortNum;
        return this;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public CodeNoteEntity setLabelId(Integer labelId) {
        this.labelId = labelId;
        return this;
    }

    public String getCodeType() {
        return codeType;
    }

    public CodeNoteEntity setCodeType(String codeType) {
        this.codeType = codeType;
        return this;
    }

    public static CodeNoteEntity of(String noteId) {
        List<CodeNoteEntity> codeNoteEntityList = CodeNoteSetting.getInstance().codeNoteEntityList;
        if (codeNoteEntityList == null)
            CodeNoteSetting.getInstance().codeNoteEntityList = new ArrayList<>();

        for (CodeNoteEntity codeNoteEntity : CodeNoteSetting.getInstance().codeNoteEntityList) {
            if (Objects.equals(noteId, codeNoteEntity.id))
                return codeNoteEntity;
        }

        return null;
    }
}