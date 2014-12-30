package edu.common.dynamicextensions.ndao;

import java.io.OutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.ContainerInfo;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.wustl.common.beans.NameValueBean;

public class ContainerDao {
	
	private static final Logger logger = Logger.getLogger(ContainerDao.class);
	
	private static final String INSERT_CONTAINER_SQL = 
			"INSERT INTO DYEXTN_CONTAINERS (IDENTIFIER, NAME, CAPTION, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY,LAST_MODIFY_TIME, XML) VALUES(?, ?, ?, ?, ?,?,? empty_blob())";

	private static final String UPDATE_CONTAINER_SQL = 
			"UPDATE DYEXTN_CONTAINERS SET NAME = ?, CAPTION = ?, LAST_MODIFIED_BY = ?, LAST_MODIFY_TIME = ?, XML = empty_blob() " +
			"WHERE IDENTIFIER = ?";
	
	private static final String GET_CONTAINER_XML_BY_ID_SQL = "SELECT XML, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY, LAST_MODIFY_TIME"
			+ " FROM DYEXTN_CONTAINERS WHERE IDENTIFIER = ?";
	
	private static final String GET_CONTAINER_XML_BY_NAME_SQL = "SELECT XML, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY, LAST_MODIFY_TIME"
			+ " FROM DYEXTN_CONTAINERS WHERE NAME = ?";
	
	private static final String GET_CONTAINER_IDS_SQL =  
			"SELECT DYEXTN_CONTAINERS_SEQ.NEXTVAL " +
			"FROM (SELECT LEVEL FROM DUAL CONNECT BY LEVEL <= %d)";
	
	private static final String GET_CONTAINER_NAME_BY_ID =  
			"SELECT NAME FROM DYEXTN_CONTAINERS WHERE IDENTIFIER = ?";
	
	private static final String LIST_ALL_CONTAINERS = "SELECT NAME,IDENTIFIER FROM DYEXTN_CONTAINERS ORDER BY NAME";
	
	private static final String GET_CONTAINER_INFO_BY_CREATOR_SQL = 
			"SELECT IDENTIFIER, NAME, CAPTION, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY, LAST_MODIFY_TIME " +
			"FROM DYEXTN_CONTAINERS WHERE CREATED_BY = ?";
	
	private static final String GET_LAST_UPDATED_TIME_SQL = "SELECT LAST_MODIFY_TIME FROM DYEXTN_CONTAINERS WHERE IDENTIFIER = ?";

	private JdbcDao jdbcDao = null;
	
	public ContainerDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
	
	public List<Long> getContainerIds(int numIds) 
	throws Exception {
		String sql = String.format(GET_CONTAINER_IDS_SQL, numIds);		
		logger.info("ContainerDao :: getContainerIds :: sql :: " + sql);
						
		ResultSet resultSet = null;		
		try {
			List<Long> ids = new ArrayList<Long>();
			resultSet = jdbcDao.getResultSet(sql, null);
			while (resultSet.next()){
				Long value = resultSet.getLong("NEXTVAL");
				ids.add(value);
			}

			logger.info("ContainerDao :: getContainerIds :: ids :: "+ids);
			return ids;			
		} finally {
			jdbcDao.close(resultSet);
		}				
	}
	
	public void insert(UserContext userCtxt, Container c) 
	throws SQLException {
		List<Object> params = new ArrayList<Object>();		
		params.add(c.getId());
		params.add(c.getName());
		params.add(c.getCaption());
		params.add(userCtxt != null ? userCtxt.getUserId() : null);
		Timestamp createTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		params.add(createTime);
		//added to insert last modified by username and timestamp
		params.add(userCtxt != null ? userCtxt.getUserId() : null);
		Timestamp modifiedTime = createTime;
		params.add(modifiedTime);
		
		jdbcDao.executeUpdate("INSERT INTO DYEXTN_CONTAINERS (IDENTIFIER, NAME, CAPTION, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY,LAST_MODIFY_TIME, XML) VALUES(?, ?, ?, ?, ?,?,?, empty_blob())", params);	
		updateContainerXml(c.getId(), c.toXml());
	}
	
	public void update(UserContext userCtxt, Container c) 
	throws SQLException {		
		List<Object> params = new ArrayList<Object>();				
		params.add(c.getName());
		params.add(c.getCaption());
		params.add(userCtxt != null ? userCtxt.getUserId() : null);
		Timestamp updateTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		params.add(updateTime);
		params.add(c.getId());
		
		jdbcDao.executeUpdate(UPDATE_CONTAINER_SQL, params);
		updateContainerXml(c.getId(), c.toXml());
	}
	
	public Container getById(Long id) throws SQLException {
		List<Object> params = new ArrayList<Object>();
		params.add(id);

		String xml = null;
		ResultSet rs = null; 
		try {
			rs = jdbcDao.getResultSet(GET_CONTAINER_XML_BY_ID_SQL, params);
			if (rs.next()) {
				Blob xmlBlob = rs.getBlob("XML");
				if (xmlBlob != null) {
					int length = (int) xmlBlob.length();
					xml = new String(xmlBlob.getBytes(1L, length));
				}
			}			

			if (xml != null) {
				Container container = Container.fromXml(xml);
				container.setCreatedBy(rs.getLong("CREATED_BY"));
				container.setCreationTime(rs.getTimestamp("CREATE_TIME"));
				container.setLastUpdatedBy(rs.getLong("LAST_MODIFIED_BY"));
				container.setLastUpdatedTime(rs.getTimestamp("LAST_MODIFY_TIME"));
				return container;
			}
		} finally {
			jdbcDao.close(rs);
		}

		return null;
	}
	
