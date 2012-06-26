package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.userinterface.DynamicExtensionLayoutInterface;

public abstract class DynamicExtensionLayout extends
		DynamicExtensionBaseDomainObject implements Serializable,
		DynamicExtensionLayoutInterface {

	private static final long serialVersionUID = 5272289464183382826L;

	@Override
	public Long getId() {
		return id;
	}
}