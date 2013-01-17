
package edu.common.dynamicextensions.ui.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.SurveyFormCacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;

public class SurveyModeRenderer extends LayoutRenderer
{

	private SurveyFormCacheManager formCache;

	public SurveyModeRenderer(HttpServletRequest req)
	{
		this.req = req;
		formCache = new SurveyFormCacheManager(req);
	}

	private String renderCategory(String categoryId) throws IOException, NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String categorydiv = "<div><div id='sm-pages'>%s</div></div>";
		String pagediv = "<div class='sm-page' id='%d' style='display:none'>%s</div>";
		StringBuilder pages = new StringBuilder();
		StringBuilder categoryHtml = new StringBuilder(renderHiddenInputs());
		
		
		//ContainerInterface container = (ContainerInterface) formCache.getCategory().getRootCategoryElement().getContainerCollection().iterator().next();
		ContainerInterface container = formCache.getContainer();
		container.setContainerValueMap(formCache.getContainerWithValueMap().getContainerValueMap());
		
		for (Page p : formCache.getPageCollection())
		{
			pages.append(String.format(pagediv, p.getId().longValue(), renderPage(p)));
		}
		
		categoryHtml.append(String.format(categorydiv, pages.toString()));
		return categoryHtml.toString();
	}

	private String renderHiddenInputs() throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String containerIdentifier = "<input type='hidden' id='containerIdentifier' name='containerIdentifier'  value='%d'></input>";
		String categoryName = "<input type='hidden' id='categoryName' value='%s'></input>";
		String controlsCount = "<input type='hidden' id='controlsCount' value='%d'></input>";
		String emptyControlsCount = "<input type='hidden' id='emptyControlsCount' value='%d'></input>";
		String caption = req.getParameter(WebUIManagerConstants.FORM_LABEL);
		int emptyControlsCount2 = formCache.emptyControlsCount();
		String hiddenFields = managePageDisplay();

		ContainerInterface container = formCache.getContainerFromCategory();
		StringBuilder results = new StringBuilder();
		results.append(String.format(containerIdentifier, container.getId()));
		results.append(String.format(categoryName, caption));
		results.append(String.format(controlsCount, formCache.controlsCount()));
		results.append(String.format(emptyControlsCount, emptyControlsCount2));
		results.append(hiddenFields);
		return results.toString();
	}

	/**
	 * Used to manage hidden variables depending request parameters. Later these hidden variables are used for 
	 * managing page display
	 * @return
	 */
	private String managePageDisplay()
	{
		String displayPage = "";
		//If use chose to open survey form, always open the first answered question
		displayPage = "<input type='hidden' id='displayPage' name='displayPage'  value='%d'></input>";
		displayPage = String.format(displayPage, formCache.getDisplayPage());
		return displayPage;
	}

	private String renderPage(Page p) throws DynamicExtensionsSystemException, IOException,
			NumberFormatException, DynamicExtensionsApplicationException
	{
		String pageHtml = "<div class='sm-page-contents'>%s</div>";
		String htmlWrapper = "<table class='sm-page-table'>%s%s</table>";
		String pageTitle = "<tr><th><div class='sm-page-title'>&nbsp;</div></th><th colspan='10'><div class='sm-page-title'>%s</div></th></tr>";
		String emptyDiv = "<div></div>";
		
		if (p == null) {
			return renderError("page not found!");
		} else {
			StringBuilder html = new StringBuilder();
			if (p.getDescription() == null)	{
				pageTitle = emptyDiv;
			} else {
				pageTitle = String.format(pageTitle, p.getDescription());
			}
			List<ControlInterface> controlList = new ArrayList<ControlInterface>(p
					.getControlCollection());
			Collections.sort(controlList);
			Collections.reverse(controlList);
			for (ControlInterface control : controlList)
			{
				control.getParentContainer().getContextParameter().put(DEConstants.CONTEXT_PATH,
						req.getContextPath());
				if (control.getValue() != null)	{
					control.setDataEntryOperation("insertParentData");
				}
				html.append(getControlHTML(control));
			}

			htmlWrapper = String.format(htmlWrapper, pageTitle, html.toString());
			return String.format(pageHtml, htmlWrapper);
		}
	}

	protected String getControlHTML(ControlInterface control)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		StringBuffer controlHTML = new StringBuffer(108);
		controlHTML.append("<tr");
		if (control.getIsSkipLogicTargetControl())
		{
			controlHTML.append(" id='" + control.getHTMLComponentName() + "_row_div' name='"
					+ control.getHTMLComponentName() + "_row_div'");
		}
		if (control.getIsHidden() != null && control.getIsHidden())
		{
			controlHTML.append(" style='display:none'");
		}
		else
		{
			controlHTML.append(" style='display:row'");
		}
		controlHTML.append('>');
		if (control.getIsSkipLogicTargetControl())
		{
			controlHTML
					.append("<input type='hidden' name='skipLogicHideControls' id='skipLogicHideControls' value = '"
							+ control.getHTMLComponentName() + "_row_div' />");
		}

		String htmlWrapper = "<tr><td height='7'></td></tr>%s%s</table></td></tr>";
		if (control.getYPosition() > 1) {
			htmlWrapper = "<tr><td height='7'></td></tr>%s%s</td></tr>";
		}		
		if (control.getIsHidden() != null && control.getIsHidden())
		{
			htmlWrapper =  "%s%s</table></td></tr>";
		}
		control.setAlignment(Control.VERTICAL);
		control.getParentContainer().setRequest(req);
		return String.format(htmlWrapper, controlHTML.toString(), control.generateHTML(control
				.getParentContainer()));
	}

	private String renderError(String message)
	{
		return message;
	}

	public String render() throws DynamicExtensionsSystemException, IOException,
			NumberFormatException, DynamicExtensionsApplicationException
	{
		String categoryId = formCache.getCategoryId();
		String responseString="";

		if (categoryId != null)
		{
			responseString = renderCategory(categoryId);
		} else {
			responseString = renderError("categoryId cannot be null!");
		}
		return responseString;
	}
}