	public List<ContainerInfo> getContainerInfoByCreatorold(Long creatorId) 
	throws SQLException {
		List<Object> params = new ArrayList<Object>();
		params.add(creatorId);
		
		ResultSet rs = null;
		try {
			List<ContainerInfo> result = new ArrayList<ContainerInfo>();
			rs = jdbcDao.getResultSet(GET_CONTAINER_INFO_BY_CREATOR_SQL, params);
			while (rs.next()) {
				ContainerInfo containerInfo = new ContainerInfo();
				containerInfo.setContainerId(rs.getLong("IDENTIFIER"));
				containerInfo.setName(rs.getString("NAME"));
				containerInfo.setCaption(rs.getString("CAPTION"));
				containerInfo.setCreatedBy(rs.getLong("CREATED_BY"));
				containerInfo.setCreationTime(rs.getTimestamp("CREATE_TIME"));
				containerInfo.setLastUpdatedBy(rs.getLong("LAST_MODIFIED_BY"));
				containerInfo.setLastUpdatedTime(rs.getTimestamp("LAST_MODIFY_TIME"));
				
				result.add(containerInfo);				
			}
			
			return result;
		} finally {
			jdbcDao.close(rs);
		}
	}
	
	public List<ContainerInfo> getContainerInfoByCreator(Long creatorId) throws SQLException
	{
		List params = new ArrayList();
		params.add(creatorId);
		ResultSet rs = null;
		try 
		{
			ContainerInfoExtractor extractor = new ContainerInfoExtractor();
			List result = new ArrayList();
			rs = this.jdbcDao.getResultSet("SELECT IDENTIFIER, NAME, CAPTION, CREATED_BY, CREATE_TIME, LAST_MODIFIED_BY, LAST_MODIFY_TIME FROM DYEXTN_CONTAINERS WHERE CREATED_BY = ?", params);
			while (rs.next()) {
				result.add(extractor.getContainerInfo(rs));
			}
	
			List localList1 = result;
			return localList1; 
		} 
		finally 
		{ 
			this.jdbcDao.close(rs); 
		}
	 }

	private void updateContainerXml(Long id, String xml) 
	throws SQLException  {
		List<Object> params = new ArrayList<Object>();
		params.add(id);
		
		ResultSet rs = null;		
		try {
			String selectXmlForUpdate = new StringBuilder(GET_CONTAINER_XML_BY_ID_SQL).append(" FOR UPDATE").toString();		
			rs = jdbcDao.getResultSet(selectXmlForUpdate, params);		
			rs.next();
			
			Blob blob = rs.getBlob("XML");
			writeToBlob(blob, xml);			
		} finally {
			jdbcDao.close(rs);
		}				
	}
	
	private void writeToBlob(Blob blob, String xml) {
		try {
			OutputStream blobOut = blob.setBinaryStream(0);		
			blobOut.write(xml.getBytes());
			blobOut.close();
		} catch (Exception e) {
			throw new RuntimeException("Error writing blob", e);
		}
	}

	public List<NameValueBean> listAllContainerIdAndName() throws SQLException {
		logger.info("containerDao: listAllContainer : " + LIST_ALL_CONTAINERS);
		
		ResultSet resultSet = null;
		try {
			resultSet = jdbcDao.getResultSet(LIST_ALL_CONTAINERS, null);
			List<NameValueBean> beans = new ArrayList<NameValueBean>();
			
			while (resultSet.next()) {
				beans.add(new NameValueBean(resultSet.getString("NAME"), resultSet.getLong("IDENTIFIER")));
			}
			
			return beans;			
		} finally {
			jdbcDao.close(resultSet);
		}
	}
	
	public Container getByName(String name) throws SQLException {
		List<Object> params = new ArrayList<Object>();
		params.add(name);
			
		String xml = null ;
		ResultSet rs = null;		
		try {
			rs = jdbcDao.getResultSet(GET_CONTAINER_XML_BY_NAME_SQL, params);
			
			if (rs.next()) {
				Blob xmlBlob = rs.getBlob("XML");			
				if (xmlBlob != null) {
					int length = (int) xmlBlob.length();
					xml = new String(xmlBlob.getBytes(1L, length));
				}
			}

			if (xml != null) {
				Container container = Container.fromXml(xml);
				container.setCreatedBy(rs.getLong("CREATED_BY"));
				container.setCreationTime(rs.getTimestamp("CREATE_TIME"));
				container.setLastUpdatedBy(rs.getLong("LAST_MODIFIED_BY"));
				container.setLastUpdatedTime(rs.getTimestamp("LAST_MODIFY_TIME"));
				return container;
			}
		} finally {
			jdbcDao.close(rs);
		}
		

		return null;
	}
	
	public Date getLastUpdatedTime(Long id) throws SQLException {
		Date lastUpdatedTime = null;
		ResultSet rs = null;
		try {
			rs = jdbcDao.getResultSet(GET_LAST_UPDATED_TIME_SQL, Collections.singletonList(id));

			if (rs.next()) {
				lastUpdatedTime = rs.getTimestamp("LAST_MODIFY_TIME");
			}
			return lastUpdatedTime;
		} finally {
			jdbcDao.close(rs);
		}
	}

	public String getNameById(Long id) throws SQLException {
		ResultSet rs = null; 
		try {
			rs = jdbcDao.getResultSet(GET_CONTAINER_NAME_BY_ID, Collections.singletonList(id));
			return rs.next() ? rs.getString("NAME") : null;
		} finally {
			jdbcDao.close(rs);
		}
	}
}
