package edu.wustl.dynamicextensions.caching.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import edu.wustl.dynamicextensions.caching.ObjectFactoryCfg;

public class ObjectFactoryCfgImpl implements ObjectFactoryCfg {
	private final static String OBJECT_FACTORY_CFG_RULES = "/objectFactoryCfgRules.xml";
	
	private final static String OBJECT_FACTORY_CFG = "/objectFactoryCfg.xml";
	
	private Set<String> excludeTableSet = new HashSet<String>();
	
	
	public void addExcludeTable(String tableName) {
		excludeTableSet.add(tableName.toUpperCase());
	}
	
	public Set<String> getExcludeTableSet() {
		return excludeTableSet;
	}
	
	public static ObjectFactoryCfg getObjectFactoryCfg() {
		try {
			URL rulesUrl = ObjectFactoryCfgImpl.class.getResource(OBJECT_FACTORY_CFG_RULES);
			Digester digester = DigesterLoader.createDigester(rulesUrl);			 			
			InputStream inputStream = ObjectFactoryCfgImpl.class.getResourceAsStream(OBJECT_FACTORY_CFG);
			return (ObjectFactoryCfg)digester.parse(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing object factory config", e);
		}				
	}
	
	public static void main(String[] args) {
		ObjectFactoryCfg cfg = ObjectFactoryCfgImpl.getObjectFactoryCfg();
		System.err.println(cfg.getExcludeTableSet().size());
		
		for (String table : cfg.getExcludeTableSet()) {
			System.err.println("Table name is " + table);
		}
	}
}
