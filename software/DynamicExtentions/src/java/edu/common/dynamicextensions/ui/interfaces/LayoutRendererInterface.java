package edu.common.dynamicextensions.ui.interfaces;

import java.io.IOException;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public interface LayoutRendererInterface {
	public String render() throws DynamicExtensionsSystemException, IOException, NumberFormatException, DynamicExtensionsApplicationException;
}
