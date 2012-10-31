/**
 * 
 */
package edu.common.dynamicextensions.domain;

import java.sql.Clob;

/**
 * @author Vinayak Pawar
 *
 */
public class FormAuditEvent {
	private Long identifier;
	
	private String formName;
	
	private Long recordId;
	
	private Clob formDataXml;

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Clob getFormDataXml() {
		return formDataXml;
	}

	public void setFormDataXml(Clob formDataXml) {
		this.formDataXml = formDataXml;
	}
	
	

}
