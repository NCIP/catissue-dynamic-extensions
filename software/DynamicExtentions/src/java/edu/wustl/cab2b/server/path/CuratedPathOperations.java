
package edu.wustl.cab2b.server.path;

import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.metadata.path.CuratedPath;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;

/**
 * Class for C R U D operations on curated path
 * @author atul_jawale
 * @author chetan_patil
 */
public class CuratedPathOperations extends DefaultBizLogic
{

	//TODO need to take care of post processing of PATH similar to Curated path, Path should also be populated
	/**
	 * This method returns the CuratedPath for the given curated path identifier
	 * @param identifier identifier of the curated path
	 * @return curated path
	 */
	public ICuratedPath getCuratePathById(Long identifier)
	{
		try
		{
			List<?> curatePathList = retrieve(CuratedPath.class.getName(), "id", identifier);
			return (ICuratedPath) curatePathList.get(0);
		}
		catch (BizLogicException e)
		{
			throw new RuntimeException("Unable to retrieve object, Exception:" + e.getMessage(),e);
		}
	}

	/**
	 * This method retrieves all the CuratedPath objects in the system.
	 * Returns all the curated path available in the system.
	 * @return List of all curated paths.
	 */
	@SuppressWarnings("unchecked")
	public List<ICuratedPath> getAllCuratedPath()
	{
		try
		{
			return retrieve(CuratedPath.class.getName());
		}
		catch (BizLogicException e)
		{
			throw new RuntimeException("Unable to retrieve object, Exception:" + e.getMessage(),e);
		}
	}

	/**
	 * This method persists the CuratedPath object
	 * @param curatedPath
	 */
	public final void saveCuratedPath(CuratedPath curatedPath)
	{
		try
		{
			insert(curatedPath);
		}
		catch (BizLogicException e)
		{
			throw new RuntimeException("Unable to save curatedPath, Exception:" + e.getMessage(),e);
		}
	}

	/**
	 * This method returns the Path object for the given path identifier
	 * @param identifier identifier of the path
	 * @return path object
	 */
	public PathInterface getPathById(Long identifier)
	{
		try
		{
			List<?> pathList = retrieve(edu.common.dynamicextensions.domain.Path.class.getName(), "id", identifier);
			return (PathInterface) pathList.get(0);
		}
		catch (BizLogicException e)
		{
			throw new RuntimeException("Unable to retrieve object, Exception:" + e.getMessage(),e);
		}
	}

	/**
	 * Checks whether path is duplicate or not.
	 * @param curatedPath
	 * @return Result whether this path is duplicate or not
	 */
	public boolean isDuplicate(ICuratedPath curatedPath)
	{
		boolean isDuplicate = false;

		Set<ICuratedPath> curatedPaths = PathFinder.getInstance().autoConnect(
				curatedPath.getEntitySet());
		for (ICuratedPath path : curatedPaths)
		{
			if (curatedPath.equals(path))
			{
				isDuplicate = true;
				break;
			}
		}

		return isDuplicate;
	}
}