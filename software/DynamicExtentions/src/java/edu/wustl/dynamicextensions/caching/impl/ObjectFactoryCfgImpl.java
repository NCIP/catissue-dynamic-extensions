package edu.wustl.dynamicextensions.caching.impl;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;

import edu.wustl.dynamicextensions.caching.ObjectFactoryCfg;

public class ObjectFactoryCfgImpl implements ObjectFactoryCfg {
	private static DigesterLoader loader = DigesterLoader.newLoader(new ObjectFactoryCfgRulesModule());
	
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
			Digester digester = loader.newDigester();			 			
			InputStream inputStream = ObjectFactoryCfgImpl.class.getResourceAsStream(OBJECT_FACTORY_CFG);
			return (ObjectFactoryCfg)digester.parse(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing object factory config", e);
		}				
	}
	
	public static class ObjectFactoryCfgRulesModule extends AbstractRulesModule {

		@Override
		protected void configure() {
			forPattern("object-factory-cfg").createObject().ofType(ObjectFactoryCfgImpl.class);
			
			forPattern("object-factory-cfg/exclude-tables/table")			
			    .callMethod("addExcludeTable").withParamCount(1).withParamTypes(String.class)
			    .then().callParam().fromAttribute("name");						
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
