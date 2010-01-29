/**
 *
 */

package edu.hostApp.src.java;

import edu.common.dynamicextensions.domain.integration.AbstractRecordEntry;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @author suhas_khot
 * @hibernate.class table="TEST_CASES_RECORD_ENTRY"
 */
public class RecordEntry extends AbstractRecordEntry
{

	/*
	 *
	 */
	private static final long serialVersionUID = 1234567890L;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object recordEntry)
	{
		boolean flag = false;
		if (this.getId() != null)
		{
			RecordEntry recEntry = (RecordEntry) recordEntry;
			if ((this).getId().equals(recEntry.getId()))
			{
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}
}