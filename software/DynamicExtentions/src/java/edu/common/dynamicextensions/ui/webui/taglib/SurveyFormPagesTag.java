/**
 * 
 */
package edu.common.dynamicextensions.ui.webui.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.ui.renderer.SurveyModeRenderer;

/**
 * @author Vinayak Pawar
 *
 */
public class SurveyFormPagesTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5789430389356049678L;
	
	@Override
	public int doEndTag()
	throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		SurveyModeRenderer render = new SurveyModeRenderer(request);
		try {
			pageContext.getOut().append(render.render());
		} catch (Exception e) {
			throw new RuntimeException("Error rendering survey pages", e);
		}		
		return super.doEndTag();
	}
}
