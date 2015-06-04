
package edu.common.dynamicextensions.ndao;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.common.dynamicextensions.domain.nui.ContainerInfo;

public class ContainerInfoExtractor
{

	public ContainerInfo getContainerInfo(ResultSet rs) throws SQLException
	{
		try
		{
			ContainerInfo containerInfo = new ContainerInfo();
			containerInfo.setContainerId(Long.valueOf(rs.getLong("IDENTIFIER")));
			containerInfo.setName(rs.getString("NAME"));
			containerInfo.setCaption(rs.getString("CAPTION"));
			containerInfo.setCreatedBy(Long.valueOf(rs.getLong("CREATED_BY")));
			containerInfo.setCreationTime(rs.getTimestamp("CREATE_TIME"));
			containerInfo.setLastUpdatedBy(Long.valueOf(rs.getLong("LAST_MODIFIED_BY")));
			containerInfo.setLastUpdatedTime(rs.getTimestamp("LAST_MODIFY_TIME"));

			return containerInfo;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error while retrieving the container info from the resultset",
					e);
		}
		
	}
}
