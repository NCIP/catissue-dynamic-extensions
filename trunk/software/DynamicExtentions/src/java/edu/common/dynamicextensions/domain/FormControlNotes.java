
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_FORM_CTRL_NOTES"
 */
public class FormControlNotes extends DynamicExtensionBaseDomainObject
		implements
			FormControlNotesInterface
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;

	protected String note;

	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_FRM_CONTROL_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @hibernate.property name="note" type="string" column="NOTE" length="255"
	 * @return Returns the note.
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note)
	{
		this.note = note;
	}

}
