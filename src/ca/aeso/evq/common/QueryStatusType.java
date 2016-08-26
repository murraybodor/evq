package ca.aeso.evq.common;

import java.util.HashMap;

/**
 * Represents a type of Query status
 * @author mbodor
 */
public class QueryStatusType {

	public static HashMap QUERY_STATUS_TYPES = new HashMap();

	private String value;
	private String description;
	
	private QueryStatusType(String value, String desc) {
		this.value = value;
		this.description = desc;
		QUERY_STATUS_TYPES.put(value, desc);
	}
	
	public static final QueryStatusType STATUS_EXECUTING = new QueryStatusType("E", "Executing");
	public static final QueryStatusType STATUS_COMPLETE = new QueryStatusType("C", "Complete");
	public static final QueryStatusType STATUS_FAILED = new QueryStatusType("F", "Failed");
	public static final QueryStatusType STATUS_REMOVED = new QueryStatusType("R", "Removed");
	public static final QueryStatusType STATUS_NO_RESULTS = new QueryStatusType("N", "No Results");

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
