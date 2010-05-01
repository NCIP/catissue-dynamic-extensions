
package edu.wustl.cab2b.client.ui.util;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * All the constants used at UI side
 * 
 * @author Chandrakant Talele
 */
public interface ClientConstants
{

	String OPERATOR_AND = "AND";

	String OPERATOR_OR = "OR";

	/** Resource bundle name for getting error codes and its description. */
	String ERROR_CODE_FILE_NAME = "errorcodes";

	/** Resource bundle name for getting externalized strings. */
	String APPLICATION_RESOURCES_FILE_NAME = "Cab2bApplicationResources";

	/**
	 * caB2B image logo
	 */
	String CAB2B_LOGO_IMAGE = "CaB2B_16_16_bg.gif";

	/**
	 * Popular category image
	 */
	String POPULAR_CATEGORIES_IMAGE = "popular_categories.gif";

	/**
	 * My Search query image
	 */
	String MY_SEARCH_QUERIES_IMAGE = "my_search.gif";

	/**
	 * My Experiment image 
	 */
	String MY_EXPERIMENT_IMAGE = "my_experiments.gif";

	/**
	 * My category Image
	 */
	String MY_CATEGORIES_IMAGE = "my_categories.gif";

	/**
	 * Datails column image
	 */
	String DETAILS_COLUMN_IMAGE = "popularcategories_icon.gif";

	/**
	 * Add limit panel connect node selected image
	 */
	String LIMIT_CONNECT_SELECTED = "limit_set_rollover.gif";

	/**
	 *  Add limit panel connect node unselected image
	 */
	String LIMIT_CONNECT_DESELECTED = "limit_set.gif";

	/**
	 * Add limit panel select cursor icon image
	 */
	String SELECT_ICON_ADD_LIMIT = "select_icon.gif";

	/**
	 * Add limit panel cursor icon mouseover image
	 */
	String SELECT_ICON_ADD_LIMIT_MOUSEOVER = "select_icon_mo.gif";

	/**
	 * Add limit panel parenthesis icon image
	 */
	String PARENTHISIS_ICON_ADD_LIMIT = "parenthesis_icon.gif";

	/**
	 * Add limit panel parenthesis icon mouseover image
	 */
	String PARENTHISIS_ICON_ADD_LIMIT_MOUSEOVER = "parenthesis_icon_mo.gif";

	/**
	 * Add limit panel papergrid image
	 */
	String PAPER_GRID_ADD_LIMIT = "paper_grid.png";

	/**
	 * Add limit panel port image
	 */
	String PORT_IMAGE_ADD_LIMIT = "port.gif";

	/**
	 * Experiment / DataList tree open folder image
	 */
	String TREE_OPEN_FOLDER = "open_folder.gif";

	/**
	 * Experiment / DataList tree close folder image
	 */
	String TREE_CLOSE_FOLDER = "close_folder.gif";

	/**
	 * Visualize data image
	 */
	String VISUALIZE_DATA = "visualize_data.gif";

	/**
	 * Analyze data image
	 */
	String ANALYZE_DATA = "analyze_data.gif";

	/**
	 * Filter data image
	 */
	String FILTER_DATA = "filter_data.gif";

	/**
	 *  Charts image icon
	 */
	String CHARTS = "charts.gif";

	/**
	 * Bar graph image icon
	 */
	String BAR_GRAPH = "graph_bar.gif";

	/**
	 * Line Graph image icon
	 */
	String LINE_GRAPH = "graph_line.gif";

	/**
	 * Scatter Graph image icon
	 */
	String SCATTER_GRAPH = "graph_scatter.gif";

	/**
	 * Heat map image icon
	 */
	String HEAT_MAP = "heatmap.jpg";

	/**
	 * Gene Annotation image icon
	 */
	String GENE_ANNOTATION = "Gene_Annotation.gif";

	/**
	 * Home tab unpressed icon
	 */
	String HOME_TAB_UNPRESSED = "home_tab.gif";

	/**
	 * Home tab unpressed icon
	 */
	String HOME_TAB_PRESSED = "home_MO_tab.gif";

	/**
	 * Search tab unpressed icon
	 */
	String SEARCH_TAB_UNPRESSED = "searchdata_tab.gif";

	/**
	 * Search tab pressed icon
	 */
	String SEARCH_TAB_PRESSED = "searchdata_MO_tab.gif";

	/**
	 * Experiment tab unpressed icon
	 */
	String EXPT_TAB_UNPRESSED = "experiment_tab.gif";

	/**
	 * Experiment tab pressed icon
	 */
	String EXPT_TAB_PRESSED = "experiment_MO_tab.gif";

	/**
	 * Tree leaf node icon
	 */
	String TREE_LEAF_NODE = "data_14_14.gif";

	/**
	 * Experiment leaf node icon
	 */
	String EXPT_LEAF_NODE = "my_experiments_data.gif";

	/**
	 * Select data category icon
	 */
	String SELECT_DATA_CATEGORY = "select_data_category.gif";

	/**
	 * Default border for client
	 */
	Border border = BorderFactory.createLineBorder(Color.BLACK);

	/**
	 * Model map property file name
	 */
	String MODEL_MAP_PROPERTY_FILE = "modelMap";

	/**
	 * Bar chart string name
	 */
	String BAR_CHART = "Bar Chart";

	/**
	 * Line chart string name
	 */
	String LINE_CHART = "Line Chart";

	/**
	 * Scatter chart string name
	 */
	String SCATTER_PLOT = "Scatter Plot";

	/**
	 * chart string name
	 */
	String CHART = "Chart";

	/**
	 * Heat map string name
	 */
	String HEATMAP = "Heatmap";

	/**
	 * Logout button icon 
	 */
	String LOGOUT_ICON = "logout_icon.gif";

	/**
	 * MySettings Page Service URL Link
	 */
	String MENU_CLICK_EVENT = "menuClicked";

	String SERVICE_URL = "Service URLs";

	String SEARCH_EVENT = "searchEvent";

	String BACK_EVENT = "backEvent";

	String UPDATE_EVENT = "updateEvent";

	String SERVICE_SELECT_EVENT = "serviceSelected";

	String UPDATE_QUERYURLS_EVENT = "updateQueryURLS";

	String DIALOG_CLOSE_EVENT = "dialogCloseEvent";
}
