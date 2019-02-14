package free.android.entity;

import java.io.Serializable;

/**
 * 便签功能实体类
 *
 * @author Administrator
 */
public class NoteEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 便签:数据编号
     */
    private String noteNo;

    /**
     * 便签:数据Id
     */
    private String noteId;

    /**
     * 便签:标题内容
     */
    private String noteContent;

    /**
     * 便签:标签内容
     */
    private String noteTag;

    /**
     * 便签:录入时间
     */
    private String noteInsertTime;

    /**
     * 便签:更新时间
     */
    private String noteUpdateTime;

    /**
     * 便签:删除时间
     */
    private String noteDeleteTime;

    /**
     * 便签:删除标记
     */
    private String noteDeleteFlag;

    /**
     * 便签:更新次数
     */
    private String noteUpdateCount;

    /**
     * 便签:数据子Id
     */
    private String noteSubId;

    /**
     * 便签:父类便签ID
     */
    private String noteParentId;

    /**
     * 便签:当前页面级别
     */
    private String noteCurrentPageLevel;

    /**
     * 便签:副便签录入时间
     */
    private String subNoteInsertTime;


    public NoteEntity() {
    }

    public String getNoteNo() {
        return noteNo;
    }

    public void setNoteNo(String noteNo) {
        this.noteNo = noteNo;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteInsertTime() {
        return noteInsertTime;
    }

    public void setNoteInsertTime(String noteInsertTime) {
        this.noteInsertTime = noteInsertTime;
    }

    public String getNoteSubId() {
        return noteSubId;
    }

    public void setNoteSubId(String noteSubId) {
        this.noteSubId = noteSubId;
    }

    public String getNoteTag() {
        return noteTag;
    }

    public void setNoteTag(String noteTag) {
        this.noteTag = noteTag;
    }

    public String getNoteUpdateTime() {
        return noteUpdateTime;
    }

    public void setNoteUpdateTime(String noteUpdateTime) {
        this.noteUpdateTime = noteUpdateTime;
    }

    public String getNoteDeleteTime() {
        return noteDeleteTime;
    }

    public void setNoteDeleteTime(String noteDeleteTime) {
        this.noteDeleteTime = noteDeleteTime;
    }

    public String getNoteDeleteFlag() {
        return noteDeleteFlag;
    }

    public void setNoteDeleteFlag(String noteDeleteFlag) {
        this.noteDeleteFlag = noteDeleteFlag;
    }

    public String getNoteUpdateCount() {
        return noteUpdateCount;
    }

    public String getNoteCurrentPageLevel() {
        return noteCurrentPageLevel;
    }

    public void setNoteCurrentPageLevel(String noteCurrentPageLevel) {
        this.noteCurrentPageLevel = noteCurrentPageLevel;
    }

    public String getSubNoteInsertTime() {
        return subNoteInsertTime;
    }

    public void setSubNoteInsertTime(String subNoteInsertTime) {
        this.subNoteInsertTime = subNoteInsertTime;
    }

    public void setNoteUpdateCount(String noteUpdateCount) {
        this.noteUpdateCount = noteUpdateCount;
    }

    public String getNoteParentId() {
        return noteParentId;
    }

    public void setNoteParentId(String noteParentId) {
        this.noteParentId = noteParentId;
    }
}
