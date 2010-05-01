
package edu.wustl.cab2b.common.errorcodes;

/**
 * This interface contains the constants for the error codes to be used in the code throughout the application. Any
 * error code constant added here should have a corresponding entry in the errorcodes.properties file.
 * 
 * @author gautam_shetty
 */
public interface ErrorCodeConstants
{

	/** User fetched with incomplete data. */
	// String UR_0001 = "UR.0001";
	/** User already exists */
	String UR_0002 = "UR.0002";

	/** Error while fetching user from database */
	String UR_0003 = "UR.0003";

	/** Error while inserting user in database */
	String UR_0004 = "UR.0004";

	/** Error while updating user information in database */
	String UR_0005 = "UR.0005";

	/** Please recheck identity provider url */
	String UR_0006 = "UR.0006";

	/** Unable to authenticate: Invalid credentials */
	String UR_0007 = "UR.0007";

	/** Please recheck dorian url */
	String UR_0008 = "UR.0008";

	/** Please check the credentials again (User name is case sensitive) */
	String UR_0009 = "UR.0009";

	//---------------------------------------------------------------------
	/** Unable to parse domain model XML file. */
	String GR_0001 = "GR.0001";

	//---------------------------------------------------------------------
	/** Unable to persist Entity Group in Dynamic Extension. */
	String DE_0001 = "DE.0001";

	/** Unable to persist Entity in Dynamic Extension. */
	String DE_0002 = "DE.0002";

	/** Inconsistent data in database */
	String DE_0003 = "DE.0003";

	/** Unable to retrieve Dynamic Extension objects */
	String DE_0004 = "DE.0004";

	//---------------------------------------------------------------------
	/** Database down. */
	String DB_0001 = "DB.0001";

	/** Unable to create a connection from datasource */
	String DB_0002 = "DB.0002";

	/** Exception while firing Parameterized query.* */
	String DB_0003 = "DB.0003";

	/** Exception while firing Update query.* */
	String DB_0004 = "DB.0004";

	/** Exception while editing saved query.* */
	String DB_0005 = "DB.0005";

	/** Error while loading path information into database.* */
	String DB_0006 = "DB.0006";

	/** Error while no service url available into database.* */
	String DB_0007 = "DB.0007";

	// ---------------------------------------------------------------------
	/** Cab2b server down. */
	String SR_0001 = "SR.0001";

	//---------------------------------------------------------------------
	/** File operation failed */
	String IO_0001 = "IO.0001";

	/** Can't find resource bundle. */
	String IO_0002 = "IO.0002";

	/** XML parse error. */
	String IO_0003 = "IO.0003";

	//---------------------------------------------------------------------
	/** Java Reflection API Error. */
	String RF_0001 = "RF.0001";

	//---------------------------------------------------------------------
	/** Unknown Error in the Application (Can be used for app. development). */
	String UN_XXXX = "UN.XXXX";

	//---------------------------------------------------------------------
	/** Unable to look up resource from JNDI */
	String JN_0001 = "JN.0001";

	/** Invalid input query object */
	String QUERY_INVALID_INPUT = "QM.0001";

	/** Critical error encountered when accessing the caGrid infrastructure.\nPlease report this to the administrator */
	//String QUERY_EXECUTION_ERROR = "QM.0002";
	/** Error occurred while saving query.\nPlease report this to the administrator */
	//String QUERY_SAVE_ERROR = "QM.0005";
	/** Error occurred while retrieving query.\nPlease report this to the administrator */
	//String QUERY_RETRIEVE_ERROR = "QM.0006";
	/** Error while saving category */
	String CATEGORY_SAVE_ERROR = "CT.0001";

	/** Error while retrieving category */
	String CATEGORY_RETRIEVE_ERROR = "CT.0002";

	/** Can not create Custom Data Categories as Data List contains Admin defined categories */
	String CUSTOM_CATEGORY_ERROR = "CT.0003";

	/** Error while saving data list */
	String DATALIST_SAVE_ERROR = "DL.0001";

	/** Error while retrieving data list */
	String DATALIST_RETRIEVE_ERROR = "DL.0002";

	/** Error while saving data category */
	String DATACATEGORY_SAVE_ERROR = "DC.001";

	/** Please connect all nodes before proceed */
	String QM_0003 = "QM.0003";

	/** Error occurred while querying URL: */
	String QM_0004 = "QM.0004";

	/** Fatal error occurred while launching caB2B client.\nPlease contact administrator */
	//     String CA_0001 = "CA.0001";
	/** Search string cannot be null */
	//     String CA_0007 = "CA.0007";
	/**
	 * Error Regarding CDS service
	 */
	/** Credential delegation failed */
	//String CDS_001 = "CDS.001";
	/** Delegated Credential's serialization failed */
	//     String CDS_002 = "CDS.002";
	/** Unable to copy CA certificates to [user.home]/.globus */
	String CDS_003 = "CDS.003";

	/** Error occurred while generating globus certificates */
	String CDS_004 = "CDS.004";

	/** Unable to serialize the delegated credentials */
	String CDS_005 = "CDS.005";

	/** An unknown internal error occurred at CDS while delegating the credentials */
	String CDS_006 = "CDS.006";

	/** Error occurred while delegating the credentials */
	String CDS_007 = "CDS.007";

	/** The server doesn't have permission to access the client's credentials */
	String CDS_008 = "CDS.008";

	/** Incorrect CDS URL. Please check the CDS URL in conf/client.properties */
	String CDS_009 = "CDS.009";

	/** Please check the dorian URL */
	String CDS_010 = "CDS.010";

	/** Error occurred at Dorian while obtaining GlobusCredential */
	String CDS_011 = "CDS.011";

	/** Invalid SAMLAssertion. Please check the Dorian URL and user's credentials. */
	String CDS_012 = "CDS.012";

	/** Error occurred due to invalid proxy. */
	String CDS_013 = "CDS.013";

	/** Incorrect user policy set for the proxy. */
	String CDS_014 = "CDS.014";

	/** You have insufficient permissions. Please contact Dorian Administrator. */
	String CDS_015 = "CDS.015";

	/** Could not find CA certificates */
	String CDS_016 = "CDS.016";

	/** Unable to delegate the credential to CDS */
	String CDS_017 = "CDS.017";

	/** Unable to generate GlobusCredential */
	String CDS_018 = "CDS.018";

	/** Unable to authenticate the user */
	String CDS_019 = "CDS.019";

	/** Unable to create the authentication client */
	String CDS_020 = "CDS.020";

	/* Experiment related error codes */
	/** Error occurred while saving Experiment */
	//String EX_001 = "EX.001";
	/** Error occurred while retrieving Experiment */
	String EX_002 = "EX.002";

	/** Error occurred while saving ExperimentGroup */
	//String EX_003 = "EX.003";
	/** Error occurred while retrieving ExperimentGroup */
	String EX_004 = "EX.004";

}
