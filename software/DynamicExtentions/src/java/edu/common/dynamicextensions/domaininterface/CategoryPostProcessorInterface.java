package edu.common.dynamicextensions.domaininterface;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;


public interface CategoryPostProcessorInterface
{

	void postProcess(Long participantId, Long dynRecId,Long recEntryId,Long containerId,SessionDataBean sessionDataBean) throws BizLogicException;

}
