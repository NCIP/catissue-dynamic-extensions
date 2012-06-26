package edu.common.dynamicextensions.domain.userinterface;

import java.util.Collection;
import java.util.LinkedHashSet;

public class SurveyModeLayout extends DynamicExtensionLayout {

	private static final long serialVersionUID = 6460804427946477366L;
	private Collection<Page> pageCollection = new LinkedHashSet<Page>();

	public Collection<Page> getPageCollection() {
		return this.pageCollection;
	}

	public void setPageCollection(final Collection<Page> pageCollection) {
		this.pageCollection = pageCollection;
	}
}