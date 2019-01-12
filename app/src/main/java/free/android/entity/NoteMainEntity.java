package free.android.entity;

import java.io.Serializable;

/**
 * 便签功能实体类
 *
 * @author Administrator
 */
public class NoteMainEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 便签主画面每条信息的No
     */
    private String noteMasterNo;

    /**
     * 便签主画面每条信息的Id
     */
    private String noteMasterId;

    /**
     * 便签主画面每条信息的title
     */
    private String noteMasterTitle;

    /**
     * 便签主画面信息的预算时间
     */
    private String noteMasterSpendTime;

    /**
     * 便签主画面信息的详细地址
     */
    private String noteMasterAddress;

    /**
     * 便签子画面
     */
    private NoteSubEntity noteSubEntity = new NoteSubEntity();

    public NoteMainEntity() {
    }

    public String getNoteMasterNo() {
        return noteMasterNo;
    }

    public void setNoteMasterNo(String noteMasterNo) {
        this.noteMasterNo = noteMasterNo;
    }

    /**
     * @return the noteMasterId
     */
    public String getNoteMasterId() {
        return noteMasterId;
    }

    /**
     * @param noteMasterId the noteMasterId to set
     */
    public void setNoteMasterId(String noteMasterId) {
        this.noteMasterId = noteMasterId;
    }

    /**
     * @return the noteMasterTitle
     */
    public String getNoteMasterTitle() {
        return noteMasterTitle;
    }

    /**
     * @param noteMasterTitle the noteMasterTitle to set
     */
    public void setNoteMasterTitle(String noteMasterTitle) {
        this.noteMasterTitle = noteMasterTitle;
    }

    /**
     * @return the noteMasterSpendTime
     */
    public String getNoteMasterSpendTime() {
        return noteMasterSpendTime;
    }

    /**
     * @param noteMasterSpendTime the noteMasterSpendTime to set
     */
    public void setNoteMasterSpendTime(String noteMasterSpendTime) {
        this.noteMasterSpendTime = noteMasterSpendTime;
    }

    /**
     * @return the noteMasterAddress
     */
    public String getNoteMasterAddress() {
        return noteMasterAddress;
    }

    /**
     * @param noteMasterAddress the noteMasterAddress to set
     */
    public void setNoteMasterAddress(String noteMasterAddress) {
        this.noteMasterAddress = noteMasterAddress;
    }

    /**
     * @return the noteSubEntity
     */
    public NoteSubEntity getNoteSubEntity() {
        return noteSubEntity;
    }

    /**
     * @param noteSubEntity the noteSubEntity to set
     */
    public void setNoteSubEntity(NoteSubEntity noteSubEntity) {
        this.noteSubEntity = noteSubEntity;
    }


    public NoteMainEntity(String noteMasterId, String noteMasterTitle, String noteMasterSpendTime,
                          String noteMasterAddress, NoteSubEntity noteSubEntity) {
        super();
        this.noteMasterId = noteMasterId;
        this.noteMasterTitle = noteMasterTitle;
        this.noteMasterSpendTime = noteMasterSpendTime;
        this.noteMasterAddress = noteMasterAddress;
        this.noteSubEntity = noteSubEntity;
    }

}
