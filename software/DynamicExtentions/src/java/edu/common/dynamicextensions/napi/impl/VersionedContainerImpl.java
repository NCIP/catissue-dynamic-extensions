package edu.common.dynamicextensions.napi.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.ContainerInfo;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.domain.nui.VersionedContainerInfo;
import edu.common.dynamicextensions.napi.FormPublishHook;
import edu.common.dynamicextensions.napi.VersionedContainer;
import edu.common.dynamicextensions.ndao.JdbcDao;
import edu.common.dynamicextensions.ndao.VersionedContainerDao;
import edu.common.dynamicextensions.nutility.ContainerChangeLog;
import edu.common.dynamicextensions.nutility.ContainerUtility;
import edu.common.dynamicextensions.nutility.FormPublishHookMgr;

public class VersionedContainerImpl implements VersionedContainer {
	private JdbcDao jdbcDao;
	
	public void setJdbcDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
	
	@Override
	public String getFormName(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			return vdao.getFormName(formId);								
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining form name: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}
	
	@Override
	public Long getContainerId(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainerId(jdbcDao, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container id: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}			
		}
	}
	
	public Long getContainerId(JdbcDao jdbcDao, Long formId) {
		return getContainerId(jdbcDao, formId, Calendar.getInstance().getTime());
	}
	
	@Override
	public Long getContainerId(Long formId, Date activationDate) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainerId(jdbcDao, formId, activationDate);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container id: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}			
		}		
	}
	
	public Long getContainerId(JdbcDao jdbcDao, Long formId, Date activationDate) {
		VersionedContainerInfo info = getMatchingContainerInfo(jdbcDao, formId, activationDate);
		return info != null ? info.getContainerId() : null;
	}
	
	@Override
	public Container getContainer(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainer(jdbcDao, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
		
	}
	
	public Container getContainer(JdbcDao jdbcDao, Long formId) {
		return getContainer(jdbcDao, formId, Calendar.getInstance().getTime());
	}
	
	@Override
	public Container getContainer(Long formId, Date activationDate) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainer(jdbcDao, formId, activationDate);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}
	
	public Container getContainer(JdbcDao jdbcDao, Long formId, Date activationDate) {		
		try {
			Container published = null;
			Long publishedId = getContainerId(jdbcDao, formId, activationDate);
			if (publishedId != null) {
				published = Container.getContainer(jdbcDao, publishedId); 								
			}

			return published;
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining published container: " + formId, e);
		} 
	}
	
	//
	// The method which refers form by name goes below
	// 
	
	@Override
	public String getContainerName(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainerName(jdbcDao, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container id: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}			
		}
	}
	
	public String getContainerName(JdbcDao jdbcDao, Long formId) {
		return getContainerName(jdbcDao, formId, Calendar.getInstance().getTime());
	}
	
	@Override
	public String getContainerName(Long formId, Date activationDate) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainerName(jdbcDao, formId, activationDate);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container id: " + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}			
		}		
	}
	
	public String getContainerName(JdbcDao jdbcDao, Long formId, Date activationDate) {
		VersionedContainerInfo info = getMatchingContainerInfo(jdbcDao, formId, activationDate);
		return info != null ? Container.getContainerNameById(info.getContainerId()) : null;
	}
	
	@Override
	public Container getContainer(String formName) {
		
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainer(jdbcDao, formName);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container: " + formName, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}

	public Container getContainer(JdbcDao jdbcDao,String formName) {
		return getContainer(jdbcDao, formName, Calendar.getInstance().getTime());
	}
	
	@Override
	public Container getContainer(String formName, Date activationDate) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getContainer(jdbcDao, formName, activationDate);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining container: " + formName, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}
	
	public Container getContainer(JdbcDao jdbcDao, String formName, Date activationDate) {		
		try {
			Container published = null;
			Long publishedId = getContainerIdByName(jdbcDao, formName, activationDate);
			if (publishedId != null) {
				published = Container.getContainer(jdbcDao, publishedId); 								
			}

			return published;
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining published container: " + formName, e);
		} 
	}
	
	public Long getContainerIdByName(JdbcDao jdbcDao, String formName, Date activationDate) {
		try {
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			List<VersionedContainerInfo> versionedContainers = vdao.getPublishedContainersInfo(formName);
			
			Long resultId = null;
			Date resultDate = null;
			for (VersionedContainerInfo info : versionedContainers) {
				if (info.getActivationDate().compareTo(activationDate) <= 0 && 
					(resultDate == null || info.getActivationDate().after(resultDate))) {

					resultId = info.getContainerId();
					resultDate = info.getActivationDate();
				}
			}
			
			return resultId;			
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining published container: " + formName, e);
		}		
	}
	
	private VersionedContainerInfo getMatchingContainerInfo(JdbcDao jdbcDao, Long formId, Date activationDate) {
		VersionedContainerInfo versionInfo = null;;
		try {
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			List<VersionedContainerInfo> versionedContainers = vdao.getPublishedContainersInfo(formId);
			
			Date resultDate = null;
			for (VersionedContainerInfo info : versionedContainers) {
			
				if (info.getActivationDate().compareTo(activationDate) <= 0 && 
						(resultDate == null || info.getActivationDate().after(resultDate))) {
					versionInfo = info;
				}
			}
			
			return versionInfo;			
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining published container: " + formId, e);
		}		
	}
	
	@Override
	public Long getDraftContainerId(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getDraftContainerId(jdbcDao, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining draft container:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}		
	}
	
	public Long getDraftContainerId(JdbcDao jdbcDao, Long formId) {		
		try {
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			VersionedContainerInfo info = vdao.getDraftContainerInfo(formId);			
			return (info != null) ? info.getContainerId() : null; 
		} catch (Exception e) {
			throw new RuntimeException("Error obtainer draft container:" + formId, e);
		}		
	}
	
	@Override
	public Container getDraftContainer(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return getDraftContainer(jdbcDao, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining draft container:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}
	
	public Container getDraftContainer(JdbcDao jdbcDao, Long formId) {		
		try {
			Container draft = null;
			Long draftContainerId = getDraftContainerId(jdbcDao, formId);
			if (draftContainerId != null) {
				draft = Container.getContainer(jdbcDao, draftContainerId);
			}
			
			return draft;
		} catch (Exception e) {
			throw new RuntimeException("Error obtainer draft container:" + formId, e);
		}
	}
	
	@Override
	public Long saveAsDraft(UserContext usrCtx, Long draftContainerId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			return saveAsDraft(jdbcDao, usrCtx, draftContainerId);
		} catch (Exception e) {
			throw new RuntimeException("Error saving draft container:" + draftContainerId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}		
	}
	
	public Long saveAsDraft(JdbcDao jdbcDao, UserContext usrCtx, Long draftContainerId) {
		VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
		Long formId = vdao.getFormIdByDraftContainerId(draftContainerId);
		
		if (formId == null) {
			String containerName = Container.getContainerNameById(jdbcDao, draftContainerId);
			formId = vdao.insertFormInfo(containerName);
			
			VersionedContainerInfo vc = new VersionedContainerInfo();
			vc.setFormId(formId);
			vc.setContainerId(draftContainerId);
			vc.setCreatedBy(usrCtx.getUserId());
			vc.setCreationTime(Calendar.getInstance().getTime());
			vc.setStatus("draft");
			
			vdao.insertVersionedContainerInfo(vc);
		}
		
		return formId;
	}
	
	@Override
	public void publishRetrospective(UserContext usrCtx, Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			publishRetrospective(jdbcDao, usrCtx, formId);
		} catch (Exception e) {
			throw new RuntimeException("Error saving container retrospectively:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}				
	}
	
	public void publishRetrospective(JdbcDao jdbcDao, UserContext usrCtx, Long formId) {		
		List<Long> publishedIds = getPublishedContainerIds(jdbcDao, formId); 		
		if (publishedIds.isEmpty()) {
			publishProspective(usrCtx, formId, Calendar.getInstance().getTime());
			return;
		}
		
		try {
			Container draftContainer = getDraftContainer(formId);			
			Container latestContainer = Container.getContainer(jdbcDao, publishedIds.get(publishedIds.size() - 1));
			
			ContainerChangeLog changeLog = ContainerUtility.getChangeLog(latestContainer, draftContainer);
			if (!changeLog.anyChanges()) {
				return; // no changes nothing to do
			}
			
			applyChangeAndSave(jdbcDao, usrCtx, latestContainer, changeLog);
			
			for (int i = 0; i < publishedIds.size() - 1; ++i) {
				Container publishedContainer = Container.getContainer(jdbcDao, publishedIds.get(i));
				applyChangeAndSave(jdbcDao, usrCtx, publishedContainer, changeLog);
			}	
			
			runPostHooks(usrCtx, formId);

		} catch (Exception e) {
			throw new RuntimeException("Error publishing container retrospectively", e);
		}
	}
	
	//
	// TODO: Validate the activationDate is after 
	//
	@Override
	public void publishProspective(UserContext usrCtx, Long formId, Date activationDate) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			publishProspective(jdbcDao, usrCtx, formId, activationDate);
		} catch (Exception e) {
			throw new RuntimeException("Error saving container prospectively:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}						
	}
		
	public void publishProspective(JdbcDao jdbcDao, UserContext usrCtx, Long formId, Date activationDate) {
		Container draftContainer = getDraftContainer(formId);
		if(draftContainer!=null)
		{
			nullifyContainerIds(draftContainer);
			
			draftContainer.setName(getFormName(draftContainer.getName(), activationDate));
			Long publishedContainerId = draftContainer.save(usrCtx, jdbcDao, false);
			activationDate = (activationDate == null) ? Calendar.getInstance().getTime() : activationDate;
			
			VersionedContainerInfo vc = new VersionedContainerInfo();
			vc.setFormId(formId);
			vc.setContainerId(publishedContainerId);
			vc.setActivationDate(activationDate);
			vc.setCreatedBy(usrCtx.getUserId());
			vc.setCreationTime(Calendar.getInstance().getTime());
			vc.setStatus("published");
			
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			vdao.insertVersionedContainerInfo(vc);
			
			runPostHooks(usrCtx, formId);
		}
	}
	
	private void runPostHooks(UserContext usrCtx, Long formId) {
		for (FormPublishHook hook : FormPublishHookMgr.getInstance().getPostPublishHooks()) {
			hook.process(usrCtx, formId);
		}
	}

	
	private String getFormName(String formName, Date activationDate) {
		SimpleDateFormat sf = new SimpleDateFormat("MMddyyyy_HHmmss");
		
		StringBuilder qualifiedFormName = new StringBuilder(formName)
			.append("_").append(sf.format(activationDate));
		
		return qualifiedFormName.toString();
	}

	@Override
	public boolean isChangedSinceLastPublish(Long formId) {
		Container draftContainer = getDraftContainer(formId);
		Container publishedContainer = getContainer(formId);
		return ContainerUtility.getChangeLog(draftContainer, publishedContainer).anyChanges();
	}
		
	private void nullifyContainerIds(Container container) {
		if(container!=null){
			container.setId(null);
			for (Container sub : container.getAllSubContainers()) {
				sub.setId(null);
			}
		}
	}

	private List<Long> getPublishedContainerIds(JdbcDao jdbcDao, Long formId) {
		VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
		List<VersionedContainerInfo> containerInfoList = vdao.getPublishedContainersInfo(formId);
		List<Long> publishedIds = new ArrayList<Long>();
		
		for (VersionedContainerInfo containerInfo : containerInfoList) {
			publishedIds.add(containerInfo.getContainerId());
		}
		
		return publishedIds;
	}
	
	public List<VersionedContainerInfo> getPublishedContainerInfo(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			return vdao.getPublishedContainersInfo(formId);
		} catch (Exception e) {
			throw new RuntimeException("Error getting published Container info:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}		
	}
	
	public Date getActivationDate(Long formId) {
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			VersionedContainerDao vdao = new VersionedContainerDao(jdbcDao);
			return vdao.getActivationDateByContainerId(formId);
		} catch (Exception e) {
			throw new RuntimeException("Error getting Activation date:" + formId, e);
		} finally {
			if (this.jdbcDao == null && jdbcDao != null) {
				jdbcDao.close();
			}
		}
	}
	
	
	
	private void applyChangeAndSave(JdbcDao jdbcDao, UserContext usrCtx, Container container, ContainerChangeLog changeLog) {
		applyChange(container, changeLog);
		container.save(usrCtx, jdbcDao, false);
	}
	
	private void applyChange(Container container, ContainerChangeLog changeLog) {
		for (Control newCtrl : changeLog.getAddedCtrls()) {
			container.addControl(newCtrl);
		}		
		
		for (Control deletedCtrl : changeLog.getDeletedCtrls()) {
			if (container.getControl(deletedCtrl.getName()) != null) {				
				container.deleteControl(deletedCtrl.getName());
			}			
		}
		
		for (Control editedCtrl : changeLog.getEditedCtrls()) {
			if (container.getControl(editedCtrl.getName()) != null) {
				container.editControl(editedCtrl.getName(), editedCtrl);
			}
		}
		
		for (Map.Entry<String, ContainerChangeLog> editedSf : changeLog.getEditedSubCtrls().entrySet()) {
			SubFormControl sfCtrl = (SubFormControl)container.getControl(editedSf.getKey());
			if (sfCtrl != null) {
				applyChange(sfCtrl.getSubContainer(), editedSf.getValue());
			}
		}
	}
	
	public List<ContainerInfo> getDraftContainerInfoByCreator(Long creatorId)
	{
		JdbcDao jdbcDao = null;
		try {
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			List localList = getDraftContainerInfoByCreator(jdbcDao, creatorId);
	       return localList;
	     }
	     catch (Exception e)
	     {
	    	 throw new RuntimeException("Error occured while retrieving draft container info for the containers created by :" + creatorId, e);
	     }
	     finally {
	    	 if ((this.jdbcDao == null) && (jdbcDao != null))
	    		 jdbcDao.close(); 
	     }
	   }
	 
	   private List<ContainerInfo> getDraftContainerInfoByCreator(JdbcDao jdbcDao, Long creatorId)
	   {
	     try
	     {
	    	 VersionedContainerDao vDao = new VersionedContainerDao(jdbcDao);
	    	 return vDao.getContainerInfoByCreator(creatorId, "draft"); } catch (Exception e) {
	    		 throw new RuntimeException("Error occured while retrieving draft container info for the containers created by :" + creatorId, e);
	     }
	    
	   }

		public boolean isContainerExpired(Long formId,Timestamp encounterDate) {
			JdbcDao jdbcDao = null;
			jdbcDao = this.jdbcDao != null ? this.jdbcDao : new JdbcDao();
			VersionedContainerDao vDao = new VersionedContainerDao(jdbcDao);
			return vDao.isContainerExpired(formId,encounterDate);
		}
}