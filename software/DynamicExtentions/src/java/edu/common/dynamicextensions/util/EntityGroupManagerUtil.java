package edu.common.dynamicextensions.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class EntityGroupManagerUtil {

	public static Set<Long> getAssociatedFormId(EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException {
		Set<Long> formIdSet = new HashSet<Long>();
		for (ContainerInterface container : entityGroup
				.getMainContainerCollection()) {
			formIdSet.add(container.getId());
			Collection<Long> categoryContainerId = getCategoryContainerId(container
					.getAbstractEntity().getId());
			if (!categoryContainerId.isEmpty()) {
				formIdSet.addAll(categoryContainerId);
			}
		}
		return formIdSet;
	}

	private static Collection<Long> getCategoryContainerId(Long entityId)
			throws DynamicExtensionsSystemException {
		String query = "select cont.identifier from dyextn_category cat join dyextn_category_entity catEnt" +
		" on cat.root_category_element = catEnt.identifier join dyextn_container cont" +
		" on cont.abstract_entity_id = catEnt.identifier and" +
		" catEnt.ENTITY_ID = ?";
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean("catEnt.ENTITY_ID", entityId));
		ResultSet resultSet = null;
		JDBCDAO jdbcDAO = null;
		Collection<Long> catContainerId = new ArrayList<Long>();
		try
		{
			jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDAO.getResultSet(query, queryDataList, null);
			if (resultSet.next())
			{
				catContainerId.add(resultSet.getLong(1));
			}
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsSystemException("Exception in query execution", e);
		}
		catch (final SQLException e)
		{
			throw new DynamicExtensionsSystemException("Exception in execution query :: " + query
					+ " identifier :: " + queryDataList.get(0).getColumnValue(), e);
		}
		finally
		{
			try
			{
				jdbcDAO.closeStatement(resultSet);
				DynamicExtensionsUtility.closeDAO(jdbcDAO);
			}
			catch (final DAOException e)
			{
				throw new DynamicExtensionsSystemException("Exception in query execution", e);
			}
		}

		return catContainerId;
	}
}
