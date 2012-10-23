
package edu.common.dynamicextensions.ui.webui.util;

public interface WebUIManagerConstants
{

	/**
	 *
	 */
	String CREATE_CONTAINER_URL = "/DisplayContainer.do";

	/**
	 *
	 */
	String MODE_PARAM_NAME = "mode";
	/**
	 *
	 */
	String CALLBACK_URL_PARAM_NAME = "callbackURL";
	/**
	 *
	 */
	String DYNAMIC_EXTENSIONS_INTERFACE_ACTION_URL = "/DynamicExtensionsInterfaceAction.de";
	/**
	 *
	 */
	String DYNAMIC_EXTENSIONS_INTERFACE_ACTION_PARAM_NAME = "operation";
	/**
	 *
	 */
	String LOAD_DATA_ENTRY_FORM_ACTION_URL = "/LoadDataEntryFormAction.de?dataEntryOperation=insertParentData";
	
	String FORM_SUMMARY_PAGE = "summaryPage";
	/**
	 *
	 */
	String CONATINER_IDENTIFIER_PARAMETER_NAME = "containerIdentifier";
	/**
	 *
	 */
	String RECORD_IDENTIFIER_PARAMETER_NAME = "recordIdentifier";
	/**
	 *
	 */
	String IS_SHOW_TEMPLATE_RECORD = "isShowTemplateRecord";
	/**
	 *
	 */
	String OPERATION_STATUS = "operationStatus";
	/**
	 *
	 */
	String SUCCESS = "success";
	/**
	 *
	 */
	String CANCELLED = "cancelled";
	/**
	 *
	 */
	String DELETED = "deleted";

	/**
	 *
	 */
	String OPERATION_STATUS_PARAMETER_NAME = "operationStatus";

	/**
	 *
	 */
	String CONTAINER_NAME = "containerName";
	/**
	 *
	 */
	String CONTAINER_IDENTIFIER = "containerIdentifier";
	/**
	 *
	 */
	String GET_ALL_CONTAINERS = "getAllContainers";
	/**
	 *
	 */
	String EDIT_MODE = "edit";
	/**
	 *
	 */
	String VIEW_MODE = "view";

	/**
	 *
	 */
	String USER_ID = "userId";
	/**
	 *
	 */
	String DELETED_ASSOCIATION_IDS = "deletedAssociationIds";

	String CACHE_ERROR = "cacheError";

	String CONTAINER_MAP = "containerMap";
	public final String DATA_VALUE_MAP = "dataValueMap";
	public final String CONTAINER = "container";
	public final String SESSION_DATA_BEAN="sessionDataBean";
	public final String RECORD_ID="recordId";
	public final String ENTITY = "entity";
	public final String ATTRVSVALUES="attrVsValues";
	public final String DYNAMIC_OBJECT_ID = "dynamicObjectId";
	public final String OLD_DYNAMIC_OBJECT = "oldDynamicObject";
	public final String STATIC_OBJECT_ID = "staticObjectId";
	public final String OLD_STATIC_OBJECT = "oldStaticObject";
	public final String ASSOCIATION = "association";

	public final String  ENTITY_GROUP = "entityGroupName";

	public final String  CATEGORY = "category";

	public final String PACKAGE_NAME = "packageName";
	public final String LOCK_FORMS = "lockForms";
	public final String RELEASE_FORMS= "releaseForms";
	public final String OPERATION= "operation";

	public final String UPDATE_CACHE = "updateCache";
	public static final String FILE_RECORD_QUERY_LIST = "fileRecordQueryList";
	public static final String DYNAMIC_EXTENSIONS="dynamicExtensions";
	public final String UPDATECACHE = "UpdateCache.de";
	
	public final String ISDRAFT = "isDraft";
	public final String IS_FORM_LOCKED = "isFormLocked";
	public final String FORM_LABEL = "formLabel";
	String OVERRIDE_CAPTION = "OverrideCaption";

	public static String IMPORT_PV_ACTION = "/ImportPVAction.de?";

	public static String DOWNLOAD_PV_VERSION_ACTION = "/DownloadPVVersionAction.de";

	public static String CREATE_CATEGORY_METADATA_ACTION = "/CreateCategoryMetadataAction.de";

	public static String CREATE_CATEGORY_ACTION = "/CreateCategoryAction.de";

	public static final String SURVEY_MODE_JSP = "surveyModePage";
	
	public static final String DE_AJAX_HANDLER = "DE_AJAX_HANDLER";
	public static final String AJAX_OPERATION = "ajaxOperation";
	public static final String DE_COMBO_DATA_ACTION = "DEComboDataAction";

}
