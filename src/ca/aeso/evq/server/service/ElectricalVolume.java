package ca.aeso.evq.server.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ElectricalVolume
 * Represents an electrical volume. 
 * Instead of private attributes with getters and setters, this class stores its' values in a list and map 
 * 
 * @author mbodor
 */
public class ElectricalVolume implements Serializable {

	private static final long serialVersionUID = 3713131258099607586L;
	
	private HashMap valueMap = new HashMap();
	private List valueList = new ArrayList();
	
	/**
	 * Add an attribute to this object. The attribute key is added to an ordered List, and the key and value are added to a HashMap  
	 * @param key
	 * @param value
	 */
	public void addValue(String key, Object value) {
		this.valueList.add(key);
		this.valueMap.put(key, value);
	}
	
	public HashMap getValueMap() {
		return valueMap;
	}

	public void setValueMap(HashMap valueMap) {
		this.valueMap = valueMap;
	}

	public List getValueList() {
		return valueList;
	}

	public void setValueList(List valueList) {
		this.valueList = valueList;
	}
	
	public Object get(String key) {
		return this.valueMap.get(key);
	}
}