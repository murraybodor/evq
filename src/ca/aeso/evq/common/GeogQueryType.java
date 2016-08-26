package ca.aeso.evq.common;

import java.util.HashMap;

/**
 * Represents a type of geographic query
 * @author mbodor
 *
 */
public class GeogQueryType {

	public static HashMap GEOG_QUERY_TYPES = new HashMap();

	private String value;
	private String description;
	
	private GeogQueryType(String value, String desc) {
		this.value = value;
		this.description = desc;
		GEOG_QUERY_TYPES.put(value, desc);
	}
	
	public static final GeogQueryType GEOG_QUERY_TYPE_ENTIRE_SYSTEM = new GeogQueryType("ES", "Entire System");
	public static final GeogQueryType GEOG_QUERY_TYPE_REGION = new GeogQueryType("RG", "Region");
	public static final GeogQueryType GEOG_QUERY_TYPE_AREA = new GeogQueryType("PA", "Planning Area");
	public static final GeogQueryType GEOG_QUERY_TYPE_SUBSTATION = new GeogQueryType("SB", "Substation");
	public static final GeogQueryType GEOG_QUERY_TYPE_MP = new GeogQueryType("MP", "Meas. Point");
	public static final GeogQueryType GEOG_QUERY_TYPE_IMPORTS_EXPORTS = new GeogQueryType("IE", "Imports/Exports");

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public static String getDescription(String type) {
		return (String)GEOG_QUERY_TYPES.get(type);
	}
	
	public boolean equals(String aQueryType) {
		if (aQueryType.equals(this.getValue()))
			return true;
		else
			return false;
	}
	
}
