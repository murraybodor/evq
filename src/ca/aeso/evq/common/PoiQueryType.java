package ca.aeso.evq.common;

import java.util.HashMap;

/**
 * Represents a type of Point of Interest query
 * @author mbodor
 *
 */
public class PoiQueryType {

	public static HashMap POI_QUERY_TYPES = new HashMap();

	private String value;
	private String description;
	
	private PoiQueryType(String value, String desc) {
		this.value = value;
		this.description = desc;
		POI_QUERY_TYPES.put(value, desc);
	}
	
	public static final PoiQueryType POI_QUERY_TYPE_PERCENTILE = new PoiQueryType("PE", "Percentile");
	public static final PoiQueryType POI_QUERY_TYPE_HOURLY = new PoiQueryType("HO", "Hourly");

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
	
	public boolean equals(String aQueryType) {
		if (aQueryType.equals(this.getValue()))
			return true;
		else
			return false;
	}
	
}
