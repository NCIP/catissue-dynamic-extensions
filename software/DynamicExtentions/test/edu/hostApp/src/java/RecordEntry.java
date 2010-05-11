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
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1234567890L;

    /**
     * Equals.
     *
     * @param recordEntry
     *            the record entry
     * @return true, if successful
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object recordEntry)
    {
        boolean flag = false;
        if (recordEntry instanceof RecordEntry)
        {
            RecordEntry recEntry = (RecordEntry) recordEntry;
            if (getId() != null && (this).getId().equals(recEntry.getId()))
            {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Sets the all values.
     *
     * @param arg0
     *            the new all values
     * @throws AssignDataException
     *             the assign data exception
     * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.IValueObject)
     */
    @Override
    public void setAllValues(IValueObject arg0) throws AssignDataException
    {
        // TODO Auto-generated method stub
    }
}
