
package edu.common.dynamicextensions.domaininterface;

import java.sql.Timestamp;

/**
 * @author kunal_kamble
 *
 */
public interface DESQLAuditInterface
{

	/**
	 * @return audit date
	 */
	Timestamp getAuditDate();

	/**
	 * @param auditDate
	 */
	void setAuditDate(Timestamp auditDate);

	/**
	 * @return sql query executed
	 */
	String getQueryExecuted();

	/**
	 * @param queryExecuted
	 */
	void setQueryExecuted(String queryExecuted);

	/**
	 * @return user id
	 */
	Long getUserId();

	/**
	 * @param userId
	 */
	void setUserId(Long userId);
}
