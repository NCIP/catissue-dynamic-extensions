package edu.common.dynamicextensions.ui.renderer;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.ui.interfaces.LayoutRendererInterface;

public abstract class LayoutRenderer implements LayoutRendererInterface {
	
	protected HttpServletRequest req;
	
	public HttpServletRequest getRequest() {
		return this.req;
	}
	
	}
