/**
 * 
 */
package edu.common.dynamicextensions.ui.webui.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.action.core.DEAjaxActionManager;

//
// This tag has been introduced solely to improve survey form load time
// 

/**
 * @author Vinayak Pawar
 *
 */
public class ServerStateHtmlGeneratorTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983134338193580031L;

	@Override
	public int doEndTag()
	throws JspException {
		try {
			DEAjaxActionManager ajaxManager = new DEAjaxActionManager();
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			pageContext.getOut().append(ajaxManager.updateServerStateGenerateHtml(request));		 
		} catch (Exception e) {
			throw new RuntimeException("Error generating server state html", e);
		}
		
		return super.doEndTag();
	}	
}
