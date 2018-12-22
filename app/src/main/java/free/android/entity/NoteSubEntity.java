package free.android.entity;

import java.io.Serializable;

public class NoteSubEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** 便签子画面信息的打卡项目 */
	private String noteSubItem;

	/** 便签子画面信息的任务完成标识 */
	private String noteSubOverFlag;

	/** 便签子画面信息的任务完成评价 */
	private String noteSubAppraisal;

	/** 便签子画面信息的类型 */
	private String noteSubType;

	/** 便签子画面信息的城市 */
	private String noteSubCity;

	public NoteSubEntity() {
	}

	/**
	 * @return the noteSubItem
	 */
	public String getNoteSubItem() {
		return noteSubItem;
	}

	/**
	 * @param noteSubItem the noteSubItem to set
	 */
	public void setNoteSubItem(String noteSubItem) {
		this.noteSubItem = noteSubItem;
	}

	/**
	 * @return the noteSubOverFlag
	 */
	public String getNoteSubOverFlag() {
		return noteSubOverFlag;
	}

	/**
	 * @param noteSubOverFlag the noteSubOverFlag to set
	 */
	public void setNoteSubOverFlag(String noteSubOverFlag) {
		this.noteSubOverFlag = noteSubOverFlag;
	}

	/**
	 * @return the noteSubAppraisal
	 */
	public String getNoteSubAppraisal() {
		return noteSubAppraisal;
	}

	/**
	 * @param noteSubAppraisal the noteSubAppraisal to set
	 */
	public void setNoteSubAppraisal(String noteSubAppraisal) {
		this.noteSubAppraisal = noteSubAppraisal;
	}

	/**
	 * @return the noteSubType
	 */
	public String getNoteSubType() {
		return noteSubType;
	}

	/**
	 * @param noteSubType the noteSubType to set
	 */
	public void setNoteSubType(String noteSubType) {
		this.noteSubType = noteSubType;
	}

	/**
	 * @return the noteSubCity
	 */
	public String getNoteSubCity() {
		return noteSubCity;
	}

	/**
	 * @param noteSubCity the noteSubCity to set
	 */
	public void setNoteSubCity(String noteSubCity) {
		this.noteSubCity = noteSubCity;
	}

}
